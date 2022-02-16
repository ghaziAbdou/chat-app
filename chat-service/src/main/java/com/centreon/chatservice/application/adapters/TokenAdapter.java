package com.centreon.chatservice.application.adapters;

/**
 * Token adapter provides methods to generate/validate tokens
 **/
public interface TokenAdapter
{
    /**
     * generate token form user's userId
     * @param userId the user's userId
     * @return the generated token
     */
    String generate(String userId);

    /**
     * validate a token
     * @param token the token to validate
     * @return the user's userId if token is valid otherwise return null
     */
    String validate(String token);
}
