package com.centreon.chatservice.application;

import com.centreon.chatservice.domain.user.User;
import lombok.Data;

/**
 * User authentication resource class.
 *
 * @author ghazi
 */
@Data
public class UserAuthenticationResource
{
	private final boolean success;
	private final UserResource user;
	private final String token;

	/**
	 * create successful authentication
	 *
	 * @param user the user
	 * @param token the jwt token
	 */
	static UserAuthenticationResource success(User user, String token)
	{
		return new UserAuthenticationResource(true, new UserResource(user), token);
	}

	/**
	 * create failure authentication
	 */
	static UserAuthenticationResource failed()
	{
		return new UserAuthenticationResource(false, null, null);
	}
}
