package com.centreon.chatservice.presentation.rest;

import java.util.List;

import com.centreon.chatservice.application.EventResource;
import com.centreon.chatservice.application.EventService;
import com.centreon.chatservice.application.UserPrincipal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Event controller
 *
 * @author ghazi
 */
@RestController
@RequestMapping("events")
public class EventController
{
	private final EventService eventService;
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Event controller constructor.
	 * @param eventService the event service
	 */
	public EventController(EventService eventService)
	{
		this.eventService = eventService;
	}

	/**
	 * retrieves event list
	 * @param requester the requester principal
	 * @param userId optional user id filter. can be {@code null}
	 * @param cursor optional cursor. can be {@code null} the event id to retrieves ,if present, events before this id
	 * @param size the desired result size by default 10
	 * @return list of events
	 */
	@PreAuthorize("isAuthenticated()")
	@GetMapping
	public List<EventResource> list(
		@AuthenticationPrincipal UserPrincipal requester,
		@RequestParam(required = false) Long userId,
		@RequestParam(required = false) Long cursor,
		@RequestParam(required = false, defaultValue = "10") int size)
	{
		LOGGER.debug("events list requested for user {}", requester.getId());
		List<EventResource> result = eventService.list(userId, cursor, size);
		LOGGER.info("events list retrieved user {} size {}", requester.getId(), result.size());
		return result;
	}
}
