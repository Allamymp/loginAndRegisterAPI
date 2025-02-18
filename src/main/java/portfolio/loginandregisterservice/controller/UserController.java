package portfolio.loginandregisterservice.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import portfolio.loginandregisterservice.model.entities.User;
import portfolio.loginandregisterservice.model.records.UserCreateRecord;
import portfolio.loginandregisterservice.model.records.UserRequestRecord;
import portfolio.loginandregisterservice.model.records.UserResponseRecord;
import portfolio.loginandregisterservice.model.service.EmailService;
import portfolio.loginandregisterservice.model.service.UserService;

import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class UserController {

    private final UserService userService;
    private final EmailService emailService;

    public UserController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> create(@Valid @RequestBody UserCreateRecord data) {
        User user = new User(data.name(), data.email(), data.password(), "");
        user = userService.create(user);
        emailService.sendActivationEmail(user.getEmail(), user.getUniqueToken());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("A valid id must be provided.");
        }

        Optional<User> userOptional = userService.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            UserResponseRecord userResponseRecord = new UserResponseRecord(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getPassword(),
                    user.getUniqueToken()
            );
            return ResponseEntity.ok().body(userResponseRecord);
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("User not found for id: " + id);
        }
    }


    @GetMapping("/findByEmail")
    public ResponseEntity<?> findByEmail(@RequestParam String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("A valid email must be provided.");
        }

        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            UserResponseRecord userResponseRecord = new UserResponseRecord(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getPassword(),
                    user.getUniqueToken()
            );
            return ResponseEntity.ok().body(userResponseRecord);
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("User not found for email: " + email);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok().body(userService.findAll()
                .stream()
                .map(user -> new UserResponseRecord(user.getId()
                        , user.getName()
                        , user.getEmail()
                        , user.getPassword()
                        , user.getUniqueToken()))
                .collect(Collectors.toList()));
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody UserRequestRecord data) {
        if (data == null || data.id() == null || data.id() <= 0) {
            throw new IllegalArgumentException("Invalid id!");
        }
        User user = userService.update(new User(data.name(), data.email(), data.password(), data.uniqueToken()));

        return ResponseEntity.ok().body(new UserResponseRecord(user.getId()
                , user.getName()
                , user.getEmail()
                , user.getPassword()
                , user.getUniqueToken()));
    }

    @GetMapping("/reset/{token}")
    public ResponseEntity<?> resetPassword(@PathVariable String token) {
        String[] info = userService.resetPassword(token);
        if (info != null) {
            emailService.sendResetPasswordConfirmation(info[0], info[1]);
            return ResponseEntity.ok().body("New password send to email!");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired token. Please request a new password reset link.");

        }
    }

    @GetMapping("/forgetPassword")
    public ResponseEntity<?> forgetPassword(@RequestParam String email) {
        userService.forgetPassword(email);
        return ResponseEntity.ok().body("Password instructions sent to your email.");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody UserRequestRecord data) {
        if (data.id() == null || data.id() <= 0) {
            return ResponseEntity.badRequest().body("Invalid id!");
        }
        userService.deleteById(data.id());
        return ResponseEntity.noContent().build();
    }


}
