package com.morningcompass.user.security.morningcompasssecurity;

import java.util.regex.Pattern;

public class LoginValidator {
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{8,20}");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

    public static boolean validUsername(String username) { return USERNAME_PATTERN.matcher(username).matches(); }
    public static boolean validEmail(String email) { return EMAIL_PATTERN.matcher(email).matches(); }
}
