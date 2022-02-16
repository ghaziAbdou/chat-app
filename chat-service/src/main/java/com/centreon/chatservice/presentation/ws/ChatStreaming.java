package com.centreon.chatservice.presentation.ws;

import java.security.Principal;

import javax.annotation.PostConstruct;

import com.centreon.chatservice.application.EventService;
import com.centreon.chatservice.application.MessageService;
import com.centreon.chatservice.application.UserPrincipal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * Chat websocket controller
 *
 * @author ghazi
 */
@Component
public class ChatStreaming
{
	private static final Logger LOGGER = LogManager.getLogger();
	private final MessageService messageService;
	private final EventService eventService;
	private final SimpMessagingTemplate messagingTemplate;

	/**
	 * ChatController constructor
	 * @param messageService the message service
	 * @param eventService the event service
	 * @param messagingTemplate the messaging template
	 */
	public ChatStreaming(MessageService messageService,
		EventService eventService,
		SimpMessagingTemplate messagingTemplate)
	{
		this.messageService = messageService;
		this.eventService = eventService;
		this.messagingTemplate = messagingTemplate;

	}

	@PostConstruct
	public void initialize()
	{
		this.messageService.stream().subscribe(message -> {
			messagingTemplate
				.convertAndSendToUser("" + message.getRecipientId(),
					"/messages", message);
			LOGGER.info("new message notification sent to {}/messages ",message.getRecipientId());
		});
		this.eventService.stream().subscribe(event -> {
			messagingTemplate
				.convertAndSendToUser("all", "/events", event);
			LOGGER.info("event {} sent to all/events ", event);
		});
	}

	//method called when user open page in browser
	@EventListener
	public void handleWebSocketConnectListener(SessionConnectedEvent event)
	{
		Long userId = getUserId(event);
		eventService.userConnected(userId);
		LOGGER.info("User connected {}", userId);
	}

	//method called when user close page in browser
	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event)
	{
		Long userId = getUserId(event);
		eventService.userDisconnected(userId);
		LOGGER.info("User unconnected {}", userId);
	}

	private Long getUserId(AbstractSubProtocolEvent event)
	{
		Principal user = event.getUser();
		if (user instanceof Authentication) {
			Object principal = ((Authentication)user).getPrincipal();
			if (principal instanceof UserPrincipal) {
				return ((UserPrincipal)principal).getId();
			}
		}
		return null;
	}
}
