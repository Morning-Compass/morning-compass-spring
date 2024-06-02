package com.youai.userprocessing.dto.auth.login;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginEmailDto {
    private String email;
    private String password;
}
