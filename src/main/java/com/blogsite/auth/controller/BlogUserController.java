package com.blogsite.auth.controller;


import com.blogsite.auth.entity.BlogUser;
import com.blogsite.auth.entity.Role;
import com.blogsite.auth.exception.MessageResponse;
import com.blogsite.auth.service.registration.BlogUserRegistrationService;
import com.blogsite.auth.service.security.twoFA.GoogleAuthUtil;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1.0/blogsite/auth")
public class BlogUserController {

    private static final Logger logger = LoggerFactory.getLogger(BlogUserController.class);

    @Autowired
    private BlogUserRegistrationService blogUserRegistrationService;

    @Autowired
    PasswordEncoder encoder;
    @Autowired
    private GoogleAuthUtil googleAuthUtil;


    @PostMapping("/user/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody BlogUser user) {
        if (blogUserRegistrationService.existsByUsername(user.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!", null));
        }

        if (blogUserRegistrationService.existsByEmail(user.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!", null));
        }
        user.setPassword(encoder.encode(user.getPassword()));
        if (user.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            Role role = new Role();
            role.setId(1L);
            roles.add(role);
            user.setRoles(roles);
        }else {
            user.setRoles(user.getRoles());
        }

        GoogleAuthenticatorKey credentials = googleAuthUtil.createCredentials();
        user.setSecret(credentials.getKey());
        String qrcode = null;
        blogUserRegistrationService.createUser(user);
        if (user.isTwoFactorEnabled()) {
            qrcode = googleAuthUtil.getQRCode(credentials);
        }
        return ResponseEntity.ok(new MessageResponse("User registered successfully!", qrcode));
    }


    @GetMapping("/user/{id}")
    public BlogUser getUserById(@PathVariable Long id) {
        return blogUserRegistrationService.findById(id);
    }

    @GetMapping("/user/all")
    public List<BlogUser> getAllUser() {
        return blogUserRegistrationService.findAll();
    }

    @DeleteMapping("/user/delete/{id}")
    public ResponseEntity<Boolean> deleteBlog(@PathVariable Long id) {
        Boolean updated = blogUserRegistrationService.deleteBlogUser(id);
        return ResponseEntity.ok(updated);
    }

}
