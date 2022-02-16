package com.centreon.chatservice.application.adapters;

/**
 * Ids generation adapter.
 * <p> provides a simple method to generate unique long ids
 *
 * @author ghazi
 */
public interface IdGenAdapter
{
	/**
	 * Retrieves a unique ID.
	 *
	 * @return a unique ID
	 */
	Long nextId();
}
