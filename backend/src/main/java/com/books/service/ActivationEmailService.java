package com.books.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivationEmailService {

    private final JavaMailSender javaMailSender;

    @Value("${app.activation.email.from:noreply@example.com}")
    private String fromEmail;

    @Value("${app.activation.email.subject:Activate your account}")
    private String emailSubject;

    @Value("${app.activation.email.base-url:http://localhost:3000}")
    private String activationBaseUrl;

    public MimeMessage buildActivationEmail(String to, String activationToken) {
        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException("Recipient email must not be null or blank");
        }

        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(emailSubject);

            String activationLink = activationBaseUrl + "/activate?token=" + activationToken;

            String htmlContent = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <style>
                            body { font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; }
                            .container { max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                            h1 { color: #333333; }
                            p { color: #555555; line-height: 1.6; }
                            .button { display: inline-block; background-color: #4CAF50; color: white; padding: 14px 28px; text-decoration: none; border-radius: 4px; margin-top: 10px; }
                            .footer { margin-top: 20px; font-size: 12px; color: #999999; }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <h1>Activate Your Account</h1>
                            <p>Thank you for registering. Please click the button below to activate your account:</p>
                            <a href="%s" class="button">Activate Account</a>
                            <p>If the button does not work, copy and paste this link into your browser:<br/>
                            <a href="%s">%s</a></p>
                            <div class="footer">
                                <p>This link will expire after 1 hour. If you did not create an account, please ignore this email.</p>
                            </div>
                        </div>
                    </body>
                    </html>
                    """.formatted(activationLink, activationLink, activationLink);

            helper.setText(htmlContent, true);

        } catch (MessagingException e) {
            log.error("Failed to build activation email for {}", to, e);
            throw new RuntimeException("Failed to build activation email", e);
        }

        return message;
    }

    public void sendActivationEmail(String to, String activationToken) {
        if (to == null || to.isBlank()) {
            log.warn("Cannot send activation email: no recipient email provided");
            return;
        }

        log.info("Sending activation email to {}", to);

        MimeMessage message = buildActivationEmail(to, activationToken);

        javaMailSender.send(message);

        log.info("Activation email sent successfully to {}", to);
    }
}
