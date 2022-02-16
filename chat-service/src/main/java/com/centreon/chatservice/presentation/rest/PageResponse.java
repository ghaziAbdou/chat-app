package com.centreon.chatservice.presentation.rest;

import java.util.List;

/**
 * Simple page response.
 *
 * @author ghazi
 */
public class PageResponse<T>
{
	private final List<T> content;
	private final long totalElements;
	private final long totalPages;

	/**
	 * Page Response constructor.
	 *
	 * @param content the page content
	 * @param totalElements the page totalElements
	 * @param totalPages the page totalPages
	 */
	public PageResponse(List<T> content, long totalElements, long totalPages)
	{
		this.content = content;
		this.totalElements = totalElements;
		this.totalPages = totalPages;
	}

	/**
	 * Retrieves this {@code PageResponse} content.
	 *
	 * @return this {@code PageResponse} content
	 */
	public List<T> getContent()
	{
		return content;
	}

	/**
	 * Retrieves this {@code PageResponse} totalElements.
	 *
	 * @return this {@code PageResponse} totalElements
	 */
	public long getTotalElements()
	{
		return totalElements;
	}

	/**
	 * Retrieves this {@code PageResponse} totalPages.
	 *
	 * @return this {@code PageResponse} totalPages
	 */
	public long getTotalPages()
	{
		return totalPages;
	}
}
