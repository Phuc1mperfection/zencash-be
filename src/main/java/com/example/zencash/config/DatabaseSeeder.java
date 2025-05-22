package com.example.zencash.config;


import com.example.zencash.entity.User;
import com.example.zencash.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DatabaseSeeder {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Check if admin user exists
            // if (userRepository.findByUsername("admin").isEmpty()) {
            //     User admin = new User();
            //     admin.setEmail("admin@gmail.com");
            //     admin.setPassword(passwordEncoder.encode("admin123"));
            //     admin.setUsername("admin");
            //     admin.setFullname("Admin");
            //     admin.setRoles(Set.of("ADMIN"));
            //     admin.setActive(true);
            //     userRepository.save(admin);
            //     System.out.println("Admin user created successfully");
            // }
        };
    }
}