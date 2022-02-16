package com.centreon.chatservice.presentation.rest.user;

import javax.validation.Valid;

import com.centreon.chatservice.application.UserAuthenticationResource;
import com.centreon.chatservice.application.UserResource;
import com.centreon.chatservice.application.UserService;
import com.centreon.chatservice.presentation.rest.PageResponse;
import com.centreon.chatservice.presentation.rest.PagingRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * User restfull controller.
 *
 * @author ghazi
 */
@RestController
@RequestMapping("/users")
public class UserController
{
	private static final Logger LOGGER = LogManager.getLogger();

	private final UserService userService;

	/**
	 * UserController constructor.
	 *
	 * @param userService the user service
	 */
	public UserController(UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * Creates a new user.
	 *
	 * @param request the user creation request.
	 * @return the created user resource.
	 */
	@PostMapping
	public UserResource create(@RequestBody @Valid UserCreationRequest request)
	{
		LOGGER.debug("request user creation {}", request);
		UserResource resource = this.userService.create(request.getEmail(), request.getPseudo(),
			request.getName(), request.getPassword());
		LOGGER.info("user created {}", resource);
		return resource;
	}

	/**
	 * Retrieves users list.
	 *
	 * @param pagingRequest the paging request.
	 * @param search optional search text. will be used to search for the user's name and email address.
	 * <p> if search is present, returns all users whose name or email contains the searched text
	 * @return users page.
	 */
	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public PageResponse<UserResource> list(@RequestParam (required = false, defaultValue = "") String search,
		PagingRequest pagingRequest)
	{
		LOGGER.debug("pagingRequest users list {}", pagingRequest);
		Page<UserResource> resource = this.userService.list(search, pagingRequest.getPage(), pagingRequest.getSize());
		LOGGER.info("user list retrieved {}", resource);
		return new PageResponse<>(resource.getContent(), resource.getTotalElements(),
			resource.getTotalPages());
	}

	/**
	 * request user authentication.
	 *
	 * @param request the authentication request
	 * @return the authentication resource contenting pseudo and password
	 */
	@PostMapping("/authenticate")
	public UserAuthenticationResource authenticate(@RequestBody @Valid UserAuthenticationRequest request)
	{
		LOGGER.debug("authentication request pseudo {}", request.getPseudo());
		UserAuthenticationResource aut = this.userService.authenticate(request.getPseudo(), request.getPassword());
		LOGGER.info("user authenticated successfully pseudo {}", request.getPseudo());
		return aut;
	}
}
