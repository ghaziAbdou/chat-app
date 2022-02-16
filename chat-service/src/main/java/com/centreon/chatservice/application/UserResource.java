package com.centreon.chatservice.application;

import com.centreon.chatservice.domain.user.User;
import lombok.Data;

/**
 * User resource class.
 *
 * @author ghazi
 */
@Data
public class UserResource
{
	private final Long id;
	private final String email;
	private final String name;

	/**
	 * UserResource constructor from user domain.
	 *
	 * @param user the user
	 */
	public UserResource(User user)
	{
		this.id = user.getId();
		this.email = user.getEmail();
		this.name = user.getName();
	}
}
