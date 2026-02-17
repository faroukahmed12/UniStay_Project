package com.unistay.housing_management_system;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UniversityHousingManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(UniversityHousingManagementSystemApplication.class, args);


	}

    @Bean
    public CommandLineRunner commandLineRunner ()
    {
        return runner -> {

        };
    }


}
