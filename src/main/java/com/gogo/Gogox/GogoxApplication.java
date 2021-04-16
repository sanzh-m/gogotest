package com.gogo.Gogox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GogoxApplication {

	public static void main(String[] args) {
		SpringApplication.run(GogoxApplication.class, args);
	}

}
