package com.centreon.chatservice.presentation.rest.chat;

import java.util.List;

import javax.validation.Valid;

import com.centreon.chatservice.application.MessageResource;
import com.centreon.chatservice.application.MessageService;
import com.centreon.chatservice.application.UserPrincipal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Chat rest controller
 * @author ghazi
 */
@RestController
@RequestMapping("/users/me")
public class ChatController
{
	private static final Logger LOGGER = LogManager.getLogger();
	private final MessageService messageService;

	/**
	 * ChatController constructor.
	 * @param messageService the chat service
	 */
	public ChatController(MessageService messageService)
	{
		this.messageService = messageService;
	}

	/**
	 * retrieves chat messages list
	 * @param requester the user principal
	 * @param recipientId the other user in the discussion
	 * @param search optional search text. can be {@code null}
	 * @param cursor optional cursor. can be {@code null} the message id to retrieves ,if present, messages before this id
	 * @param size the desired result size by default 10
	 * @return list of messages
	 */
	@GetMapping("/messages")
	@PreAuthorize("isAuthenticated()")
	public List<MessageResource> list(@AuthenticationPrincipal UserPrincipal requester,
		@RequestParam Long recipientId,
		@RequestParam(required = false) String search,
		@RequestParam(required = false) Long cursor,
		@RequestParam(required = false, defaultValue = "10") int size)
	{
		LOGGER.debug("messages list requested for user {}", requester.getId());
		List<MessageResource> result = messageService.listDiscussion(requester.getId(), recipientId, search, cursor, size);
		LOGGER.info("messages list retrieved user {} size {}", requester.getId(), result.size());
		return result;
	}

	/**
	 * send new message
	 * @param requester the user principal
	 * @param message the message request
	 * @return the created message resource
	 */
	@PostMapping("/messages")
	@PreAuthorize("isAuthenticated()")
	public MessageResource send(@AuthenticationPrincipal UserPrincipal requester,
		@RequestBody @Valid ChatMessageRequest message)
	{
		LOGGER.debug("message send requested for user {} to {}", requester.getId(), message.getRecipientId());
		MessageResource result = messageService.send(requester.getId(), message.getRecipientId(), message.getContent());
		LOGGER.info("messages sent from user {} to {}", requester.getId(), result.getRecipientId());
		return result;
	}
}
