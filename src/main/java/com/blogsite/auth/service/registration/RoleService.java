package com.blogsite.auth.service.registration;

import com.blogsite.auth.entity.BlogUser;
import com.blogsite.auth.enums.RoleEnum;

import java.util.Set;

public interface RoleService {

    public BlogUser updateUserRoles(Long userId, Set<RoleEnum> roleEnums);
}
