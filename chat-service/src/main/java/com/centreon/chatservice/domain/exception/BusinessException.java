package com.centreon.chatservice.domain.exception;

/**
 * Must be thrown when an error occurred due to input parameters or a violation of business logic.
 *
 * @author ghazi
 */
public final class BusinessException extends RuntimeException
{
	private final String errorCode;

	/**
	 * Create exception with a message.
	 *
	 * @param message the message exception
	 */
	public BusinessException(String message)
	{
		super(message);
		this.errorCode = null;
	}

	/**
	 * Create exception with an error code and a message.
	 *
	 * @param errorCode the error code
	 * @param message the message exception
	 */
	public BusinessException(String errorCode, String message)
	{
		super(message);
		this.errorCode = errorCode;
	}

	/**
	 * The error code of this exception.
	 *
	 * @return an error code (may be null)
	 */
	public String getErrorCode()
	{
		return errorCode;
	}
}
