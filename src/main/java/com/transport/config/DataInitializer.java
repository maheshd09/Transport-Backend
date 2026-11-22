package com.transport.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.transport.entity.User;
import com.transport.repository.UserRepository;

@Configuration
public class DataInitializer {

	@Bean
    CommandLineRunner createAdmin(UserRepository repo, PasswordEncoder encoder,
                                  @Value("${app.admin.username:admin}") String username,
                                  @Value("${app.admin.password:Admin@12345}") String password) {
        return args -> {
            if (!repo.existsByUsername(username)) {
                User u = new User();
                u.setUsername(username);
                u.setPasswordHash(encoder.encode(password));
                u.setFullName("Administrator");
                u.setRole("ADMIN");
                repo.save(u);
                System.out.println(">>> Admin user created: " + username);
            }
        };
    }
}
