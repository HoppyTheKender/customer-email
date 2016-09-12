package com.rueckert.customer.email.config;

import java.util.Properties;

import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.Bean;

public class CloudConfig extends AbstractCloudConfig {
	public static final String CUSTOMER_TOPIC_NAME = "customer-topic";
	public static final String CUSTOMER_QUEUE_NAME = "customer-email-queue";

	@Bean
	public Properties cloudProperties() {
		return properties();
	}
}
