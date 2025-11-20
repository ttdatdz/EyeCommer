package com.eyecommer.Backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine springTemplateEngine;

    @Value("${spring.mail.from}")
    private String emailFrom;



    public String sendEmail(String recipients, String subject, String content, MultipartFile[] files) throws UnsupportedEncodingException,  MessagingException {
        log.info("Email is sending ...");

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(emailFrom, "Dat Tran Demo");

        if (recipients.contains(",")) { // send to multiple users
            helper.setTo(InternetAddress.parse(recipients));
        } else { // send to single user
            helper.setTo(recipients);
        }

        // Send attach files
        if (files != null) {
            for (MultipartFile file : files) {
                helper.addAttachment(Objects.requireNonNull(file.getOriginalFilename()), file);
            }
        }

        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(message);

        log.info("Email has sent to successfully, recipients: {}", recipients);

        return "Sent";
    }

    public void sendConfirmLink(String email, Long id, String s) throws MessagingException, UnsupportedEncodingException {
        log.info("Email is sending confirm link ...");

        //tạo email trống
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,"UTF-8");

        //Chuẩn bị data muốn truyền vào template
        String linkConfirm = String.format("http://localhost:8086/user/confirm/%s?verifyCode=%s",id,s);
        Map<String, Object> properties = new HashMap<>();
        properties.put("linkConfirm",linkConfirm);

        //gắn data vào context. để thymeleaf có thể lấy data từ đây
        Context context = new Context();
        context.setVariables(properties);

        //render template tạo ra chuỗi html
        String html =springTemplateEngine.process("confirm-email",context);

        //cấu hình mail(người gửi + chủ đề+ ng nhận + nội dung)
        helper.setFrom(emailFrom,"Dat Tran Demo");
        helper.setTo(email);
        helper.setSubject("Please confirm your account");
        helper.setText(html, true);
        //gửi mail đi
        mailSender.send(message);
        log.info("Email has sent confirm link successfully, email: {}", email);
    }

    public void sendResetPasswordLink(String email, String resetLink)
            throws MessagingException, UnsupportedEncodingException {
        log.info("Sending reset password email...");

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper =
                new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

        // Gắn dữ liệu vào template (Thymeleaf)
        Map<String, Object> variables = new HashMap<>();
        variables.put("resetLink", resetLink);
        Context context = new Context();
        context.setVariables(variables);

        // Render HTML từ template "reset-password-email.html"
        String html = springTemplateEngine.process("reset-password-email", context);

        helper.setFrom(emailFrom, "Dat Tran Demo");
        helper.setTo(email);
        helper.setSubject("Reset your password");
        helper.setText(html, true);

        mailSender.send(message);
        log.info("Reset password email sent successfully to {}", email);
    }

}
