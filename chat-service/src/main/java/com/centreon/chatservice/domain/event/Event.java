package com.centreon.chatservice.domain.event;

import java.time.OffsetDateTime;

import lombok.Getter;

/**
 * Event domain class.
 *
 * @author ghazi
 */
@Getter
public class Event
{
	private final Long id;
	private final Long userId;
	private final OffsetDateTime createdAt;
	private final EventType type;

	public Event(Long id, Long userId, OffsetDateTime createdAt,
		EventType type)
	{
		this.id = id;
		this.userId = userId;
		this.createdAt = createdAt;
		this.type = type;
	}

	public enum EventType
	{
		USER_CONNECTED,
		USER_DISCONNECTED,
		USER_REGISTRED,
	}
}
