package com.ops.app.courseregistration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class Team1AppApplication {

	public static void main(String[] args) {
		SpringApplication.run(Team1AppApplication.class, args);
	}

}
