package portfolio.loginandregisterservice.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import portfolio.loginandregisterservice.model.entities.User;
import portfolio.loginandregisterservice.model.repository.UserRepository;
import portfolio.loginandregisterservice.model.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;
import static portfolio.loginandregisterservice.common.UserConstants.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;
    @AfterEach
    public void afterEach() {
        USER.setId(null);
    }

    // padrao de nomenclatura = operacao_estado_retornoEsperado
    @Test
    void createUser_WithValidData_ReturnsUser() {
        when(userRepository.save(USER)).thenReturn(USER);
        //sut = system under test
        User sut = userService.create(USER);
        assertThat(sut).isEqualTo(USER);
    }

    @Test
    void createUser_WithNoValidData_ThrowsException() {
        assertThatThrownBy(() -> userService.create(INVALID_USER))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void createUser_withExistentEmail_ThrowsException() {
        when(userRepository.findByEmail(USER.getEmail())).thenReturn(Optional.of(USER));

        assertThatThrownBy(
                () -> userService.create(USER))
                .isInstanceOf(DataIntegrityViolationException.class);

    }

    @Test
    void findById_withExistingId_returnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(USER));
        Optional<User> sut = userService.findById(1L);
        assertThat(sut.isPresent()).isTrue();
        assertThat(sut.get()).isEqualTo(USER);
    }

    @Test
    void findById_withNonexistentId_returnsNull() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<User> sut = userService.findById(1L);
        assertThat(sut.isPresent()).isFalse();
    }

    @Test
    void findByEmail_withValidEmail_returnsUser() {

        when(userRepository.findByEmail("email")).thenReturn(Optional.of(USER));

        Optional<User> sut = userService.findByEmail("email");

        assertThat(sut.isPresent()).isTrue();
    }

    @Test
    void findByEmail_withNonExistentEmail_returnsNull() {

        when(userRepository.findByEmail("nonExistentEmail")).thenReturn(Optional.empty());

        Optional<User> sut = userService.findByEmail("nonExistentEmail");

        assertThat(sut.isEmpty()).isTrue();
    }

    @Test
    void findAll_withNoParameters_returnsListOfUsers() {

        when(userRepository.findAll()).thenReturn(USER_LIST);

        List<User> sut = userService.findAll();

        assertThat(sut.isEmpty()).isFalse();
        assertThat(sut.getFirst().getName()).isEqualTo("user1");
        assertThat(sut.getLast().getName()).isEqualTo("user3");
    }

    @Test
    void updateUser_WithBlankName_DoesNotUpdateName() {
        // Arrange
        User updatedUser = new User("", "updated@example.com", "new@Password1", "token");
        updatedUser.setId(1L);
        USER.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(USER));
        when(userRepository.save(USER)).thenReturn(USER);

        // Act
        User result = userService.update(updatedUser);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("user_name"); // Name remains unchanged
    }


    @Test
    void deleteById_withExistingId_DoesNotThrowAnyException() {
        assertThatCode(() -> userService.deleteById(1L)).doesNotThrowAnyException();
    }

    @Test
    void forgetPassword_withExistingEmail_returnStringArray() {
        when(userRepository.findByEmail(USER.getEmail())).thenReturn(Optional.of(USER));

        String[] sud = userService.forgetPassword(USER.getEmail());

        assertThat(sud[0]).isNotBlank();
        assertThat(sud[0]).isEqualTo(USER.getEmail());
        assertThat(sud[1]).isNotBlank();
        assertThat(sud[1]).isEqualTo(USER.getUniqueToken());
    }

    @Test
    void resetPassword_withExistingEmail_returnsStringArray() {
        when(userRepository.findByUniqueToken(USER.getUniqueToken())).thenReturn(Optional.of(USER));


        String[] sud = userService.resetPassword(USER.getUniqueToken());

        assertThat(sud[0]).isNotBlank();
        assertThat(sud[1]).isNotBlank();
        assertThat(sud[0]).isEqualTo(USER.getEmail());
        assertThat(sud[1]).isNotEqualTo(USER.getPassword());
    }
}