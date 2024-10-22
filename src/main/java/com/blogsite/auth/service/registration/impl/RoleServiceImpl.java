package com.blogsite.auth.service.registration.impl;

import com.blogsite.auth.entity.BlogUser;
import com.blogsite.auth.entity.Role;
import com.blogsite.auth.enums.RoleEnum;
import com.blogsite.auth.repository.BlogUserRepository;
import com.blogsite.auth.repository.RoleRepository;
import com.blogsite.auth.service.registration.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {

    private static final Logger logger = LoggerFactory.getLogger(RoleService.class);
    private final BlogUserRepository blogUserRepository;
    private final RoleRepository roleRepository;

    public RoleServiceImpl(BlogUserRepository blogUserRepository, RoleRepository roleRepository) {
        this.blogUserRepository = blogUserRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    @Override
    public BlogUser updateUserRoles(Long userId, Set<RoleEnum> roleEnums) {
        Optional<BlogUser> userOpt = blogUserRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        BlogUser user = userOpt.get();
        Set<Role> roles = user.getRoles();
        // Clear existing roles
        roles.clear();
        // Add new roles
        for (RoleEnum roleEnum : roleEnums) {
            Role role = roleRepository.findByName(roleEnum)
                    .orElseGet(() -> roleRepository.save(new Role(roleEnum)));
            roles.add(role);
        }
        return blogUserRepository.save(user);
    }
}
