package com.morningcompass.user.service.impl;

import com.morningcompass.user.models.UserEntity;
import com.morningcompass.user.repository.UserRepository;
import com.morningcompass.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setAccountVerified(String email) {
        Optional<UserEntity> user = userRepository.findByEmail(email);
        user.ifPresent(usr -> {
            usr.setVerifiedAccount(true);
            userRepository.save(usr);
        });

    }

    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Email not found"));
    }

    public Boolean isAccountVerified(String email) {
        Optional<UserEntity> user = userRepository.findByEmail(email);
        return user.map(UserEntity::isVerifiedAccount).orElse(false);
    }
}
