package com.web.app;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class MyCodeReviewApplication {

	public static final String APPLICATION_LOCATIONS = "spring.config.location="
			+ "classpath:application.yml,"
			+ "file:/home/ubuntu/app/config/application-prod.yml,"
			+ "file:/home/ubuntu/app/config/application-prodoauth.yml";

	public static void main(String[] args) {
		new SpringApplicationBuilder(MyCodeReviewApplication.class)
				.properties(APPLICATION_LOCATIONS)
				.run(args);
	}

}