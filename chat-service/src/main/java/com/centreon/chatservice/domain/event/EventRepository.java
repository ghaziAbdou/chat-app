package com.centreon.chatservice.domain.event;

import java.time.OffsetDateTime;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author ghazi
 */
public interface EventRepository
{

	/**
	 * insert new event asynchronously
	 * @param event event to save
	 * @return the saved event
	 */
	Mono<Event> insert(Event event);

	/**
	 * stream events from date
	 * @param dateTime starting date time
	 * @return event flux
	 */
	Flux<Event> stream(OffsetDateTime dateTime);

	Flux<Event> findAll(Long userId, Long cursor, int size);
}
