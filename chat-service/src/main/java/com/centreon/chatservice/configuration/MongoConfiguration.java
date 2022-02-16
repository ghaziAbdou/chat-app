package com.centreon.chatservice.configuration;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Configuration for mongo, including converters, transactionManager and other configurations
 *
 * @author ghazi
 */
@Configuration
public class MongoConfiguration
{
	/**
	 * Configure a {@link PlatformTransactionManager} compatible with mongo.
	 *
	 * @param dbFactory spring bean
	 * @return a transaction manager
	 */
	@Bean
	@Profile("!no-mongo-transaction")
	public PlatformTransactionManager transactionManager(MongoDatabaseFactory dbFactory)
	{
		return new MongoTransactionManager(dbFactory);
	}

	/**
	 * Configures a {@link MappingMongoConverter} without _class field.
	 *
	 * @param mongoDbFactory a {@link MongoDatabaseFactory}
	 * @param context a {@link MongoMappingContext}
	 * @return a {@link MappingMongoConverter}
	 */
	@Bean
	public MappingMongoConverter mappingMongoConverter(
		final MongoDatabaseFactory mongoDbFactory,
		final MongoMappingContext context)
	{

		DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory);
		MappingMongoConverter converter =
			new MappingMongoConverter(dbRefResolver, context);
		converter.setTypeMapper(new DefaultMongoTypeMapper(null));
		List<Converter<?, ?>> customConverters = new ArrayList<>();
		customConverters.add(new OffsetDateTimeReadConverter());
		customConverters.add(new OffsetDateTimeWriteConverter());
		customConverters.add(new OffsetDateTimeStringReadConverter());
		converter.setCustomConversions(
			new MongoCustomConversions(customConverters));
		converter.setMapKeyDotReplacement("#");
		return converter;
	}

	/**
	 * Converts OffsetDateTime to Date objects which
	 * in turn is stored in Mongo as ISODate
	 */
	private static class OffsetDateTimeWriteConverter implements Converter<OffsetDateTime, Date>
	{
		@Override
		public Date convert(OffsetDateTime source)
		{
			return Date.from(source.toInstant());
		}
	}

	/**
	 * Reads converts java.util.Date (the default read type for mongo ISODate
	 * formats) into
	 */
	private static class OffsetDateTimeReadConverter implements Converter<Date, OffsetDateTime>
	{
		@Override
		public OffsetDateTime convert(Date source)
		{
			return OffsetDateTime.ofInstant(source.toInstant(), ZoneOffset.UTC);
		}
	}

	/**
	 * Converter to remain compatible with entries writen as String prior to
	 * centralisation
	 */
	private static class OffsetDateTimeStringReadConverter implements Converter<String, OffsetDateTime>
	{
		@Override
		public OffsetDateTime convert(String source)
		{
			return OffsetDateTime.parse(source);
		}
	}
}
