package com.morningcompass.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// todo: password verification while login
// todo: username/email validation
// todo: email generation jwt
// todo: token is generated based on email check

@SpringBootApplication
public class UserProcessingApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserProcessingApplication.class, args);
	}

}
