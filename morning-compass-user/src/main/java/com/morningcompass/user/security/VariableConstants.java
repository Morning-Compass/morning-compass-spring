package com.morningcompass.user.security;

public class VariableConstants {
    public static String generateVerificationEmailBody(String userName, String verificationLink) {
        return "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<style>" +
                "body {font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;}" +
                ".container {width: 100%; max-width: 600px; margin: 0 auto; padding: 20px; background-color: #ffffff; border: 1px solid #dddddd; border-radius: 5px;}" +
                ".header {background-color: #007BFF; color: #ffffff; padding: 10px 0; text-align: center; border-radius: 5px 5px 0 0;}" +
                ".header h1 {margin: 0; font-size: 24px;}" +
                ".content {padding: 20px; text-align: center;}" +
                ".content p {font-size: 16px; line-height: 1.5;}" +
                ".content a {display: inline-block; margin-top: 20px; padding: 10px 20px; font-size: 16px; color: #ffffff; background-color: #007BFF; text-decoration: none; border-radius: 5px;}" +
                ".footer {text-align: center; padding: 10px 0; font-size: 14px; color: #777777;}" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h1>Email Verification</h1>" +
                "</div>" +
                "<div class='content'>" +
                "<p>Hi " + userName + ",</p>" +
                "<p>Thank you for registering with us. Please click the button below to verify your email address:</p>" +
                "<a href='" + verificationLink + "'>Verify Email</a>" +
                "<p>If the button above does not work, copy and paste the following link into your web browser:</p>" +
                "<p><a href='" + verificationLink + "'>" + verificationLink + "</a></p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>&copy; " + java.time.Year.now().getValue() + " MorningCompass. All rights reserved.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}
