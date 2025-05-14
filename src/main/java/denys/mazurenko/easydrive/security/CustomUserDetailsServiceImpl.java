package denys.mazurenko.easydrive.security;

import denys.mazurenko.easydrive.exception.EntityNotFoundException;
import denys.mazurenko.easydrive.model.User;
import denys.mazurenko.easydrive.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        return findByEmail(email);
    }

    @Override
    public User getUserFromAuthentication(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return findByEmail(user.getEmail());
    }

    private User findByEmail(String email) {
        return userRepository.findUserByEmail(email).orElseThrow(() ->
                new EntityNotFoundException(
                        "Can't find user by email " + email
                )
        );
    }
}
