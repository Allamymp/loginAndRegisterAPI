package portfolio.loginandregisterservice.infra.security;


import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import portfolio.loginandregisterservice.model.entities.User;
import portfolio.loginandregisterservice.model.repository.UserRepository;

import java.util.UUID;

@Service
public class AuthenticationService {

    private final JwtService jwtService;
    private final UserRepository userRepository;


    public AuthenticationService(JwtService jwtService,
                                 UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    public String authenticate(Authentication authentication) {
        return jwtService.generateToken(authentication);
    }

    public void activateAccount(User user) {
        user.setEnabled(true);
        String newToken = UUID.randomUUID().toString();
        user.setUniqueToken(newToken);
        userRepository.save(user);
    }

}
