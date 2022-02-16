package com.centreon.chatservice.domain.user;

import java.time.OffsetDateTime;

import com.centreon.chatservice.domain.exception.BusinessException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Version;

/**
 * The user domain class.
 *
 * @author ghazi
 */
@Getter
@Setter
public class User
{
	private final Long id;
	private String pseudo;
	private final String email;
	private String name;
	private String password;
	private final OffsetDateTime createdAt;
	private OffsetDateTime lastConnectionAt;
	private boolean connected;
	@Version
	private Long version;

	/**
	 * User constructor.
	 *
	 * @param id the user's id
	 * @param pseudo the user's pseudo
	 * @param email the user's email
	 * @param name the user's name
	 * @param password the user's password
	 * @param createdAt the user's creation date
	 */
	public User(Long id, String pseudo, String email, String name,
		String password, OffsetDateTime createdAt)
	{
		this.id = id;
		this.pseudo = pseudo;
		this.email = email;
		this.name = name;
		this.password = password;
		this.createdAt = createdAt;
	}

	public void connected(OffsetDateTime now)
	{
		this.lastConnectionAt = now;
		this.connected = true;
	}

	public void disconnected()
	{
		if (! this.connected) {
			throw new BusinessException("user not connected");
		}
		this.connected = false;
	}
}
