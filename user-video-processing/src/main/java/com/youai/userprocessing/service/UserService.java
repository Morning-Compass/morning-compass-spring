package com.youai.userprocessing.service;

import com.youai.userprocessing.models.UserEntity;

public interface UserService {
    void setAccountVerified(String email);
    UserEntity getUserByEmail(String email);
    Boolean isAccountVerified(String email);
}
