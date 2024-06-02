package com.youai.userprocessing.dto.auth.login;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginUsernameDto {
    private String username;
    private String password;
}
