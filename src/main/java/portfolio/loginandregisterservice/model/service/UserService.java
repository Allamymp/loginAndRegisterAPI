package portfolio.loginandregisterservice.model.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import portfolio.loginandregisterservice.model.entities.User;
import portfolio.loginandregisterservice.model.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }


    public User create(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new DataIntegrityViolationException("Email already exists.");
        }
        if (!validatePassword(user.getPassword())) {
            throw new IllegalArgumentException("A valid password must provide at least one uppercase"
                    + " letter, one lowercase letter, one special character, one number "
                    + "and be between 8 and 20 characters in length.");
        }
        String activationToken = UUID.randomUUID().toString();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setUniqueToken(activationToken);
        return userRepository.save(user);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);

    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User update(User data) {
        Optional<User> userOptional = userRepository.findById(data.getId());
        return userOptional.map(user -> {
            if (!Objects.equals(user.getName(), data.getName()) && !data.getName().isBlank()) {
                user.setName(data.getName());
            }

            if (!Objects.equals(user.getEmail(), data.getEmail()) && !data.getEmail().isBlank()) {
                user.setEmail(data.getEmail());
            }

            if (!data.getPassword().isBlank()
                    && !passwordEncoder.matches(data.getPassword(), user.getPassword())
                    && validatePassword(data.getPassword())) {
                user.setPassword(passwordEncoder.encode(data.getPassword()));
            }

            return userRepository.save(user);
        }).orElseThrow(EntityNotFoundException::new);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public void forgetPassword(String email) {
        userRepository.findByEmail(email)
                .ifPresentOrElse(
                        user -> emailService.sendResetPasswordEmailAuth(user.getEmail(), user.getUniqueToken()),
                        () -> {
                            throw new IllegalArgumentException("User not found for email: " + email);
                        }
                );
    }

    public String[] resetPassword(String token) {
        Optional<User> userOptional = userRepository.findByUniqueToken(token);
        if (userOptional.isPresent()) {
            String newPassword = generateRandomPassword(8);
            User user = userOptional.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setUniqueToken(UUID.randomUUID().toString());
            userRepository.save(user);
            return new String[]{user.getEmail(), newPassword};
        }
        return null;
    }


    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder newPassword = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomIndex = (int) (Math.random() * chars.length());
            newPassword.append(chars.charAt(randomIndex));
        }
        return newPassword.toString();
    }

    private boolean validatePassword(String password) {
        if (password.length() < 8 || password.length() > 20) {
            return false;
        }
        // Verifica se a senha contém pelo menos uma letra maiúscula, uma minúscula e um
        // caractere especial
        Pattern upperCase = Pattern.compile("[A-Z]");
        Pattern lowerCase = Pattern.compile("[a-z]");
        Pattern specialChar = Pattern.compile("[!@#$%^&*()-_+=<>?/{}\\[\\]]");
        Pattern numChar = Pattern.compile("[0-9]");

        Matcher upperCaseMatcher = upperCase.matcher(password);
        Matcher lowerCaseMatcher = lowerCase.matcher(password);
        Matcher specialCharMatcher = specialChar.matcher(password);
        Matcher numCharMatcher = numChar.matcher(password);

        return upperCaseMatcher.find() && lowerCaseMatcher.find()
                && specialCharMatcher.find() && numCharMatcher.find();
    }
}
