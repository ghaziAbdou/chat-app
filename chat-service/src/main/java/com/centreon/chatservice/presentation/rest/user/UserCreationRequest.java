package com.centreon.chatservice.presentation.rest.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.ToString;

/**
 * The user creation request class.
 *
 * @author ghazi
 */
@ToString(exclude = "password")
public class UserCreationRequest
{

	@Size(max = 20)
	@NotBlank
	private String pseudo;

	@Email
	@NotBlank
	private String email;

	private String name;

	private String password;

	/**
	 * Retrieves this {@code UserCreationRequest} pseudo.
	 *
	 * @return this {@code UserCreationRequest} pseudo
	 */
	public String getPseudo()
	{
		return pseudo;
	}

	/**
	 * Sets this {@code UserCreationRequest} pseudo.
	 *
	 * @param pseudo this {@code UserCreationRequest} pseudo
	 */
	public void setPseudo(String pseudo)
	{
		this.pseudo = pseudo;
	}

	/**
	 * Retrieves this {@code UserCreationRequest} email.
	 *
	 * @return this {@code UserCreationRequest} email
	 */
	public String getEmail()
	{
		return email;
	}

	/**
	 * Sets this {@code UserCreationRequest} email.
	 *
	 * @param email this {@code UserCreationRequest} email
	 */
	public void setEmail(String email)
	{
		this.email = email;
	}

	/**
	 * Retrieves this {@code UserCreationRequest} name.
	 *
	 * @return this {@code UserCreationRequest} name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets this {@code UserCreationRequest} name.
	 *
	 * @param name this {@code UserCreationRequest} name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Retrieves this {@code UserCreationRequest} password.
	 *
	 * @return this {@code UserCreationRequest} password
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * Sets this {@code UserCreationRequest} password.
	 *
	 * @param password this {@code UserCreationRequest} password
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}
}
