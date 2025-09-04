package com.maahi.spry.configServices;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
public class DbConfig {

@Value("${spring.data.mongodb.uri}")
String mongouri;

    @Bean
    public MongoDatabaseFactory getMongoDatabaseFactory(){
        return new SimpleMongoClientDatabaseFactory(mongouri);
    }
    @Bean
    public MongoTemplate getMongoTemplate(){
        return new MongoTemplate(getMongoDatabaseFactory());
    }
}
