package com.zhi.claims_analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClaimsAnalyticsApplication {

	public static void main(String[] args) {
		//curl.exe -X POST -F "file=@claims.csv" http://localhost:8080/claims/upload
		SpringApplication.run(ClaimsAnalyticsApplication.class, args);
	}

}
