package com.centreon.chatservice.application;

import java.time.OffsetDateTime;

import com.centreon.chatservice.domain.event.Event;
import lombok.Getter;
import lombok.ToString;

/**
 * Event resource class
 * @author ghazi
 */
@Getter
@ToString
public class EventResource
{
	private final Long id;
	private final Long userId;
	private final OffsetDateTime createdAt;
	private final String type;

	public EventResource(Event event)
	{
		this.id = event.getId();
		this.userId = event.getUserId();
		this.createdAt = event.getCreatedAt();
		this.type = event.getType().name();
	}
}
