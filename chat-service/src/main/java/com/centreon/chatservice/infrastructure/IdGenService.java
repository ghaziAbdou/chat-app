package com.centreon.chatservice.infrastructure;

import java.time.Instant;
import java.time.OffsetDateTime;

import com.centreon.chatservice.application.adapters.IdGenAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * The {@code IdGenService} class implements Twitter-like Snowflakes id.
 *
 * <p>It allows for distributed (multi-datacenter) id generation,
 * with short ids, and ordering.
 *
 * <p>Important: care must be taken to assign a different ID for each DC,
 * and a different ID to each instance within a DC.
 *
 * @author ghazi
 */
@Service
public class IdGenService implements IdGenAdapter
{

	private static final int MAX_SEQUENCE = (1 << 6) - 1;

	// Custom Epoch (January 1, 2020 Midnight UTC = 2020-01-01T00:00:00Z)
	private static final long CUSTOM_EPOCH = 1577836800000L;

	private final int nodeId;

	private volatile long lastTimestamp = -1L;
	private volatile long sequence = 0L;

	/**
	 * Creates a new {@code IdGenService}.
	 *
	 * @param datacenter the datacenterId (between 0 and 31)
	 * @param worker the workerId (between 0 and 7)
	 */
	public IdGenService(@Value("${idgen.datacenter}") int datacenter, @Value("${idgen.worker}") int worker)
	{
		if (datacenter < 0 || datacenter > 31) {
			throw new IllegalArgumentException("Invalid datacenter");
		}
		if (worker < 0 || worker > 7) {
			throw new IllegalArgumentException("Invalid worker");
		}
		this.nodeId = (datacenter << 3) | worker;
	}

	@Override
	public Long nextId()
	{
		return nextId(OffsetDateTime.now().toInstant());
	}

	/**
	 * Retrieves a unique ID.
	 *
	 * @param now the current time
	 * @return a unique ID
	 */
	synchronized long nextId(Instant now)
	{
		long currentTimestamp = now.toEpochMilli() - CUSTOM_EPOCH;

		if (currentTimestamp < lastTimestamp) {
			throw new IllegalStateException(
				"IdGen Clock went backward.");
		}

		if (currentTimestamp == lastTimestamp) {
			sequence = (sequence + 1) & MAX_SEQUENCE;
			if (sequence == 0) {
				throw new IllegalStateException(
					"IdGen Sequence exhausted.");
			}
		} else {
			sequence = 0;
		}

		lastTimestamp = currentTimestamp;
		// id = [ time ][ nodeId ][ seq ]
		long id = currentTimestamp << 14;
		id |= (long)nodeId << 6;
		id |= sequence;
		return id;
	}
}
