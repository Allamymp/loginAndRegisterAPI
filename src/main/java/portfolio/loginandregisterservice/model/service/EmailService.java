package portfolio.loginandregisterservice.model.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender emailSender;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void registeredMail(String email, String name) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("New Account");
        message.setText("Welcome to my service, "+name
                +". Your registration was successfully completed.");
        emailSender.send(message);
    }
}
