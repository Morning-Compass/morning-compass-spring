package com.youai.userprocessing.dto.auth.register;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResendEmailDto {
    private String email;
}
