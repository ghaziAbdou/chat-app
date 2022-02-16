package com.centreon.chatservice.domain.user;

import java.time.OffsetDateTime;

import com.centreon.chatservice.domain.exception.BusinessException;
import org.junit.Assert;
import org.junit.Test;

/**
 * User test class
 */
public class UserTest
{

	@Test
	public void createdUserShouldNotBeConnected()
	{
		OffsetDateTime now = OffsetDateTime.now();
		//given
		User user = new User(1L, "pseudo", "email@test.com", "name",
			"password", now);
		//then
		Assert.assertEquals("should get the right id", Long.valueOf(1L), user.getId());
		Assert.assertEquals("should get the right pseudo", "pseudo",
			user.getPseudo());
		Assert.assertEquals("should get the right email", "email@test.com",
			user.getEmail());
		Assert.assertEquals("should get the right name", "name",
			user.getName());
		Assert.assertEquals("should get the right password", "password",
			user.getPassword());
		Assert.assertEquals("should get the right createdAt", now,
			user.getCreatedAt());
	}

	@Test
	public void createdUserShouldSettAllFields()
	{
		//given
		User user = createUser();
		//then
		Assert.assertFalse("connected should be false", user.isConnected());
		Assert.assertNull("lastConnectionAt should be null", user.getLastConnectionAt());
	}

	@Test
	public void connectedShouldUpdateConnected()
	{
		//given
		User user = createUser();
		Assert.assertFalse("connected should be false", user.isConnected());
		OffsetDateTime now = OffsetDateTime.now();
		//when
		user.connected(now);
		//then
		Assert.assertTrue("connected should be updated", user.isConnected());
	}

	@Test
	public void connectedShouldUpdateLastConnectionAt()
	{
		//given
		User user = createUser();
		OffsetDateTime now = OffsetDateTime.now();
		//when
		user.connected(now);
		//then
		Assert.assertEquals("lastConnectionAt should be updated",
			now, user.getLastConnectionAt());
	}

	@Test
	public void disconnectedShouldUpdateConnected()
	{
		//given
		User user = createUser();
		user.connected(OffsetDateTime.now());
		//when
		user.disconnected();
		Assert.assertFalse("connected should be updated", user.isConnected());
	}

	@Test
	public void disconnectedShouldThrowBusinessExceptionIfUserNotConnected()
	{
		//given
		User user = createUser();
		//then

		Assert.assertThrows("disconnected should throw exception if user not connected",
			BusinessException.class, user::disconnected);
	}

	private User createUser()
	{
		return new User(1L, "pseudo", "email@test.com", "name",
			"password", OffsetDateTime.now());
	}
}
