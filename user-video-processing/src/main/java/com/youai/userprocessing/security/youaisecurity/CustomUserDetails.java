package com.youai.userprocessing.security.youaisecurity;

import com.youai.userprocessing.models.UserEntity;

public class CustomUserDetails extends org.springframework.security.core.userdetails.User {
    private final UserEntity userEntity;

    public CustomUserDetails(UserEntity userEntity) {
        super(userEntity.getUsername(), userEntity.getPassword(), MapToAuthorities.mapToAuthorities(userEntity.getRoles()));
        this.userEntity = userEntity;
    }

    public UserEntity getUserEntity() { return userEntity; }
}
