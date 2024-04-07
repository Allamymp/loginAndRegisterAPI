package portfolio.loginandregisterservice.repository;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import portfolio.loginandregisterservice.model.entities.User;
import portfolio.loginandregisterservice.model.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static portfolio.loginandregisterservice.common.UserConstants.*;


@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    TestEntityManager testEntityManager;

    @AfterEach
    public void afterEach() {
        USER.setId(null);
    }

    @Test
    public void createUser_withValidData_returnsUser() {
        //Arrange
        User user = userRepository.save(USER);
        //Act
        User sut = testEntityManager.find(User.class, user.getId());
        //Assert
        assertThat(sut).isNotNull();
        assertThat(user.getName()).isEqualTo(sut.getName());
        assertThat(user.getEmail()).isEqualTo(sut.getEmail());
        assertThat(user.getPassword()).isEqualTo(sut.getPassword());
    }

    @Test
    public void createUser_withInvalidData_throwsException() {
        assertThatThrownBy(() -> userRepository.save(EMPTY_USER)).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> userRepository.save(INVALID_USER)).isInstanceOf(RuntimeException.class);

    }

    @Test
    public void createUser_withExistingEmail_throwsException() {

        User duplicatedUser = testEntityManager.persistFlushFind(USER);
        testEntityManager.detach(duplicatedUser);
        duplicatedUser.setId(null);

        assertThatThrownBy(() -> userRepository.save(duplicatedUser)).isInstanceOf(RuntimeException.class);


    }

    @Test
    public void getUser_byExistingId_returnsUser() {

        User user = testEntityManager.persistFlushFind(USER);
        Optional<User> sud = userRepository.findById(user.getId());

        assertThat(sud).isNotNull();
        assertThat(sud).isNotEmpty();
        assertThat(user.getId()).isEqualTo(sud.get().getId());
    }
    @Test
    public void getUser_byUnexistingId_returnsUser(){

        Optional<User> sud = userRepository.findById(1L);
        assertThat(sud).isEmpty();
    }

    @Test
    public void getUser_ByExistingEmail_returnsUser() throws Exception {
        User user = testEntityManager.persistFlushFind(USER);
        Optional<User> sud = userRepository.findByEmail(user.getEmail());
        assertThat(sud).isNotEmpty();
    }

    @Test
    public void getUser_ByUnexistingEmail_returnsEmpty() throws Exception {
        Optional<User> sud = userRepository.findByEmail(USER.getName());
        assertThat(sud).isEmpty();
    }

    @Test
    public void getUser_byExistingUniqueToken_returnsUser(){
        User user = testEntityManager.persistFlushFind(USER);
        Optional<User> sud = userRepository.findByUniqueToken(user.getUniqueToken());
        assertThat(sud).isNotEmpty();
    }
    @Test
    public void getUser_byUnexistingUniqueToken_returnsEmpty(){
        Optional<User> sud = userRepository.findByUniqueToken(USER.getUniqueToken());
        assertThat(sud).isEmpty();
    }

}