package com.centreon.chatservice.presentation.rest.chat;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * Chat message request.
 *
 * @author ghazi
 */
@Data
class ChatMessageRequest
{
	@NotNull
	private Long recipientId;
	private String content;
}
