package com.centreon.chatservice.application;

import com.centreon.chatservice.domain.user.User;
import lombok.Getter;

/**
 * Authentication principal
 *
 * @author ghazi
 */
@Getter
public class UserPrincipal
{
	private final Long id;
	private final String name;

	public UserPrincipal(User user)
	{
		this.id = user.getId();
		this.name = user.getName();
	}
}
