package com.resolvedd.authenticatedd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:jdbc.properties")
public class AuthenticateddApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthenticateddApplication.class, args);
	}
}
