package com.centreon.chatservice.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.Instant;
import java.time.OffsetDateTime;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author ghazi
 */
public class IdGenServiceTest
{

	private final IdGenService idGenService = new IdGenService(0, 0);

	@Test
	public void nextIdShouldGenerate64IdInSameMillis()
	{
		// given
		final int[] count = {0};

		Instant fixed = OffsetDateTime.now().toInstant();
		// when
		Throwable thrown = catchThrowable(() -> {
			for (int i = 0; i < 64; i++) {
				idGenService.nextId(fixed);
				count[0]++;
			}
		});

		// then
		assertSoftly(softly -> {
			assertThat(thrown).doesNotThrowAnyException();
			assertThat(count[0])
				.as(
					"nextId must be able to generate 64 id before raised an exception")
				.isEqualTo(64);
		});
	}

	@Test
	public void nextIdShouldThrowISEIfMoreThan64IdIsRequestedInSameMillis()
	{
		// given
		final int[] count = {0};
		Instant fixed = OffsetDateTime.now().toInstant();
		// when
		Throwable thrown = catchThrowable(() -> {
			for (int i = 0; i < 259; i++) {
				idGenService.nextId(fixed);
				count[0]++;
			}
		});

		// then
		assertSoftly(softly -> {
			assertThat(thrown)
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("IdGen Sequence exhausted.");
			assertThat(count[0])
				.as(
					"nextId must be able to generate 64 id before raised an exception")
				.isEqualTo(64);
		});
	}

	@Test
	public void nextIdShouldGenerateUniqueIdsForSameInstant()
	{
		OffsetDateTime now = OffsetDateTime.now();
		long id1 = idGenService.nextId(now.toInstant());
		long id2 = idGenService.nextId(now.toInstant());
		Assert.assertNotEquals("next id should generate unique ids", id1, id2);
	}
}
