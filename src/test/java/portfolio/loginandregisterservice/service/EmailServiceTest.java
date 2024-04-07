package portfolio.loginandregisterservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import portfolio.loginandregisterservice.model.service.EmailService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static portfolio.loginandregisterservice.common.UserConstants.USER;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender emailSender;

    @InjectMocks
    private EmailService emailService;

    private final String TEST_EMAIL = USER.getEmail();
    private final String TEST_TOKEN = USER.getUniqueToken();
    private final String TEST_PASSWORD = USER.getPassword();

    @Test
    void sendWelcomeEmail_ShouldSendEmail() {
        // Act
        emailService.sendWelcomeEmail(USER.getEmail(), USER.getName());

        // Assert
        verify(emailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendActivationEmail_ShouldSendEmail() {
        // Act
        emailService.sendActivationEmail(USER.getEmail(), USER.getUniqueToken());

        // Assert
        verify(emailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendResetPasswordEmailAuth_ShouldSendEmail() {
        // Act
        emailService.sendResetPasswordEmailAuth(TEST_EMAIL, TEST_TOKEN);

        // Assert
        verify(emailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendResetPasswordConfirmation_ShouldSendEmail() {
        // Act
        emailService.sendResetPasswordConfirmation(TEST_EMAIL, TEST_PASSWORD);

        // Assert
        verify(emailSender).send(any(SimpleMailMessage.class));
    }
}