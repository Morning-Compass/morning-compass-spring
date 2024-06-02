package com.youai.userprocessing.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.youai.userprocessing.dto.auth.login.LoginEmailDto;
import com.youai.userprocessing.dto.auth.login.LoginUsernameDto;
import com.youai.userprocessing.dto.auth.register.RegisterDto;
import com.youai.userprocessing.dto.auth.register.ResendEmailDto;
import com.youai.userprocessing.dto.auth.response.AuthResponseDto;
import com.youai.userprocessing.models.UserEntity;
import com.youai.userprocessing.models.role.Role;
import com.youai.userprocessing.repository.RoleRepository;
import com.youai.userprocessing.repository.UserRepository;
import com.youai.userprocessing.security.SecurityConstants;
import com.youai.userprocessing.security.youaisecurity.*;
import com.youai.userprocessing.security.jwt.JwtTokenGenerator;
import com.youai.userprocessing.service.impl.ConfirmationTokenServiceImpl;
import com.youai.userprocessing.service.impl.JsonFileReader;
import com.youai.userprocessing.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import java.net.URISyntaxException;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailUserDetailsService emailUserDetailsService;
    private final UsernameUserDetailsService usernameUserDetailsService;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final ConfirmationTokenServiceImpl confirmationTokenServiceImpl;
    private final UserServiceImpl userServiceImpl;
    private final JsonFileReader jsonFileReader;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository, RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder,
                          @Qualifier("emailUserDetailsService") EmailUserDetailsService emailUserDetailsService,
                          @Qualifier("usernameUserDetailsService") UsernameUserDetailsService usernameUserDetailsService,
                          JwtTokenGenerator jwtTokenGenerator,
                          ConfirmationTokenServiceImpl confirmationTokenServiceImpl,
                          UserServiceImpl userServiceImpl,
                          JsonFileReader jsonFileReader
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailUserDetailsService = emailUserDetailsService;
        this.usernameUserDetailsService = usernameUserDetailsService;
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.confirmationTokenServiceImpl = confirmationTokenServiceImpl;
        this.userServiceImpl = userServiceImpl;
        this.jsonFileReader = jsonFileReader;
    }

    private final JsonResponse jsonResponse = new JsonResponse();

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        try {
            JsonNode registration = jsonFileReader.read("api-response.json").path("auth").path("register");
            JsonNode registrationSuccess = registration.path("auth_register_success");
            JsonNode registrationError = registration.path("auth_register_error");

            if (userRepository.existsByUsername(registerDto.getUsername()) || userRepository.existsByEmail(registerDto.getEmail()))
                return jsonResponse.Generate(registrationSuccess.path("key").asText(), registrationSuccess.path("message").asText(), HttpStatus.BAD_REQUEST);

            if (!LoginValidator.validUsername(registerDto.getUsername()) && !LoginValidator.validEmail(registerDto.getEmail()))
                return jsonResponse.Generate(registrationSuccess.path("key").asText(), registrationSuccess.path("message").asText(), HttpStatus.BAD_REQUEST);

            Role roles = roleRepository.findByName("USER").get();
            UserEntity user = UserEntity.builder()
                    .username(registerDto.getUsername())
                    .email(registerDto.getEmail())
                    .password(passwordEncoder.encode(registerDto.getPassword()))
                    .roles(Collections.singletonList(roles))
                    .verifiedAccount(false)
                    .createdAt(new Date())
                    .build();

            userRepository.save(user);
            Date currentDate = new Date();
            Date expirationDate = new Date(currentDate.getTime() + SecurityConstants.CONFIRMATION_TOKEN_EXPIRATION);
            confirmationTokenServiceImpl.createAndSaveConfirmationToken(user, false);
            return jsonResponse.Generate(registrationSuccess.path("key").asText(), registrationSuccess.path("message").asText(), HttpStatus.OK);
        }
        catch (IOException | URISyntaxException e) {
            return new ResponseEntity<>("An error occurred while processing the register request", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // change createAndSave to some function sending emial

    public ResponseEntity<String> validateAccount(@PathVariable String token) {
        try {
            JsonNode validation = jsonFileReader.read("api-response.json").path("auth").path("validate");
            JsonNode validationSuccess = validation.path("account_validation_success");
            JsonNode validationError = validation.path("account_validation_error");

            try {
                confirmationTokenServiceImpl.getToken(token).ifPresentOrElse(confirmationToken -> {
                    Date currentDate = new Date();
                    if (currentDate.after(confirmationToken.getExpiresAt())) {
                        confirmationTokenServiceImpl.createAndSaveConfirmationToken(confirmationToken.getUser(), false);
                        throw new BadCredentialsException("Token has expired, new one created");
                    }

                    confirmationTokenServiceImpl.confirmToken(confirmationToken);
                }, () -> {
                    throw new BadCredentialsException("Invalid token");
                });

                return jsonResponse.Generate(validationSuccess.path("key").asText(), validationSuccess.path("message").asText(), HttpStatus.OK);
            } catch (BadCredentialsException e) {
                return jsonResponse.Generate(validationError.path("key").asText(), validationError.path("message").asText() + ", \n" + e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        } catch (IOException | URISyntaxException e) {
            return new ResponseEntity<>("An error occurred while processing the validating account request", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/resend/email")
    public ResponseEntity<String> resendTokenByRequestEmail(@RequestBody ResendEmailDto resendEmailDto) {
        try {
            JsonNode resend = jsonFileReader.read("api-response.json").path("auth").path("email_resend");
            JsonNode resendSuccess = resend.path("email_resend_success");
            JsonNode renderError = resend.path("email_resend_error");

            CustomUserDetails userDetails = (CustomUserDetails) emailUserDetailsService.loadUserByUsername(resendEmailDto.getEmail());
            if (userServiceImpl.isAccountVerified(resendEmailDto.getEmail())) {
                return jsonResponse.Generate(renderError.path("key").asText(), renderError.path("message").asText(), HttpStatus.BAD_REQUEST);
            }

            confirmationTokenServiceImpl.createAndSaveConfirmationToken(userDetails.getUserEntity(), true);
            return jsonResponse.Generate(resendSuccess.path("key").asText(), resendSuccess.path("message").asText(), HttpStatus.OK);
        } catch (IOException | URISyntaxException e) {
            return new ResponseEntity<>("An error occurred while processing the emeil resend request", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login/username")
    public ResponseEntity<String> loginUsername(@RequestBody LoginUsernameDto loginUsernameDto) throws IOException, URISyntaxException {
        JsonNode loginUsername = jsonFileReader.read("api-response.json").path("auth").path("login_username");
        JsonNode loginUsernameSuccess = loginUsername.path("login_username_success");
        JsonNode loginUsernameError = loginUsername.path("login_username_error");
        try {
            CustomUserDetails userDetails = (CustomUserDetails) usernameUserDetailsService.loadUserByUsername(loginUsernameDto.getUsername());

            if(!passwordEncoder.matches(loginUsernameDto.getPassword(), userDetails.getPassword())) throw new BadCredentialsException("");

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                loginUsernameDto.getPassword(),
                userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenGenerator.generateToken(authentication);
            return jsonResponse.Generate(loginUsernameSuccess.path("key").asText(), new AuthResponseDto(token, loginUsernameSuccess.path("message").asText(), userDetails), HttpStatus.OK) ;
        }
        catch (BadCredentialsException | UsernameNotFoundException e) {return jsonResponse.Generate(loginUsernameError.path("key").asText(), new AuthResponseDto(null, loginUsernameError.path("message").asText()), HttpStatus.BAD_REQUEST ) ;}
        catch (AuthenticationException e) {return jsonResponse.Generate(loginUsernameError.path("key").asText(), new AuthResponseDto(null, loginUsernameError.path("message").asText()), HttpStatus.BAD_REQUEST );} catch (
                IOException e) {
            return new ResponseEntity<>("{\"error\": \"" + "Logging in went wrong: \n" + e.getMessage() + "\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login/email")
    public ResponseEntity<String> loginEmail(@RequestBody LoginEmailDto loginEmailDto) throws IOException, URISyntaxException {
        JsonNode loginEmail = jsonFileReader.read("api-response.json").path("auth").path("login_email");
        JsonNode loginEmailSuccess = loginEmail.path("login_email_success");
        JsonNode loginEmailError = loginEmail.path("login_email_error");
        try {
            CustomUserDetails userDetails = (CustomUserDetails) emailUserDetailsService.loadUserByUsername(loginEmailDto.getEmail());

            if(!passwordEncoder.matches(loginEmailDto.getPassword(), userDetails.getPassword())) throw new BadCredentialsException("");

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    loginEmailDto.getPassword(),
                    userDetails.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenGenerator.generateToken(authentication);
            return jsonResponse.Generate(loginEmailSuccess.path("key").asText(), new AuthResponseDto(token, loginEmailSuccess.path("message").asText(), userDetails), HttpStatus.OK) ;
        }
//        catch (BadCredentialsException | UsernameNotFoundException e) { return new ResponseEntity<>(new AuthResponseDto(null, "Invalid email or password"), HttpStatus.UNAUTHORIZED); }
        catch (BadCredentialsException | UsernameNotFoundException e) {return jsonResponse.Generate(loginEmailError.path("key").asText(), new AuthResponseDto(null, loginEmailError.path("message").asText()), HttpStatus.BAD_REQUEST ) ;}
        catch (AuthenticationException e) {return jsonResponse.Generate(loginEmailError.path("key").asText(), new AuthResponseDto(null, loginEmailError.path("message").asText()), HttpStatus.BAD_REQUEST );} catch (
                IOException e) {
            return new ResponseEntity<>("{\"error\": \"" + "Logging in went wrong: \n" + e.getMessage() + "\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
