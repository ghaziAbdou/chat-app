package com.centreon.chatservice.presentation.rest.error;

import com.centreon.chatservice.domain.exception.BusinessException;
import com.centreon.chatservice.domain.exception.NotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * Handles all exceptions thrown by Controller
 *
 * @author ghazi
 */
@RestControllerAdvice
class ErrorRestHandler
{
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Catches {@link BusinessException}.
	 *
	 * Send a 400 Bad Request. The error message of the response body is the
	 * exception message.
	 *
	 * @param ex the exception to handle
	 * @return the http response representation
	 */
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorMessageResponse> handle(BusinessException ex)
	{
		LOGGER.info("The request payload is invalid: {} path {}", ex.getMessage(), getPath());
		return ResponseEntity.badRequest()
			.body(new ErrorMessageResponse(
				ex.getErrorCode(),
				ex.getMessage(),
				null));
	}

	/**
	 * Catches {@link NotFoundException}.
	 *
	 * Send a 404 Not Found. The error message of the response body is the
	 * exception message.
	 *
	 * @param ex the exception to handle
	 * @return the http response representation
	 */
	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ErrorMessageResponse> handle(NotFoundException ex)
	{
		LOGGER.info("Resource not found: {} path {}", ex.getMessage(), getPath());
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(new ErrorMessageResponse(
				null,
				ex.getMessage(),
				null));
	}

	/**
	 * This method catches the {@link MethodArgumentTypeMismatchException} that
	 * may be thrown when Spring failed to convert a value to the corresponding
	 * type in a controller parameter
	 *
	 * @param ex the {@link MethodArgumentTypeMismatchException} thrown by
	 * spring
	 * @return A HTTP response with a status {@code BAD_REQUEST} and the field
	 * in error
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorMessageResponse> handle(
		MethodArgumentTypeMismatchException ex
	)
	{
		LOGGER.info(
			"Failed to convert input to corresponding type for parameter: {} path {}",
			ex.getName(), getPath());
		return ResponseEntity.badRequest()
			.body(new ErrorMessageResponse(
				null,
				"Invalid param format: " + ex.getName(),
				null));
	}



	/**
	 * Handle missing RequestParameter error when a required parameter is missing.
	 * @param ex the {@code UnsatisfiedServletRequestParameterException}
	 * @return A HTTP response with a status {@code INVALID_REQUEST_ERROR}
	 * and the field validation
	 * message
	 */
	@ExceptionHandler(MissingServletRequestParameterException.class)
	ResponseEntity<ErrorMessageResponse> handle(MissingServletRequestParameterException ex)
	{
		LOGGER.info(
			"Invalid request. missing parameter {} on path {} ex {}",
			ex.getParameterName(), ex.getMessage(), getPath());
		return ResponseEntity.badRequest()
			.body(new ErrorMessageResponse(
				null,
				"Missing parameter : " + ex.getParameterName(),
				null));
	}

	/**
	 * This method catches the {@code BindException} that may be thrown
	 * during bean validation used in the controllers.
	 *
	 * @param ex the {@code BindException} thrown by the controller method
	 * @return A HTTP response with a status {@code BAD_REQUEST}
	 * and the field validation message. Note that this method return the
	 * first validation error in case of multiple validation error
	 */
	@ExceptionHandler(BindException.class)
	public ResponseEntity<ErrorMessageResponse> handle(BindException ex)
	{
		LOGGER.info("Bind error occurred in {} path {}", ex.getMessage(), getPath());
		FieldError field = ex.getFieldError();
		String fieldName = field != null ? field.getField() : null;
		return ResponseEntity.badRequest()
			.body(new ErrorMessageResponse(null, String.format("cannot bind request : %s %s",
				fieldName , ex.getAllErrors().get(0).getDefaultMessage()), fieldName));
	}

	/**
	 * This method catches the {@code ConcurrencyFailureException} that may be thrown
	 * when two processes try to modify the same entity at the same time and they conflict
	 *
	 * @param ex the {@code ConcurrencyFailureException} thrown by the controller method
	 * @return A HTTP response with a status {@code Conflict}
	 * and information about the object in conflict
	 */
	@ExceptionHandler(ConcurrencyFailureException.class)
	public ResponseEntity<ErrorMessageResponse> handle(ConcurrencyFailureException ex)
	{
		LOGGER.info("Concurrency Failure: {} path {}", ex.getMessage(), getPath());
		return ResponseEntity.status(HttpStatus.CONFLICT)
			.body(new ErrorMessageResponse(
				null,
				"conflict to access the resource",
				null));
	}

