package com.pranit.docmind.authentication.seed;

import com.pranit.docmind.authentication.repository.UserRepository;
import com.pranit.docmind.authorization.exception.RoleNotFoundException;
import com.pranit.docmind.authorization.repository.RoleRepository;
import com.pranit.docmind.authorization.service.UserRoleService;
import com.pranit.docmind.constant.UserMetadata;
import com.pranit.docmind.entities.entity.Role;
import com.pranit.docmind.entities.entity.User;
import com.pranit.docmind.properties.AdminSeedProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminSeedProperties properties;
    private final RoleRepository roleRepository;
    private final UserRoleService userRoleService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void run(String @NonNull ... args) {
        if (userRepository.existsByUsername(properties.username())) {
            log.info("Admin user already exists: {}", properties.username());
            return;
        }
        User user = User.builder()
                .username(properties.username())
                .password(passwordEncoder.encode(properties.password()))
                .email("pranitbhangale07@gmail.com")
                .fullName("Pranit Bhangale")
                .enabled(true)
                .deleted(false)
                .build();
        user = userRepository.save(user);
        userRoleService.addRoleToUser(user.getUserId(), findRole());
        log.info("Admin user seeded successfully: {}", user.getUsername());
    }

    private Role findRole() {
        return roleRepository.findByRoleName(UserMetadata.ADMIN_ROLE)
                .orElseThrow(() -> {
                    log.warn("Role not found with name: {}", UserMetadata.ADMIN_ROLE);
                    return new RoleNotFoundException("Role not found");
                });
    }
}
