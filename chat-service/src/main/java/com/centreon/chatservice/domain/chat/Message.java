package com.centreon.chatservice.domain.chat;

import java.time.OffsetDateTime;

import lombok.Getter;

/**
 * The chat message domain class.
 *
 * @author ghazi
 */
@Getter
public class Message
{
	private final Long id;
	private final Long senderId;
	private final Long recipientId;
	private final String content;
	private final OffsetDateTime sentAt;
	private OffsetDateTime seenAt;

	public Message(Long id, Long senderId, Long recipientId,
		String content, OffsetDateTime sentAt)
	{
		this.id = id;
		this.senderId = senderId;
		this.recipientId = recipientId;
		this.content = content;
		this.sentAt = sentAt;
	}
}
