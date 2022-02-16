package com.centreon.chatservice.presentation.rest.error;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

/**
 * Http Error message response that contains several details about the occurred
 * error. used when the HTTP code is not 2xx.
 *
 * @author ghazi
 */
@JsonPropertyOrder({"error", "errorDescription", "paramName"})
@Data
public class ErrorMessageResponse
{
	private final String error;
	private final String errorDescription;
	private final String paramName;

	/**
	 * @param error error code
	 * @param errorDescription error human-readable message description
	 * @param paramName error paramName
	 */
	@JsonCreator
	ErrorMessageResponse(String error, String errorDescription, String paramName)
	{
		this.error = error;
		this.errorDescription = errorDescription;
		this.paramName = paramName;
	}
}
