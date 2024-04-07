package portfolio.loginandregisterservice.model.records;

public record UserRequestRecord(Long id, String name, String email, String password, String uniqueToken) {
}
