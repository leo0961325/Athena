package com.athena.datasource.config.mongo;

import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoAuditing
@EnableMongoRepositories(
    basePackages = "com.athena.datasource.mongo",
    excludeFilters =
        @ComponentScan.Filter(type = FilterType.ANNOTATION))
@Configuration
@Profile("!test")
public class CoreMongoDbConfig {

  @Primary
  @Bean
  @ConfigurationProperties(prefix = "spring.data.mongodb")
  public MongoProperties primaryProperties() {
    return new MongoProperties();
  }

  @Primary
  @Bean
  public MongoTemplate mongoTemplate(
      MongoDatabaseFactory primaryFactory, MongoCustomConversions customMongoConversions) {
    MongoTemplate mongoTemplate = new MongoTemplate(primaryFactory);
    MappingMongoConverter converter = (MappingMongoConverter) mongoTemplate.getConverter();
    // Add custom converters
    converter.setCustomConversions(customMongoConversions);
    // Prevent mongoDB from adding type information to a document
    converter.setTypeMapper(new DefaultMongoTypeMapper(null));
    converter.afterPropertiesSet();
    return mongoTemplate;
  }

  @Primary
  @Bean
  public MongoDatabaseFactory primaryFactory(MongoProperties primaryProperties) {
    return new SimpleMongoClientDatabaseFactory(primaryProperties.getUri());
  }
}
