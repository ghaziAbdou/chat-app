package com.centreon.chatservice.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.io.IOException;
import java.time.Duration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author ghazi
 */
public class JwtTokenServiceTest
{
	private static final String PRIVATE_KEY = "-----BEGIN EC PRIVATE KEY-----\n"
		+ "MHQCAQEEIHKzNRrQOZZGwm7aeoMqyIH385yUfuD5fUm3IEvAMlm0oAcGBSuBBAAK\n"
		+ "oUQDQgAEFdHgaQ/hSGPefa675nX11y1EUi4s6OmCAeggEHdMSntE3lnGxnZuctVu\n"
		+ "lZcC2U+wqnvRRsu1t9UNHRQhk1y0Sg==\n"
		+ "-----END EC PRIVATE KEY-----\n";

	private static final String PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n"
		+ "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEFdHgaQ/hSGPefa675nX11y1EUi4s6OmC\n"
		+ "AeggEHdMSntE3lnGxnZuctVulZcC2U+wqnvRRsu1t9UNHRQhk1y0Sg==\n"
		+ "-----END PUBLIC KEY-----\n";

	private JwtTokenService service;

	@Before
	public void setUp() throws IOException
	{
		JwtTokenConfigurationProperties
			tokenConfiguration = new JwtTokenConfigurationProperties();
		tokenConfiguration
			.setPrivateKeyPem(PRIVATE_KEY);
		tokenConfiguration
			.setPublicKeyPem(PUBLIC_KEY);
		tokenConfiguration.setValidity(Duration.ofMinutes(10));
		tokenConfiguration.init();
		this.service = new JwtTokenService(tokenConfiguration);
	}

	@Test
	public void generateShouldThrowNPEIfEmailIsNull()
	{
		// when
		Throwable actual = catchThrowable(
			() -> service.generate(null));

		// then
		assertSoftly(softly -> {
			assertThat(actual)
				.isInstanceOf(NullPointerException.class)
				.hasMessage("userId must not be null");
		});
	}

	@Test
	public void validateShouldThrowNPEIfTokenIsNull()
	{
		// when
		Throwable actual = catchThrowable(() -> service.validate(null));

		// then
		assertSoftly(softly -> assertThat(actual)
			.isInstanceOf(NullPointerException.class)
			.hasMessage("token must not be null"));
	}

	@Test
	public void validateShouldReturnUserId()
	{
		// given
		String userId = "id";
		String validToken = validToken(userId);
		// when
		String decodeId = service.validate(validToken);
		Assert.assertEquals("decoded user id should be equals to the provided one",
			userId, decodeId);
	}

	@Test
	public void generateShouldGivesTokenContainsUserId()
	{
		// given
		String userId = "123";
		//when
		String token = service.generate(userId);
		//then
		DecodedJWT decode = JWT.decode(token);
		Assert.assertEquals("generated token should contains user id",
			userId, decode.getClaim("user-id").asString());
	}

	@Test
	public void validateShouldReturnNullIfTokenHasExpired()
	{
		// given
		// expired valid token
		String expiredToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzUxMiJ9.eyJ1c2VyLWlkIjoiMTIzIiwiZXhwIjo"
			+ "xNjQ1MDIwMzUyLCJpYXQiOjE2NDUwMjAzNTJ9.AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB"
			+ "mP4l5pdx-LdHGiISfbJHO-a8A-L0cNn-3vPuXuSsxtAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
			+ "AAALenHtWRgJ1GjYni-Pnjost68KQ3Q-ZStWlAVuhq7-ZV";

		//when
		String docode = service.validate(expiredToken);

		//then
		Assert.assertNull("should not validate expired token", docode);
	}

	@Test
	public void  validateShouldReturnNullIfTokenIsInvalid()
	{
		// given
		String fakeToken = "fake";

		//when
		String docode = service.validate(fakeToken);

		//then
		Assert.assertNull("should not validate invalid token", docode);
	}

	private String validToken(String userId)
	{
		return service.generate(userId);
	}
}
