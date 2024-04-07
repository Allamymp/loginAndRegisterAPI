package portfolio.loginandregisterservice.model.records;

import jakarta.validation.constraints.NotBlank;

public record UserCreateRecord(
        @NotBlank String name,
        @NotBlank String email,
        @NotBlank String password) {
}
