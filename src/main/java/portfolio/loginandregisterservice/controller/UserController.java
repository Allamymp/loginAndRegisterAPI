package portfolio.loginandregisterservice.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import portfolio.loginandregisterservice.model.records.UserRequestRecord;
import portfolio.loginandregisterservice.model.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid UserRequestRecord data) {
        if (data == null || data.name() == null || data.name().isBlank()
                || data.email() == null || data.email().isBlank()
                || data.password() == null || data.password().isBlank()) {
            return ResponseEntity.badRequest().body("Name, email and password must be provided.");
        }
        return ResponseEntity.ok(userService.create(data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@Valid @PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body("A valid id must be provided.");
        }
        return ResponseEntity.ok().body(userService.findById(id));
    }

    @GetMapping("/{email}")
    public ResponseEntity<?> findByEmail(@Valid @PathVariable String email) {
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body("A valid email must be provided.");
        }
        return ResponseEntity.ok().body(userService.findByEmail(email));
    }

    @GetMapping("/{name}")
    public ResponseEntity<?> findByName(@Valid @PathVariable String name) {
        if (name == null || name.isBlank()) {
            return ResponseEntity.badRequest().body("A valid name must be provided.");
        }
        return ResponseEntity.ok().body(userService.findByName(name));
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok().body(userService.findAll());
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid UserRequestRecord data) {
        if (data == null || data.id() == null || data.id() <= 0) {
            return ResponseEntity.badRequest().body("Invalid id!");
        }
        return ResponseEntity.ok().body(userService.update(data));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@Valid UserRequestRecord data) {
        if (data.id() == null || data.id() <= 0) {
            return ResponseEntity.badRequest().body("Invalid id!");
        }
        userService.deleteById(data);
        return ResponseEntity.noContent().build();
    }
}
