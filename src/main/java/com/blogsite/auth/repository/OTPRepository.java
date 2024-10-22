package com.blogsite.auth.repository;

import com.blogsite.auth.entity.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OTPRepository extends JpaRepository<OTP, Long> {
    Optional<OTP> findByEmailAndOtp(String email, String otp);
}
