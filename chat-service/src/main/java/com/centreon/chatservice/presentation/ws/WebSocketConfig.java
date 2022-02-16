package com.centreon.chatservice.presentation.ws;

import java.util.Collections;
import java.util.List;

import com.centreon.chatservice.application.UserService;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Websocket configuration to enable and configure websockets
 *
 * @author ghazi
 */
@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer
{

	private final UserService userService;

	/**
	 * constructor.
	 * @param userService the user service
	 */
	public WebSocketConfig(UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * Configures a simple in-memory message broker with one destination for sending and receiving
	 * messages.
	 *
	 * <p> The destination is prefixed with /users, it also designates the /app prefix for messages
	 * that are bound for methods annotated with @MessageMapping.
	 *
	 * <p> User destination prefix /users is used by ConvertAndSendToUser method
	 * of SimpleMessagingTemplate to prefix all user-specific destinations with /users.
	 * @param config the message broker registry
	 */
	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker( "/queue");
		config.setUserDestinationPrefix("/queue");
	}

	/**
	 * Registers /ws STOMP endpoint
	 *
	 * <p>This endpoint is used by the client to connect to the STOMP server.
	 * It also enables the SockJS fallback options, so that alternative messaging options may be
	 * used if WebSockets are not available.
	 *
	 * @param registry the StompEndpointRegistry
	 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/stomp").setAllowedOrigins("*");
		registry.addEndpoint("/stomp").setAllowedOrigins("*").withSockJS();
	}

	@Override
	public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
		DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
		resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		converter.setObjectMapper(JsonMapper.builder()
			.addModule(new ParameterNamesModule())
			.addModule(new Jdk8Module())
			.addModule(new JavaTimeModule())
			.build());
		converter.setContentTypeResolver(resolver);
		messageConverters.add(converter);
		return false;
	}

	/**
	 * https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#websocket-stomp-authentication-token-based
	 * @param registration channel registration
	 */
	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(new ChannelInterceptor() {
			@Override
			public Message<?> preSend(Message<?> message, MessageChannel channel) {
				StompHeaderAccessor accessor =
					MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
				if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
					// Verify that the request holds a Bearer authentication
					String authorization = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);
					if (authorization != null && authorization.startsWith("Bearer ")) {
						// Extract the authentication principal from the JWT token
						String token = authorization.replace("Bearer ", "");
						userService.checkAuthentication(token).ifPresent(principal -> {
							PreAuthenticatedAuthenticationToken authenticationToken = new PreAuthenticatedAuthenticationToken(principal,
								"", Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
							authenticationToken.setAuthenticated(true);
							accessor.setUser(authenticationToken);
						});
					}
				}
				return message;
			}
		});
	}
}
