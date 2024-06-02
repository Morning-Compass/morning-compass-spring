package com.youai.userprocessing.dto.auth.register;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterDto {
    private String username;
    private String email;
    private String password;
}
