package com.morningcompass.user.service;

import com.morningcompass.user.dto.auth.response.ConfirmationTokenDto;
import com.morningcompass.user.models.ConfirmationToken;
import com.morningcompass.user.models.UserEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ConfirmationTokenService {
    void saveConfirmationToken(ConfirmationTokenDto confirmationTokenDto);
    Optional<ConfirmationToken> getToken(String token);
    @Deprecated
    @Transactional
    void confirm(String token, String email);
    @Transactional
    void confirmToken(ConfirmationToken confirmationToken);
    void createAndSaveConfirmationToken(UserEntity userEntity, boolean resend);
    String sendVerificationEmail(String email, String username, String token);
}
