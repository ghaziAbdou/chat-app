package com.centreon.chatservice.presentation.rest.user;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * The authentication request
 **/
@Getter
@Setter
public class UserAuthenticationRequest
{
    @NotBlank
    private String pseudo;
    @NotBlank
    private String password;
}
