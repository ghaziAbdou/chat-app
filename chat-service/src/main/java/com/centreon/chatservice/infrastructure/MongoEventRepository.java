package com.centreon.chatservice.infrastructure;

import java.time.OffsetDateTime;

import com.centreon.chatservice.domain.event.Event;
import com.centreon.chatservice.domain.event.EventRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Mongo event repository
 * @author ghazi
 */
@Service
public class MongoEventRepository implements EventRepository
{
	private final ReactiveMongoTemplate reactiveMongoTemplate;

	/**
	 * MongoEventRepository constructor
	 * @param reactiveMongoTemplate the reactiveMongoTemplate
	 */
	public MongoEventRepository(ReactiveMongoTemplate reactiveMongoTemplate)
	{
		this.reactiveMongoTemplate = reactiveMongoTemplate;

	}

	/**
	 * insert new event asynchronously
	 *
	 * @param event event to save
	 * @return the saved event
	 */
	@Override
	public Mono<Event> insert(Event event)
	{
		return reactiveMongoTemplate.insert(event);
	}

	/**
	 * stream events from date
	 *
	 * @param dateTime starting date time
	 * @return event flux
	 */
	@Override
	public Flux<Event> stream(OffsetDateTime dateTime)
	{
		return reactiveMongoTemplate.tail(new Query(Criteria.where("createdAt").gt(dateTime)), Event.class);
	}

	@Override
	public Flux<Event> findAll(Long userId, Long cursor, int size)
	{
		Criteria criteria = new Criteria();

		if (cursor != null) {
			criteria.and("id").lt(cursor);
		}
		if (userId != null) {
			criteria.and("userId").is(userId);
		}
		return reactiveMongoTemplate.find(
			new Query(criteria).with(Sort.by("id").descending()).limit(size),
			Event.class);
	}
}
