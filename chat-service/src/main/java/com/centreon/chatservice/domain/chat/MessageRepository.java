package com.centreon.chatservice.domain.chat;

import java.time.OffsetDateTime;
import java.util.List;

import reactor.core.publisher.Flux;

/**
 * Message repository
 * @author ghazi
 */
public interface MessageRepository
{
	/**
	 * find all messages for a discussion of user with a recipient
	 * @param userId the user id
	 * @param recipientId the other user in the discussion
	 * @param search optional search query. can be {@code  null}
	 * @param cursor the cursor. can be {@code  null}
	 * @param size the result size
	 * @return list of messages
	 */
	List<Message> findAllInDiscussion(Long userId, Long recipientId, String search, Long cursor, int size);

	Flux<Message> stream(OffsetDateTime now);

	/**
	 * save a new message
	 * @param message the message to save
	 * @return the saved message
	 */
	Message save(Message message);
}
