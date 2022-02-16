package com.centreon.chatservice.infrastructure;

import com.centreon.chatservice.domain.user.User;
import com.centreon.chatservice.domain.user.UserRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * MongoDB's implementation of user repository
 *
 * @author ghazi
 */
@Repository
public interface MongoUserRepository extends UserRepository, MongoRepository<User, Long>
{
}
