package com.centreon.chatservice.presentation.rest;

/**
 * Page request class.
 *
 * @author ghazi
 */
public class PagingRequest
{
	private int size = 10;
	private int page = 0;

	/**
	 * Retrieves this {@code PagingRequest} size.
	 *
	 * @return this {@code PagingRequest} size
	 */
	public int getSize()
	{
		return size;
	}

	/**
	 * Retrieves this {@code PagingRequest} page.
	 *
	 * @return this {@code PagingRequest} page
	 */
	public int getPage()
	{
		return page;
	}

	/**
	 * Sets this {@code PagingRequest} size.
	 *
	 * @param size this {@code PagingRequest} size
	 */
	public void setSize(int size)
	{
		this.size = Math.min(size, 100);
	}

	/**
	 * Sets this {@code PagingRequest} page.
	 *
	 * @param page this {@code PagingRequest} page
	 */
	public void setPage(int page)
	{
		this.page = Math.max(page, 0);
	}
}
