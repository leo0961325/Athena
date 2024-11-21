package com.athena.datasource.config.mysql;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableJpaAuditing
@Configuration
// Using AdviceMode.ASPECTJ provides @Transaction support on private methods
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
@EnableJpaRepositories(basePackages = "com.athena.datasource.jdbc")
@EntityScan(basePackages = "com.athena")
@Profile("!test")
public class MySqlDataSourceConfig {}
