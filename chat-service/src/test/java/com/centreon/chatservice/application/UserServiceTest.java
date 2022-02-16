package com.centreon.chatservice.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.centreon.chatservice.application.adapters.IdGenAdapter;
import com.centreon.chatservice.application.adapters.TokenAdapter;
import com.centreon.chatservice.domain.event.EventRepository;
import com.centreon.chatservice.domain.exception.BusinessException;
import com.centreon.chatservice.domain.user.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;

/**
 * @author ghazi
 */
@RunWith(SpringRunner.class)
public class UserServiceTest
{
	private UserService userService;
	@MockBean
	private IdGenAdapter idGenAdapter;
	@MockBean
	private TokenAdapter tokenAdapter;
	@MockBean
	private UserRepository userRepository;
	@MockBean
	private EventRepository eventRepository;
	@MockBean
	private PasswordEncoder passwordEncoder;

	@Before
	public void setUp()
	{
		when(idGenAdapter.nextId()).thenReturn(1L);
		when(userRepository.insert(any())).thenAnswer(i -> i.getArguments()[0]);
		when(eventRepository.insert(any())).thenAnswer(i -> Mono.just(i.getArguments()[0]));
		this.userService = new UserService(idGenAdapter, tokenAdapter, userRepository,
			eventRepository, passwordEncoder);
	}

	@Test
	public void createShouldReturnCreatedUserResource()
	{
		//given
		String email = "test@test.com";
		String pseud = "pseudo";
		String name = "name";
		String password = "password";

		//when
		UserResource resource = userService.create(email, pseud, name, password);

		//then
		Assert.assertEquals("should return id", Long.valueOf(1L), resource.getId());
		Assert.assertEquals("should return email", email, resource.getEmail());
		Assert.assertEquals("should return name", name, resource.getName());
		// event & user
		verify(idGenAdapter, times(2)).nextId();
		verify(userRepository, times(1)).insert(any());
		verify(eventRepository, times(1)).insert(any());
		verify(passwordEncoder, times(1)).encode(password);
		verifyNoMoreInteractions(idGenAdapter, tokenAdapter, userRepository, eventRepository, passwordEncoder);
	}

	@Test
	public void createShouldThrowBusinessExceptionIfPseudoIsDuplicated()
	{
		//given
		String pseudo = "pseudo";

		when(userRepository.insert(any())).thenThrow(DuplicateKeyException.class);
		//when

		Throwable exception = catchThrowable(() -> userService.create("test@test.com", pseudo, "name",
			"password"));

		//then
		assertThat(exception).isInstanceOf(BusinessException.class)
			.hasFieldOrPropertyWithValue("errorCode",
				"user.pseudo.duplicated");
	}
}
