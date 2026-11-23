package com.transport.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.transport.entity.User;
import com.transport.repository.UserRepository;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner initData(UserRepository userRepo, 
                              PasswordEncoder encoder,
                              @Value("${app.admin.username:admin}") String username,
                              @Value("${app.admin.password:admin123}") String password) {
        return args -> {
            // Ensure default Administrator account exists
            if (!userRepo.existsByUsername(username)) {
                User u = new User();
                u.setUsername(username);
                u.setPasswordHash(encoder.encode(password));
                u.setFullName("Administrator");
                u.setRole("ADMIN");
                userRepo.save(u);
                log.info("Administrator account initialized: {}", username);
            }
        };
    }
}

