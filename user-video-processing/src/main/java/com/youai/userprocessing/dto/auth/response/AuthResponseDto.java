package com.youai.userprocessing.dto.auth.response;

import com.youai.userprocessing.security.youaisecurity.CustomUserDetails;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
public class AuthResponseDto {
    private String accesToken;
    private String tokenType = "Bearer ";
    private String username;
    private String email;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;
    private boolean accountVerified;
    private String responseMessage;

    public AuthResponseDto(String accesToken, String responseMessage) {
        this.accesToken = accesToken;
        this.responseMessage = responseMessage;
    }

    public AuthResponseDto(String accesToken, String responseMessage, CustomUserDetails userDetails) {
        this.accesToken = accesToken;
        this.responseMessage = responseMessage;
        this.username = userDetails.getUsername();
        this.email = userDetails.getUserEntity().getEmail();
        this.authorities = userDetails.getAuthorities();
        this.accountNonExpired = userDetails.isAccountNonExpired();
        this.accountNonLocked = userDetails.isAccountNonLocked();
        this.credentialsNonExpired = userDetails.isCredentialsNonExpired();
        this.enabled = userDetails.isEnabled();
        this.accountVerified = userDetails.getUserEntity().isVerifiedAccount();
    }
}
