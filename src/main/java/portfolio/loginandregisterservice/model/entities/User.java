package portfolio.loginandregisterservice.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TB_USERS")
@NoArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = false, unique = true)
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    private boolean enabled;
    @NotBlank
    private String uniqueToken;


    public User(String name, String email, String password, String uniqueToken) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.uniqueToken = uniqueToken;
        this.enabled = false;

    }
}

