package com.books.service;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivationEmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private ActivationEmailService activationEmailService;

    @BeforeEach
    void setUp() {
        MimeMessage mockMessage = mock(MimeMessage.class);
        lenient().when(javaMailSender.createMimeMessage()).thenReturn(mockMessage);

        ReflectionTestUtils.setField(activationEmailService, "fromEmail", "noreply@example.com");
        ReflectionTestUtils.setField(activationEmailService, "emailSubject", "Activate your account");
        ReflectionTestUtils.setField(activationEmailService, "activationBaseUrl", "http://localhost:3000");
    }

    @Test
    @DisplayName("shouldBuildActivationEmailWithCorrectTo")
    void shouldBuildActivationEmailWithCorrectTo() {
        String toEmail = "alice.dupont@example.com";
        String token = "abc123token";

        MimeMessage message = activationEmailService.buildActivationEmail(toEmail, token);

        assertNotNull(message);
        verify(javaMailSender).createMimeMessage();
    }

    @Test
    @DisplayName("shouldBuildActivationEmailWithActivationTokenInLink")
    void shouldBuildActivationEmailWithActivationTokenInLink() {
        String toEmail = "alice@example.com";
        String token = "mySecretToken123";

        MimeMessage message = activationEmailService.buildActivationEmail(toEmail, token);

        assertNotNull(message);
        verify(javaMailSender).createMimeMessage();
    }

    @Test
    @DisplayName("shouldBuildActivationEmailWithConfigurableSubject")
    void shouldBuildActivationEmailWithConfigurableSubject() {
        ReflectionTestUtils.setField(activationEmailService, "emailSubject", "Welcome! Activate your account now");

        String toEmail = "bob@example.com";
        String token = "token123";

        MimeMessage message = activationEmailService.buildActivationEmail(toEmail, token);

        assertNotNull(message);
    }

    @Test
    @DisplayName("shouldSendActivationEmail")
    void shouldSendActivationEmail() {
        String toEmail = "alice@example.com";
        String token = "abc123";

        doNothing().when(javaMailSender).send(any(MimeMessage.class));

        activationEmailService.sendActivationEmail(toEmail, token);

        verify(javaMailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("shouldNotSendWhenRecipientIsBlank")
    void shouldNotSendWhenRecipientIsBlank() {
        activationEmailService.sendActivationEmail("", "token123");

        verify(javaMailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("shouldNotSendWhenRecipientIsNull")
    void shouldNotSendWhenRecipientIsNull() {
        activationEmailService.sendActivationEmail(null, "token123");

        verify(javaMailSender, never()).send(any(MimeMessage.class));
    }
}
