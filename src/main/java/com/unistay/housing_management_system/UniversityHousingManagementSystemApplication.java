package com.unistay.housing_management_system;

import com.unistay.housing_management_system.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class UniversityHousingManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(UniversityHousingManagementSystemApplication.class, args);


	}




}
