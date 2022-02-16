package com.centreon.chatservice.application;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.centreon.chatservice.application.adapters.IdGenAdapter;
import com.centreon.chatservice.domain.chat.Message;
import com.centreon.chatservice.domain.chat.MessageRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * Messages services
 *
 * @author ghazi
 */
@Service
public class MessageService
{
	private final IdGenAdapter idGenAdapter;
	private final MessageRepository messageRepository;

	/**
	 * Messages service constructor.
	 *
	 * @param idGenAdapter the idGen adapter
	 * @param messageRepository the message repository
	 */
	public MessageService(
		IdGenAdapter idGenAdapter,
		MessageRepository messageRepository)
	{
		this.idGenAdapter = idGenAdapter;
		this.messageRepository = messageRepository;
	}

	/**
	 * Find and search messages.
	 * @param userId the user id
	 * @param recipientId the other user in the discussion
	 * @param search message's text search
	 * @param cursor the requested cursor. Message id to retrieves ,if present, messages before this id
	 * @param size the resul size.
	 * @return list of messages
	 */
	public List<MessageResource> listDiscussion(Long userId, Long recipientId, String search, Long cursor, int size)
	{
		return messageRepository.findAllInDiscussion(userId, recipientId, search, cursor, size)
			.stream().map(MessageResource::new)
			.collect(Collectors.toList());
	}

	/**
	 * create and save a new message
	 * @param senderId the sender id
	 * @param recipientId the recipientId
	 * @param content the message content
	 * @return the created MessageResource
	 */
	public MessageResource send(Long senderId, Long recipientId, String content)
	{
		return new MessageResource(
			messageRepository.save(new Message(
				idGenAdapter.nextId(), senderId, recipientId, content, OffsetDateTime.now()))
		);
	}
	public Flux<Message> stream()
	{
		return messageRepository.stream(OffsetDateTime.now());
	}
}
