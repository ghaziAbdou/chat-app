package com.centreon.chatservice.presentation.ws;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

/**
 * @author ghazi
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 100)
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer
{
	@Override
	protected void configureInbound(
		MessageSecurityMetadataSourceRegistry messages)
	{
		messages
			.nullDestMatcher().authenticated()
			.simpDestMatchers("/queue/**").hasRole("USER")
			.simpDestMatchers("/events/**").hasRole("USER")
			.anyMessage().denyAll();
	}

	@Override
	protected boolean sameOriginDisabled()
	{
		return true;
	}
}
