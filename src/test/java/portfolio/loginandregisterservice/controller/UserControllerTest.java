package portfolio.loginandregisterservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import portfolio.loginandregisterservice.model.entities.User;
import portfolio.loginandregisterservice.model.service.EmailService;
import portfolio.loginandregisterservice.model.service.UserService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static portfolio.loginandregisterservice.common.UserConstants.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;
    //emailService is necessary in UserService
    @MockBean
    private EmailService emailService;

    @AfterEach
    public void afterEach() {
        USER.setId(null);
    }

    @Test
    public void createUser_WithValidData_ReturnsCreated() throws Exception {
        when(userService.create(any(User.class))).thenReturn(USER);

        // Realize a chamada POST para /register com o objeto user convertido para JSON
        mockMvc.perform(post("/register")
                        .content(objectMapper.writeValueAsString(USER))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void createUser_withInvalidData_returnsBadRequest() throws Exception {
        mockMvc
                .perform(
                        post("/register")
                                .content(objectMapper.writeValueAsString(INVALID_USER))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc
                .perform(
                        post("/register").content(objectMapper.writeValueAsString(EMPTY_USER))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void createUser_withExistingEmail_returnsConflict() throws Exception {
        when(userService.create(any())).thenThrow(DataIntegrityViolationException.class);

        mockMvc
                .perform(
                        post("/register").content(objectMapper.writeValueAsString(USER))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    public void getUser_byExistingId_returnsUser() throws Exception {
        when(userService.findById(1L)).thenReturn(Optional.of(USER));

        mockMvc.perform(get("/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER.getId()))
                .andExpect(jsonPath("$.name").value(USER.getName()))
                .andExpect(jsonPath("$.email").value(USER.getEmail()))
                .andExpect(jsonPath("$.encryptedPassword").isNotEmpty())
                .andExpect(jsonPath("$.encryptedUniqueToken").isNotEmpty());


    }

    @Test
    public void getUser_byUnexistingId_ReturnsNotFound() throws Exception {
        mockMvc
                .perform(
                        get("/1"))
                .andExpect(status().isNotFound());

    }
    @Test
    public void getUser_byExistingEmail_returnsUser() throws Exception {
        when(userService.findByEmail(USER.getEmail())).thenReturn(Optional.of(USER));

        mockMvc.perform(get("/findByEmail")
                        .param("email", USER.getEmail())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER.getId()))
                .andExpect(jsonPath("$.name").value(USER.getName()))
                .andExpect(jsonPath("$.email").value(USER.getEmail()))
                .andExpect(jsonPath("$.encryptedPassword").isNotEmpty())
                .andExpect(jsonPath("$.encryptedUniqueToken").isNotEmpty());
    }
    @Test
    public void findByEmail_EmptyEmail_ReturnsBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/findByEmail")
                        .param("email", "")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void findByEmail_NullEmail_ReturnsInternalServerError() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/findByEmail")
                        .param("email", (String) null)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void findByEmail_EmailNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        when(userService.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/findByEmail")
                        .param("email", "user@email.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
