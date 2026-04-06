package nl.miwnn.ch19.vincent.LibraryDemo.service;

import nl.miwnn.ch19.vincent.LibraryDemo.dto.ChangePasswordDTO;
import nl.miwnn.ch19.vincent.LibraryDemo.dto.NewLibraryUserDTO;
import nl.miwnn.ch19.vincent.LibraryDemo.model.LibraryUser;
import nl.miwnn.ch19.vincent.LibraryDemo.repository.UserRepository;
import nl.miwnn.ch19.vincent.LibraryDemo.service.mapper.LibraryUserMapper;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author Vincent Velthuizen
 * Handle all businesslogic regarding users
 */
@Service
public class LibraryUserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LibraryUserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Gebruiker niet gevonden met username: " + username));
    }

    public void saveNewUser(NewLibraryUserDTO dto) {
        LibraryUser libraryUser = LibraryUserMapper.toLibraryUser(dto, passwordEncoder);
        userRepository.save(libraryUser);
    }


    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public String resetPassword(Long id) {
        LibraryUser user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Gebruiker niet gevonden met id: " + id));
        String newPassword = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return newPassword;
    }

    public @Nullable Object getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Changes the password for the given user after verifying the current password.
     * Returns false if the current password does not match.
     */
    public boolean changePassword(String username, ChangePasswordDTO dto) {
        LibraryUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Gebruiker niet gevonden: " + username));
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
        return true;
    }
}
