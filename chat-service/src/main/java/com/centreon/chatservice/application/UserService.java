package com.centreon.chatservice.application;

import java.time.OffsetDateTime;
import java.util.Optional;

import com.centreon.chatservice.application.adapters.IdGenAdapter;
import com.centreon.chatservice.application.adapters.TokenAdapter;
import com.centreon.chatservice.domain.event.Event;
import com.centreon.chatservice.domain.event.EventRepository;
import com.centreon.chatservice.domain.exception.BusinessException;
import com.centreon.chatservice.domain.user.User;
import com.centreon.chatservice.domain.user.UserRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * User service.
 *
 * @author ghazi
 */
@Service
public class UserService
{
	private final IdGenAdapter idGenAdapter;
	private final TokenAdapter tokenAdapter;
	private final UserRepository userRepository;
	private final EventRepository eventRepository;
	private final PasswordEncoder passwordEncoder;

	/**
	 * The User service constructor.
	 *
	 * @param idGenAdapter the idgen adapter
	 * @param tokenAdapter the token adapter
	 * @param userRepository the user repository
	 * @param passwordEncoder the password encoder
	 */
	public UserService(
		IdGenAdapter idGenAdapter,
		TokenAdapter tokenAdapter,
		UserRepository userRepository,
		EventRepository eventRepository,
		PasswordEncoder passwordEncoder)
	{
		this.idGenAdapter = idGenAdapter;
		this.tokenAdapter = tokenAdapter;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.eventRepository = eventRepository;
	}

	/**
	 * Creates new user.
	 *
	 * @param email the user's email
	 * @param pseudo the user's pseudo
	 * @param name the user's name. can be {@code null}
	 * @param password the user's password. can be {@code null}
	 * @return the created user resource
	 * @throws BusinessException if the pseudo is already taken by another user
	 */
	public UserResource create(String email, String pseudo, String name, String password)
	{
		try {
			OffsetDateTime now = OffsetDateTime.now();
			User user = userRepository.insert(new User(idGenAdapter.nextId(), pseudo, email,
				name != null ? name : "",
				password != null ? passwordEncoder.encode(password) : null, now));
			eventRepository.insert(
					new Event(idGenAdapter.nextId(), user.getId(), now, Event.EventType.USER_REGISTRED))
				.block();

			return new UserResource(user);
		} catch (DuplicateKeyException e) {
			throw new BusinessException("user.pseudo.duplicated",
				"the user pseudo '" + pseudo + "' is already taken try with a new one");
		}
	}

	/**
	 * Find and search users.
	 *
	 * @param search user's text search
	 * @param page the requested page
	 * @param size requested page size
	 * @return page of users
	 */
	public Page<UserResource> list(String search, int page, int size)
	{
		return userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(search,
				search, PageRequest.of(page, size, Sort.by("id").descending()))
			.map(UserResource::new);
	}

	/**
	 * request user authentication.
	 *
	 * @param pseudo the user's pseudo
	 * @param password the user's password
	 * @return the authentication resource contenting pseudo and password
	 */
	public UserAuthenticationResource authenticate(String pseudo, String password)
	{
		return userRepository.findByPseudo(pseudo)
			.filter(user -> this.passwordEncoder.matches(password, user.getPassword()))
			.map(user -> UserAuthenticationResource.success(user, token(user)))
			.orElse(UserAuthenticationResource.failed());
	}

	/**
	 * check token authentication.
	 *
	 * @param token the token
	 */
	public Optional<UserPrincipal> checkAuthentication(String token)
	{
		String userId = tokenAdapter.validate(token);
		if (userId != null) {
			return userRepository.findById(Long.valueOf(userId))
				.map(UserPrincipal::new);
		}
		return Optional.empty();
	}


	/**
	 * create jwt token from user
	 */
	private String token(User user)
	{
		return tokenAdapter.generate("" + user.getId());
	}
}
