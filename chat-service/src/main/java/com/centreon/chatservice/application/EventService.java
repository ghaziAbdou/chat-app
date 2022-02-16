package com.centreon.chatservice.application;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.centreon.chatservice.application.adapters.IdGenAdapter;
import com.centreon.chatservice.domain.event.Event;
import com.centreon.chatservice.domain.event.EventRepository;
import com.centreon.chatservice.domain.exception.NotFoundException;
import com.centreon.chatservice.domain.user.User;
import com.centreon.chatservice.domain.user.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * Event service
 *
 * @author ghazi
 */
@Service
public class EventService
{

	private final EventRepository eventRepository;
	private final UserRepository userRepository;
	private final IdGenAdapter idGenAdapter;

	/**
	 * EventService constructor.
	 * @param eventRepository the event repository
	 * @param userRepository the user repository
	 * @param idGenAdapter the id gen adapter
	 */
	public EventService(EventRepository eventRepository,
		UserRepository userRepository,
		IdGenAdapter idGenAdapter)
	{
		this.eventRepository = eventRepository;
		this.userRepository = userRepository;
		this.idGenAdapter = idGenAdapter;
	}

	public void userConnected(Long userId)
	{
		OffsetDateTime now = OffsetDateTime.now();
		User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found."));
		user.connected(now);
		userRepository.save(user);
		Event event = new Event(idGenAdapter.nextId(), userId, now, Event.EventType.USER_CONNECTED);
		eventRepository.insert(event).block();
	}

	public void userDisconnected(Long userId)
	{
		OffsetDateTime now = OffsetDateTime.now();
		User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found."));
		user.disconnected();
		userRepository.save(user);
		Event event = new Event(idGenAdapter.nextId(), userId, now, Event.EventType.USER_DISCONNECTED);
		eventRepository.insert(event).block();
	}

	/**
	 *
	 * @return events stream from now
	 */
	public Flux<EventResource> stream()
	{
		return eventRepository.stream(OffsetDateTime.now())
			.map(EventResource::new);
	}

	/**
	 * retrieves event list
	 * @param userId optional user id filter. can be {@code null}
	 * @param cursor optional cursor. can be {@code null} the event id to retrieves ,if present, events before this id
	 * @param size the desired result size by default 10
	 * @return list of events
	 */
	public List<EventResource> list(Long userId, Long cursor, int size)
	{
		return eventRepository.findAll(userId, cursor, size)
			.toStream()
			.map(EventResource::new)
			.collect(Collectors.toList());
	}
}
