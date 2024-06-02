package com.youai.userprocessing.service.impl;

import com.youai.userprocessing.dto.auth.response.ConfirmationTokenDto;
import com.youai.userprocessing.models.ConfirmationToken;
import com.youai.userprocessing.models.UserEntity;
import com.youai.userprocessing.repository.ConfirmationTokenRepository;
import com.youai.userprocessing.repository.UserRepository;
import com.youai.userprocessing.security.SecurityConstants;
import com.youai.userprocessing.security.VariableConstants;
import com.youai.userprocessing.service.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final ConfirmationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailServiceImpl emailService;

    public void saveConfirmationToken(ConfirmationTokenDto confirmationTokenDto) {
        tokenRepository.save(mapToEntity(confirmationTokenDto));
    }

    public Optional<ConfirmationToken> getToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

//    public Optional<UserEntity> getTokenAndEmail(String confirmationToken) {
//        return confirmationTokenRepository.findEmailByToken(confirmationToken);
//    }

    public void confirm(String token, String email) {
        confirmationTokenRepository.updateConfirmedAt(token, new Date());
        userRepository.updateUserVerifiedAccount(email);
    }


    public void confirmToken(ConfirmationToken confirmationToken) {
        confirmationTokenRepository.updateConfirmedAt(confirmationToken.getToken(), new Date());
        userRepository.updateUserVerifiedAccount(confirmationToken.getUser().getEmail());
    }

    public void createAndSaveConfirmationToken(UserEntity userEntity, boolean resend) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + SecurityConstants.CONFIRMATION_TOKEN_EXPIRATION);
        ConfirmationTokenDto confirmationToken = ConfirmationTokenDto.builder()
                .user(userEntity)
                .token(UUID.randomUUID().toString())
                .createdAt(now)
                .expiresAt(expirationDate)
                .build();
        confirmationTokenRepository.save(mapToEntity(confirmationToken));
        if(!resend)
            sendVerificationEmail(userEntity.getEmail(), userEntity.getUsername(), confirmationToken.getToken());
        else
            resendVerificationEmail(userEntity.getEmail(), userEntity.getUsername(), confirmationToken.getToken());
    }

    public String sendVerificationEmail(String email, String username, String token) {
        String subject = "Verify Your YouAi Account";
        String body = VariableConstants.generateVerificationEmailBody(username, SecurityConstants.CURRENT_CLIENT_LOCATION + "/validate/" + token);
        emailService.sendEmail(email, subject, body);
        System.out.println("Email sent");
        return "Mail with verification link sent successfully";
    }

    public String resendVerificationEmail(String token, String username, String email) {
        String subject = "Verify Your YouAi Account";
        String body = VariableConstants.generateVerificationEmailBody(username, SecurityConstants.CURRENT_CLIENT_LOCATION + "/validate/" + token);
        emailService.sendEmail(email, subject, body);
        System.out.println("Email sent");
        return "Mail with verification link sent successfully";
    }

    private ConfirmationToken mapToEntity(ConfirmationTokenDto confirmationTokenDto) {
        return ConfirmationToken.builder()
                .user(confirmationTokenDto.getUser())
                .token(confirmationTokenDto.getToken())
                .createdAt(confirmationTokenDto.getCreatedAt())
                .expiresAt(confirmationTokenDto.getExpiresAt())
                .confirmedAt(confirmationTokenDto.getExpiresAt())
        .build();
    }
}
