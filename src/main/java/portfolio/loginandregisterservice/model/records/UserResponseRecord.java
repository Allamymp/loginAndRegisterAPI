package portfolio.loginandregisterservice.model.records;

public record UserResponseRecord(Long id, String name, String email, String encryptedPassword, String encryptedUniqueToken) {
}
