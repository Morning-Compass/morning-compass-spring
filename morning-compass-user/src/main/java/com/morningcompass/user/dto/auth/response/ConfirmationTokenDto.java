package com.morningcompass.user.dto.auth.response;

import com.morningcompass.user.models.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfirmationTokenDto {
    private UserEntity user;
    private String token;
    private Date createdAt;
    private Date expiresAt;
}
