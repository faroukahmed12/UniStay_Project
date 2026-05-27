package com.unistay.housing_management_system;

import com.unistay.housing_management_system.Repository.UserRepository;
import com.unistay.housing_management_system.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class TestRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {


            /*User user = userRepository
                    .findByEmail("ahmed.ali@unistay.com")
                    .orElseThrow();

            String dbPassword = user.getPassword();

            System.out.println("DB Password: " + dbPassword);

            System.out.println(
                    passwordEncoder.matches("123456", dbPassword)
            );


            System.out.println("-----------------");

            String password = new BCryptPasswordEncoder().encode("123456");
            System.out.println(password);*/
        };
    }
}
