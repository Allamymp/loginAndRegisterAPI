package portfolio.loginandregisterservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
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
    public void registerUser_WithValidData_ReturnsCreated() throws Exception {
        when(userService.create(any(User.class))).thenReturn(USER);

        // Realize a chamada POST para /register com o objeto user convertido para JSON
        mockMvc.perform(post("/register")
                        .content(objectMapper.writeValueAsString(USER))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void registerUser_withInvalidData_returnsBadRequest() throws Exception {
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
    public void registerUser_withExistingEmail_returnsConflict() throws Exception {
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
    public void getUser_byUnexistingId_returnsNotFound() throws Exception {
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
    public void findByEmail_EmptyEmail_returnsBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/findByEmail")
                        .param("email", "")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void findByEmail_NullEmail_returnsInternalServerError() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/findByEmail")
                        .param("email", (String) null)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void findByEmail_EmailNotFound_returnsNotFound() throws Exception {
        // Arrange
        when(userService.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/findByEmail")
                        .param("email", "user@email.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateUser_withValidData_returnsUser() throws Exception {
        User updatedUser = USER;
        updatedUser.setName("updatedName");
        updatedUser.setId(1L);

        Mockito.when(userService.update(any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(MockMvcRequestBuilders.put("/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"updatedName\",\"email\":\""
                                + USER.getEmail() + "\",\"password\":\"" + USER.getPassword() + "\",\"uniqueToken\":\""
                                + USER.getUniqueToken() + "\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(USER.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(USER.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.encryptedPassword").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.encryptedUniqueToken").isNotEmpty());
    }

    @Test
    public void updateUser_withInvalidData_returnsServerError() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"" + USER.getName() + "\",\"email\":\""
                                + USER.getEmail() + "\",\"password\":\"newpassword\",\"uniqueToken\":\""
                                + USER.getUniqueToken() + "\"}"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    public void resetPassword_withValidToken_returnsIsOk() throws Exception {
        String token = USER.getUniqueToken();
        String email = USER.getEmail();
        String newPassword = "newPassword";
        String[] info = {email, newPassword};

        Mockito.when(userService.resetPassword(anyString())).thenReturn(info);

        mockMvc.perform(MockMvcRequestBuilders.get("/reset/{token}", token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("New password send to email!"));

        // Verificar se o serviço de e-mail foi chamado com as informações corretas
        Mockito.verify(emailService).sendResetPasswordConfirmation(email, newPassword);
    }

    @Test
    public void resetPassword_withInvalidToken_returnsBadRequest() throws Exception {
        String token = "invalidToken";

        Mockito.when(userService.resetPassword(anyString())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/reset/{token}", token))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers
                        .content()
                        .string("Invalid or expired token. Please request a new password reset link."));
    }

    @Test
    public void forgetPassword_withValidEmail_returnsIsOk() throws Exception {
        Mockito.doNothing().when(userService).forgetPassword(USER.getEmail());

        mockMvc.perform(MockMvcRequestBuilders.get("/forgetPassword")
                        .param("email", USER.getEmail())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers
                        .content().string("Password instructions sent to your email."));
    }

    @Test
    public void fogetPassword_withInvalidEmail_returnsBadRequest() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("User not found for email: " + INVALID_USER.getEmail()))
                .when(userService).forgetPassword(INVALID_USER.getEmail());

        mockMvc.perform(MockMvcRequestBuilders.get("/forgetPassword")
                        .param("email", INVALID_USER.getEmail())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .string("Invalid request. User not found for email: "
                                + INVALID_USER.getEmail()));
    }
    @Test
    public void deleteUser_byExistingid_returnsNoContent() throws Exception {
        long userId = 1L;
        String requestBody = "{\"id\": " + userId + "}";

        mockMvc.perform(MockMvcRequestBuilders.delete("/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.content().string(""));


        Mockito.verify(userService).deleteById(userId);


        Mockito.verify(userService).deleteById(Mockito.eq(userId));
    }
    @Test
    public void deleteUser_byUnexistingId_returnsBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": null}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Invalid id!"));
    }

}
