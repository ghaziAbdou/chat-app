package com.centreon.chatservice.infrastructure;

import java.time.OffsetDateTime;
import java.util.List;

import com.centreon.chatservice.domain.chat.Message;
import com.centreon.chatservice.domain.chat.MessageRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * Mongo implementation of MessageRepository
 *
 * @author ghazi
 */
@Service
public class MongoMessageRepository implements MessageRepository
{
	private final MongoTemplate mongoTemplate;
	private final ReactiveMongoOperations reactiveMongoTemplate;

	/**
	 * MongoMessageRepository constructor.
	 * @param mongoTemplate the mongoTemplate
	 * @param reactiveMongoTemplate the reactiveMongoTemplate
	 */
	public MongoMessageRepository(MongoTemplate mongoTemplate,
		ReactiveMongoOperations reactiveMongoTemplate)
	{
		this.mongoTemplate = mongoTemplate;
		this.reactiveMongoTemplate = reactiveMongoTemplate;
	}

	@Override
	public List<Message> findAllInDiscussion(Long userId, Long recipientId,String search,
		Long cursor, int size)
	{
		Criteria criteria = new Criteria().orOperator(
			Criteria.where("recipientId").is(userId).and("senderId").is(recipientId),
			Criteria.where("senderId").is(userId).and("recipientId").is(recipientId)
		);
		if (search != null) {
			criteria = criteria.and("content").regex("/.*"+ search +".*$/i");
		}
		if (cursor != null) {
			criteria.and("id").lt(cursor);
		}

		return mongoTemplate.find(new Query(criteria).with(Sort.by("id").descending()).limit(size),
			Message.class);
	}

	@Override
	public Flux<Message> stream(OffsetDateTime now)
	{
		return reactiveMongoTemplate.tail(new Query(Criteria.where("sentAt").gt(now)), Message.class);
	}

	@Override
	public Message save(Message message)
	{
		return mongoTemplate.save(message);
	}
}
