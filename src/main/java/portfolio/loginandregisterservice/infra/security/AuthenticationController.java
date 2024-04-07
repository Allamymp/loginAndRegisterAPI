package portfolio.loginandregisterservice.infra.security;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import portfolio.loginandregisterservice.model.entities.User;
import portfolio.loginandregisterservice.model.repository.UserRepository;
import portfolio.loginandregisterservice.model.service.EmailService;

import java.util.Optional;

@RestController
public class AuthenticationController {
    private final EmailService emailService;
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    public AuthenticationController(EmailService emailService, AuthenticationService authenticationService,
                                    UserRepository userRepository) {
        this.emailService = emailService;
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
    }

    @PostMapping("/authenticate")
    public String authenticate(Authentication authentication) {
        try {
            return authenticationService.authenticate(authentication);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }

    @GetMapping("/activate/{token}")
    public ResponseEntity<String> activateAccount(@Valid @PathVariable String token) {
        Optional<User> userOptional = userRepository.findByUniqueToken(token);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            authenticationService.activateAccount(user);
            emailService.sendWelcomeEmail(user.getEmail(), user.getName());
            return ResponseEntity.ok("Account activated successfully.");
        }
        return ResponseEntity.badRequest().body("Invalid activation token.");
    }

}