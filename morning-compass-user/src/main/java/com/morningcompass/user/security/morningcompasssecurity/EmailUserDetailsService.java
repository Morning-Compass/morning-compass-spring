package com.morningcompass.user.security.morningcompasssecurity;

import com.morningcompass.user.models.UserEntity;
import com.morningcompass.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class EmailUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    public EmailUserDetailsService(UserRepository userRepository) { this.userRepository = userRepository; }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(("User not found with email: " + email)));
        return new CustomUserDetails(user);
    }
}
