package nl.miwnn.ch19.vincent.LibraryDemo.service;

import nl.miwnn.ch19.vincent.LibraryDemo.repository.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author Vincent Velthuizen
 * Handle all businesslogic regarding users
 */
@Service
public class LibraryUserService implements UserDetailsService {
    private final UserRepository userRepository;

    public LibraryUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Gebruiker niet gevonden met username: " + username));
    }
}
