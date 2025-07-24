package com.bkb.scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableJpaAuditing
@EnableAsync

// Main Application Class
@SpringBootApplication
public class BkbScannerApplication {
	public static void main(String[] args) {
		SpringApplication.run(BkbScannerApplication.class, args);
	}
}