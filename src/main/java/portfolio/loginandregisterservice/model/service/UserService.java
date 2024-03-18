package portfolio.loginandregisterservice.model.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import portfolio.loginandregisterservice.model.entities.User;
import portfolio.loginandregisterservice.model.records.UserRequestRecord;
import portfolio.loginandregisterservice.model.records.UserResponseRecord;
import portfolio.loginandregisterservice.model.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public String create(UserRequestRecord data) {
        try {
            userRepository.save(new User(data.name(), data.email(), data.password()));
            return "User created successfully.";
        } catch (DataIntegrityViolationException e) {
            return "Email already exists.";
        } catch (TransactionSystemException e) {
            return "Failed to create user due to transaction issue.";
        } catch (Exception e) {
            return "An unexpected error occurred.";
        }
    }

    public Optional<UserResponseRecord> findById(Long id) {
        return userRepository.findById(id)
                .map(user -> new UserResponseRecord(user.getId(), user.getName(), user.getEmail(), user.getPassword()));
    }

    public Optional<UserResponseRecord> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(user -> new UserResponseRecord(user.getId(), user.getName(), user.getEmail(), user.getPassword()));
    }

    public List<UserResponseRecord> findByName(String name) {
        return userRepository.findAllByName(name)
                .stream()
                .map(user -> new UserResponseRecord(user.getId(), user.getName(), user.getEmail(), user.getPassword()))
                .collect(Collectors.toList());
    }

    public List<UserResponseRecord> findAll() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponseRecord(user.getId(), user.getName(), user.getEmail(), user.getPassword()))
                .collect(Collectors.toList());
    }

    public String update(UserRequestRecord data) {
        Optional<User> userOptional = userRepository.findById(data.id());
        return userOptional.map(user -> {
            if (!Objects.equals(user.getName(), data.name()) && !data.name().isBlank()) {
                user.setName(data.name());
            }

            if (!Objects.equals(user.getEmail(), data.email()) && !data.email().isBlank()) {
                user.setEmail(data.email());
            }

            if (!Objects.equals(user.getPassword(), data.password()) && !data.password().isBlank()) {
                user.setPassword(data.password());
            }
            userRepository.save(user);
            return "User updated successfully!";
        }).orElse("User not found for id " + data.id());
    }

    public void deleteById(UserRequestRecord data) {
        userRepository.deleteById(data.id());
    }
}
