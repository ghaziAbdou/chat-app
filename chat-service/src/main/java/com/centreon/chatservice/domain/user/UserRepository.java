package com.centreon.chatservice.domain.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * User repository interface.
 *
 * @author ghazi
 */
public interface UserRepository
{
	/**
	 * Saves a user.
	 *
	 * @param user the user to save
	 */
	User insert(User user);

	/**
	 * Saves a user.
	 *
	 * @param user the user to save
	 */
	User save(User user);

	/**
	 * Retrieves a page of users.
	 *
	 * @param name optional name filter
	 * @param email optional email filter
	 * @param pageable page request.
	 * @return page of users.
	 */
	Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email, Pageable pageable);

    Optional<User> findByPseudo(String pseudo);

	Optional<User> findById(Long userId);
}
