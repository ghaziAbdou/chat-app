package com.centreon.chatservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * The password encoder configuration
 *
 * @author ghazi
 */
@Configuration
public class PasswordConfiguration
{
	/**
	 * Gets the password encoder instance.
	 *
	 * <p> We use BCrypt, as it's usually the best solution available.
	 */
	@Bean
	public PasswordEncoder encoder()
	{
		return new BCryptPasswordEncoder();
	}
}
