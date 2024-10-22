package com.blogsite.auth.controller;

import com.blogsite.auth.entity.BlogUser;
import com.blogsite.auth.entity.OTP;
import com.blogsite.auth.repository.OTPRepository;
import com.blogsite.auth.enums.RoleEnum;
import com.blogsite.auth.payload.request.LoginRequest;
import com.blogsite.auth.payload.request.TwoFactorRequest;
import com.blogsite.auth.payload.response.JwtResponse;
import com.blogsite.auth.repository.BlogUserRepository;
import com.blogsite.auth.service.forgotpassword.OTPService;
import com.blogsite.auth.service.registration.RoleService;
import com.blogsite.auth.service.security.jwttokenutil.JwtTokenUtils;
import com.blogsite.auth.service.security.login.BlogUserDetailsImpl;
import com.blogsite.auth.service.security.twoFA.GoogleAuthUtil;
import com.blogsite.auth.service.security.twoFA.TwoFactorAuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1.0/blogsite/auth")
public class AuthenticationController {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);


    @Autowired
    private BlogUserRepository userRepository;

    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    RoleService roleService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtTokenUtils jwtUtils;

    @Autowired
    private TwoFactorAuthService twoFactorAuthService;

    @Autowired
    private GoogleAuthUtil googleAuthUtil;

    @Autowired
    private OTPService otpService;


    @PostMapping("/user/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Login process started........");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        BlogUserDetailsImpl userDetails = (BlogUserDetailsImpl) authentication.getPrincipal();
        BlogUser user = userRepository.findByEmail(userDetails.getEmail()).orElseThrow();
        boolean enabled2FA = user.isTwoFactorEnabled();
        logger.info("enabled2FA {}", enabled2FA);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                enabled2FA,
                roles));
    }



    @PostMapping("/user/2fa-enable")
    public ResponseEntity<String> enable2FA(@RequestBody TwoFactorRequest request) {
        BlogUser user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        System.out.println(user.getEmail() + "," + user.getSecret());
        boolean isCodeValid = twoFactorAuthService.verifyTOTP(user.getSecret(), request.getCode());
        if (isCodeValid) {
            return ResponseEntity.ok("Enable 2FA successful");
        }
        return ResponseEntity.status(403).body("Invalid 2FA code");
    }


    @PostMapping("/user/2fa-verify")
    public ResponseEntity<String> verify2FA(@RequestBody TwoFactorRequest request) {
        BlogUser user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        boolean isCodeValid = twoFactorAuthService.verifyTOTP(user.getSecret(), request.getCode());
        if (isCodeValid) {
            return ResponseEntity.ok("2FA successful");
        }
        return ResponseEntity.status(403).body("Invalid 2FA code");
    }


    @PutMapping("/user/{userId}/roles")
    public ResponseEntity<BlogUser> updateUserRoles(@PathVariable Long userId, @RequestBody Set<RoleEnum> roles) {
        BlogUser updatedUser = roleService.updateUserRoles(userId, roles);
        return ResponseEntity.ok(updatedUser);
    }



}