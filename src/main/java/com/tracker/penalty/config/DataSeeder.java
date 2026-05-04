package com.tracker.penalty.config;

import com.tracker.penalty.entity.Penalty;
import com.tracker.penalty.entity.PenaltyStatus;
import com.tracker.penalty.entity.Severity;
import com.tracker.penalty.entity.User;
import com.tracker.penalty.repository.PenaltyRepository;
import com.tracker.penalty.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class DataSeeder implements CommandLineRunner {

    private final PenaltyRepository penaltyRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(PenaltyRepository penaltyRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.penaltyRepository = penaltyRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Seed default admin user
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole("ROLE_ADMIN");
            userRepository.save(admin);
        }

        // Seed 30 Penalty Records
        if (penaltyRepository.count() == 0) {
            Severity[] severities = Severity.values();
            PenaltyStatus[] statuses = PenaltyStatus.values();
            Random random = new Random();

            for (int i = 1; i <= 30; i++) {
                Penalty penalty = new Penalty();
                penalty.setTitle("Sample Regulatory Penalty #" + i);
                penalty.setDescription("This is an automatically generated description for penalty #" + i + ". It details the violation code, audit logs, and compliance issues detected during the routine inspection.");
                penalty.setSeverity(severities[random.nextInt(severities.length)]);
                penalty.setStatus(statuses[random.nextInt(statuses.length)]);
                penaltyRepository.save(penalty);
            }
            
            System.out.println("Data Seeder: Successfully injected 30 dummy penalty records into the database.");
        }
    }
}
