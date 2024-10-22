package com.blogsite.auth.service.forgotpassword;

import com.blogsite.auth.entity.OTP;
import com.blogsite.auth.repository.OTPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OTPService {

    @Autowired
    private OTPRepository otpRepository; // Repository for storing OTP
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendOTP(String email) {
        String otp = generateOTP();// Generate a random OTP
        LocalDateTime expirationTime=LocalDateTime.now().plusMinutes(10);
        OTP otpEntity = new OTP();
        otpEntity.setEmail(email);
        otpEntity.setOtp(otp);
        otpEntity.setExpirationTime(expirationTime);
        otpRepository.save(otpEntity);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("dataemission@gmail.com");
        message.setTo(email);
        message.setSubject("Password Reset OTP");
        message.setText("Your OTP for password reset is: " + otp);
        javaMailSender.send(message);
    }



    private String generateOTP() {
        return String.valueOf(new Random().nextInt(999999)+ 100000); // Generate a 6-digit OTP
    }
}
