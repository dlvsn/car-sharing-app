package denys.mazurenko.easydrive.security;

import denys.mazurenko.easydrive.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface CustomUserDetailsService extends UserDetailsService {
    User getUserFromAuthentication(Authentication authentication);
}
