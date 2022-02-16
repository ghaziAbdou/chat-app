package com.centreon.chatservice.application;

import java.time.OffsetDateTime;

import com.centreon.chatservice.domain.chat.Message;
import lombok.Getter;

/**
 * Message resource
 *
 * @author ghazi
 */
@Getter
public class MessageResource
{
	private final Long id;
	private final Long senderId;
	private final Long recipientId;
	private final String content;
	private final OffsetDateTime sentAt;

	public MessageResource(Message message)
	{
		this.id = message.getId();
		this.senderId = message.getSenderId();
		this.recipientId = message.getRecipientId();
		this.content = message.getContent();
		this.sentAt = message.getSentAt();
	}
}
