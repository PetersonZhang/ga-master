package com.scb.ga.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * @author David Wang
 * 
 *
 * 
 */
@Configuration
@EnableConfigurationProperties(DataSourceProperties.class)
public class DruidDataSourceConfig {

	/**
	 * Use druid as spring datasource
	 */
	@Bean(name = "dataSource")
	@ConfigurationProperties("spring.datasource.druid")
	public DruidDataSource dataSource(DataSourceProperties properties) {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setDriverClassName(properties.determineDriverClassName());
		dataSource.setUrl(properties.determineUrl());
		dataSource.setUsername(properties.determineUsername());
		dataSource.setPassword(properties.determinePassword());
		DatabaseDriver databaseDriver = DatabaseDriver.fromJdbcUrl(properties.determineUrl());
		String validationQuery = databaseDriver.getValidationQuery();
		if (validationQuery != null) {
			dataSource.setTestOnBorrow(true);
			dataSource.setValidationQuery(validationQuery);
		}
		return dataSource;
	}
}
