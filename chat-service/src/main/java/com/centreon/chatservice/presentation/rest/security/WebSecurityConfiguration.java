package com.centreon.chatservice.presentation.rest.security;

import com.centreon.chatservice.application.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.web.cors.CorsConfiguration;

/**
 * Web security configuration.
 **/
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter
{
    private final UserService userService;

    public WebSecurityConfiguration(UserService userService)
    {
        this.userService = userService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        CorsConfiguration corsConfiguration =
            new CorsConfiguration().applyPermitDefaultValues();

        // Configure our custom JWT filter
        http.csrf().disable()
            .authorizeRequests().anyRequest().permitAll()
            .and()
            .addFilterAfter(new JWTAuthorizationFilter(userService),
                AbstractPreAuthenticatedProcessingFilter.class)
            .cors().configurationSource(request -> corsConfiguration);
    }
}
