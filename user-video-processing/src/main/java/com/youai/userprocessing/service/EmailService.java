package com.youai.userprocessing.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface EmailService {
    String sendEmail(String to, String subject, String body);
    String sendEmailWithAttachments(String to, String subject, String body, MultipartFile[] file);
    String sendEmailWithCC(String to, String[] cc, String subject, String body);
    String sendEmailWithCCandAttachments(String to, String[] cc, String subject, String body, MultipartFile[] file);

}
