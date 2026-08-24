package com.pranit.docmind.authentication.service.impl;

import com.pranit.docmind.authentication.dto.SignupRequest;
import com.pranit.docmind.authentication.dto.SignupResponse;
import com.pranit.docmind.authentication.exception.EmailAlreadyExistsException;
import com.pranit.docmind.authentication.exception.PasswordNotMatchingException;
import com.pranit.docmind.authentication.exception.UsernameAlreadyExistsException;
import com.pranit.docmind.authentication.repository.UserRepository;
import com.pranit.docmind.authentication.service.RegistrationService;
import com.pranit.docmind.authorization.exception.RoleNotFoundException;
import com.pranit.docmind.authorization.repository.RoleRepository;
import com.pranit.docmind.authorization.service.UserRoleService;
import com.pranit.docmind.entities.constant.EmailPurpose;
import com.pranit.docmind.entities.entity.Role;
import com.pranit.docmind.entities.entity.User;
import com.pranit.docmind.otp.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private static final String USER_ROLE = "USER";
    private static final String ADMIN_ROLE = "ADMIN";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleService userRoleService;
    private final OtpService otpService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public SignupResponse createNewUserAccount(final SignupRequest request) {
        return registerAccount(request, USER_ROLE);
    }

    private SignupResponse registerAccount(final SignupRequest request, final String role) {
        validateUniqueUsernameAndEmail(request.username(), request.email());
        validatePasswordMatch(request.password(), request.confirmPassword());
        final EmailPurpose type = EmailPurpose.REGISTRATION;
        final User user = buildUser(request);
        final User saved = userRepository.save(user);
        userRoleService.addRoleToUser(saved.getUserId(), findRole(role));
        log.info("New account created successfully for username: {}", saved.getUsername());
        log.info("New account created successfully for username: {}", saved.getUsername());
        final String challengeId = sendOtp(saved, type);
        return SignupResponse.builder()
                .challengeId(challengeId)
                .message("Check email to verify account and activate it.")
                .build();
    }

    private void validateUniqueUsernameAndEmail(final String username, final String email) {
        if (userRepository.existsByUsername(username)) {
            log.warn("Username already taken: {}", username);
            throw new UsernameAlreadyExistsException("Username already taken");
        }

        if (userRepository.existsByEmail(email)) {
            log.warn("Email already taken: {}", email);
            throw new EmailAlreadyExistsException("Email already in use.");
        }
    }

    private void validatePasswordMatch(final String password, final String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            log.warn("Password confirmation mismatch");
            throw new PasswordNotMatchingException("Password and confirm password do not match");
        }
    }

    private User buildUser(final SignupRequest request) {
        return User.builder()
                .fullName(request.fullName())
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .enabled(false)
                .deleted(false)
                .build();
    }

    private Role findRole(final String roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> {
                    log.warn("Role not found with name: {}", roleName);
                    return new RoleNotFoundException("Role not found");
                });
    }

    private String sendOtp(final User saved, final EmailPurpose purpose) {
        log.info("Send Email for account activation");
        return otpService.sendOtp(saved.getEmail(), purpose);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public SignupResponse createNewAdminAccount(final SignupRequest request) {
        return registerAccount(request, ADMIN_ROLE);
    }

}