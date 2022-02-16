package com.centreon.chatservice.domain.exception;

/**
 * Must be thrown when a resource can not be found.
 *
 * @author ghazi
 */
public final class NotFoundException extends RuntimeException
{
	/**
	 * Create exception with a message.
	 *
	 * @param message the message exception
	 */
	public NotFoundException(String message)
	{
		super(message);
	}
}
