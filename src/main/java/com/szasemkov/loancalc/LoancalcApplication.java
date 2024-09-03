package com.szasemkov.loancalc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LoancalcApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoancalcApplication.class, args);
	}

}