	/**
	 * This method catches a {@code HttpRequestMethodNotSupportedException}
	 * when the method use for the request doesn't exist
	 *
	 * @param ex {@code HttpRequestMethodNotSupportedException}
	 * @return A HTTP response with a {@code METHOD_NOT_ALLOWED} status
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	ResponseEntity<ErrorMessageResponse> handle(
		HttpRequestMethodNotSupportedException ex)
	{
		LOGGER.info("No method handler exists for this request path {}", getPath());
		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
			.body(new ErrorMessageResponse(
				null,
				"Method " + ex.getMethod() + " not supported",
				null));
	}

	/**
	 * This method catches the {@code HttpMediaTypeNotSupportedException}
	 * to create a custom HTTP response.
	 * It's when non multipart content have been found
	 *
	 * @param ex the {@code HttpMediaTypeNotSupportedException}
	 * @return A HTTP response with a status {@code UNSUPPORTED_MEDIA_TYPE}
	 */
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	ResponseEntity<ErrorMessageResponse> handle(
		HttpMediaTypeNotSupportedException ex)
	{
		LOGGER.info("Content-Type not supported for this request path {}", getPath());
		return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
			.body(new ErrorMessageResponse(
				null,
				"Content-Type "
					+ ex.getContentType()
					+ " not supported",
				null));
	}

	/**
	 * This method catches the a {@code NoHandlerFoundException} when the
	 * request contains resource which doesn't exist
	 *
	 * @param ex the {@code NoHandlerFoundException}
	 * @return A HTTP response with a {@code NOT_FOUND} status
	 */
	@ExceptionHandler(NoHandlerFoundException.class)
	ResponseEntity<ErrorMessageResponse> handle(NoHandlerFoundException ex)
	{
		LOGGER.info("No resource found for URL {}", ex.getRequestURL());
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(new ErrorMessageResponse(
				null,
				"No resource found for URL " + ex.getRequestURL(),
				null));
	}

	/**
	 * This method catches {@link HttpMessageNotReadableException} which are typically thrown when
	 * an issue prevents from parsing the body.
	 *
	 * @param ex the {@link HttpMessageNotReadableException}
	 * @return A HTTP response with a {@link HttpStatus#BAD_REQUEST} status
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorMessageResponse> handle(HttpMessageNotReadableException ex)
	{
		LOGGER.info("Bad request: {} path {}", ex.getMessage(), getPath());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(new ErrorMessageResponse(
				null,
				"Required body is messing",
				"body"
			));
	}

	/**
	 * This method catches {@link HttpMessageConversionException} which are typically thrown when
	 * an issue prevents from parsing the body.
	 *
	 * @param ex the {@link HttpMessageConversionException}
	 * @return A HTTP response with a {@link HttpStatus#BAD_REQUEST} status
	 */
	@ExceptionHandler(HttpMessageConversionException.class)
	public ResponseEntity<ErrorMessageResponse> handle(HttpMessageConversionException ex)
	{
		LOGGER.info("Bad request: {} path {}", ex.getMessage(), getPath());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(new ErrorMessageResponse(
				null,
				"Fail to decode body",
				"body"
			));
	}

	/**
	 * Catches {@link AccessDeniedException}. This exception is thrown when
	 * no required token is found in the request or if the token is invalid.
	 *
	 * The gateway should intercept requests that requires a token to send a
	 * 401 Unauthorized, so this method send a 403 Forbidden.
	 *
	 * @param ex the {@link AccessDeniedException}
	 * @return a HTTP response with status {@link HttpStatus#FORBIDDEN}
	 */
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorMessageResponse> handle(
		AccessDeniedException ex)
	{
		LOGGER.info("Access denied: {} path {}", ex.getMessage(), getPath());
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
			.body(new ErrorMessageResponse(
				null,
				"Authentication required",
				null));
	}

	/**
	 * Catches not identified exceptions
	 *
	 * @param ex Throwable
	 * @return A HTTP response with a {@code INTERNAL_SERVER_ERROR} status
	 */
	@ExceptionHandler(Throwable.class)
	public ResponseEntity<ErrorMessageResponse> handle(Throwable ex)
	{
		LOGGER.error("Unknown error occurred: {} path {}", ex.getMessage(), getPath(), ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(new ErrorMessageResponse(
				null,
				"An unknown error occurred",
				null));

	}

	private static String getPath()
	{
		RequestAttributes request =
			RequestContextHolder.getRequestAttributes();
		if (request instanceof ServletRequestAttributes) {
			return ((ServletRequestAttributes)request).getRequest().getRequestURI();
		}
		return null;
	}
}
