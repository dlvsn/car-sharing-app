package denys.mazurenko.carsharingapp.security;

import denys.mazurenko.carsharingapp.exception.EntityNotFoundException;
import denys.mazurenko.carsharingapp.exception.ErrorMessages;
import denys.mazurenko.carsharingapp.model.User;
import denys.mazurenko.carsharingapp.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        return findByEmail(email);
    }

    public User getUserFromAuthentication(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return findByEmail(user.getEmail());
    }

    private User findByEmail(String email) {
        return userRepository.findUserByEmail(email).orElseThrow(() ->
                new EntityNotFoundException(
                        String.format(
                                ErrorMessages.getCANT_FIND_USER_BY_EMAIL(),
                                email)
                )
        );
    }
}
