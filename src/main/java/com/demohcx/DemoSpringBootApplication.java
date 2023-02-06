package com.demohcx;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoSpringBootApplication {

	private static Logger logger = LogManager.getLogger(DemoSpringBootApplication.class);
	public static void main(String[] args) {
        logger.info("hi info");
		logger.debug("hey debug");
		logger.error("hey error");
		logger.trace("hey trace");
		SpringApplication.run(DemoSpringBootApplication.class, args);
	}


}
