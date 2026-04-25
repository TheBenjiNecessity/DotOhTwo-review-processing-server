package com.dotohtwo.review_processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.cassandra.autoconfigure.CassandraAutoConfiguration;
import org.springframework.boot.data.cassandra.autoconfigure.DataCassandraAutoConfiguration;
import org.springframework.boot.data.cassandra.autoconfigure.DataCassandraReactiveAutoConfiguration;
import org.springframework.boot.data.cassandra.autoconfigure.DataCassandraReactiveRepositoriesAutoConfiguration;
import org.springframework.boot.data.cassandra.autoconfigure.DataCassandraRepositoriesAutoConfiguration;

@SpringBootApplication(exclude = {
        CassandraAutoConfiguration.class,
        DataCassandraAutoConfiguration.class,
        DataCassandraReactiveAutoConfiguration.class,
        DataCassandraRepositoriesAutoConfiguration.class,
        DataCassandraReactiveRepositoriesAutoConfiguration.class
})
public class ReviewProcessorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReviewProcessorApplication.class, args);
	}

}
