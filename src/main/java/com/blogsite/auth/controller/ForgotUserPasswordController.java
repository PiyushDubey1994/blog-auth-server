package com.blogsite.auth.controller;


import com.blogsite.auth.entity.BlogUser;
import com.blogsite.auth.entity.OTP;
import com.blogsite.auth.repository.BlogUserRepository;
import com.blogsite.auth.repository.OTPRepository;
import com.blogsite.auth.service.forgotpassword.OTPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1.0/blog/user")
public class ForgotUserPasswordController {
    private static final Logger logger = LoggerFactory.getLogger(ForgotUserPasswordController.class);
    @Autowired
    private BlogUserRepository userRepository;
    @Autowired
    private OTPRepository otpRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    private OTPService otpService;


    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email) {
        String decodedEmail = URLDecoder.decode(email, StandardCharsets.UTF_8);
       logger.info("Received email: " + decodedEmail); // Check the decoded email
        otpService.sendOTP(decodedEmail);
        return ResponseEntity.ok("OTP has been sent to your email.");
    }
    @PostMapping("/reset-password/verify-otp")
    public ResponseEntity<String> verifyOTP(@RequestParam String email, @RequestParam String otp) {
        String decodedEmail = URLDecoder.decode(email, StandardCharsets.UTF_8);
        Optional<OTP> otpEntity =  otpRepository.findByEmailAndOtp(decodedEmail.trim(), otp.trim());

        if (otpEntity.isPresent() && otpEntity.get().getExpirationTime().isAfter(LocalDateTime.now())) {
            return ResponseEntity.ok("OTP verified. You can now reset your password.");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired OTP.");
    }
    @PostMapping("/reset-password/confirm")
    public ResponseEntity<String> resetPasswordConfirm(
            @RequestParam String email,
            @RequestParam String newPassword,
            @RequestParam String otp) {
        String decodedEmail = URLDecoder.decode(email, StandardCharsets.UTF_8);
        Optional<OTP> otpEntity = otpRepository.findByEmailAndOtp(decodedEmail, otp);

        if (otpEntity.isPresent() && otpEntity.get().getExpirationTime().isAfter(LocalDateTime.now())) {
            Optional<BlogUser> userOptional = userRepository.findByEmail(decodedEmail);
            if (userOptional.isPresent()) {
                BlogUser user = userOptional.get();
                user.setPassword(encoder.encode(newPassword));// Update the password
                userRepository.save(user);     // Save the updated user entity
                return ResponseEntity.ok("Password updated successfully");

            } else {
                return ResponseEntity.ok("User not found");
            }

            //userRepository.save(BlogUser.builder().password(newPassword).build());

        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired OTP.");
    }

}
