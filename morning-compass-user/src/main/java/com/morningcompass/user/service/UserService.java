package com.morningcompass.user.service;

import com.morningcompass.user.models.UserEntity;

public interface UserService {
    void setAccountVerified(String email);
    UserEntity getUserByEmail(String email);
    Boolean isAccountVerified(String email);
}
