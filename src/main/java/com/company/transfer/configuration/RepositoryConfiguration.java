package com.company.transfer.configuration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration // Annotates this to Spring Boot as a Configuration class 
@EnableAutoConfiguration // Enables this Spring Boot auto-configuration using below annotations 
@EntityScan(basePackages = {"com.company.transfer.domain"}) // Locate our entity classes
@EnableJpaRepositories(basePackages = {"com.company.transfer.repositories"}) // Locate our repositories
@EnableTransactionManagement // Enables Spring Boot annotated transaction management
public class RepositoryConfiguration {
	public RepositoryConfiguration() {
		super();
	}
}
