package nl.miwnn.ch19.vincent.LibraryDemo.service;

import nl.miwnn.ch19.vincent.LibraryDemo.dto.ChangePasswordDTO;
import nl.miwnn.ch19.vincent.LibraryDemo.model.LibraryUser;
import nl.miwnn.ch19.vincent.LibraryDemo.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author Vincent Velthuizen
 */
@ExtendWith(MockitoExtension.class)
class LibraryUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LibraryUserService libraryUserService;

    private LibraryUser testUser() {
        return new LibraryUser("testgebruiker", "$2a$10$encodedpassword", false);
    }

    // --- deleteById ---

    @Test
    @DisplayName("deleteById should call repository deleteById with the given id")
    void deleteByIdShouldCallRepositoryDeleteById() {
        libraryUserService.deleteById(42L);

        verify(userRepository, times(1)).deleteById(42L);
    }

    // --- resetPassword ---

    @Test
    @DisplayName("resetPassword should throw when user is not found")
    void resetPasswordShouldThrowWhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> libraryUserService.resetPassword(99L));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("resetPassword should return a non-empty plain text password")
    void resetPasswordShouldReturnNonEmptyPassword() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser()));
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$newhash");

        String result = libraryUserService.resetPassword(1L);

        assertNotNull(result);
        assertFalse(result.isBlank());
    }

    @Test
    @DisplayName("resetPassword should return a password of 12 characters")
    void resetPasswordShouldReturn12CharacterPassword() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser()));
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$newhash");

        String result = libraryUserService.resetPassword(1L);

        assertEquals(12, result.length());
    }

    @Test
    @DisplayName("resetPassword should save the user with the new encoded password")
    void resetPasswordShouldSaveUserWithNewEncodedPassword() {
        LibraryUser user = testUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$newhash");

        libraryUserService.resetPassword(1L);

        ArgumentCaptor<LibraryUser> captor = ArgumentCaptor.forClass(LibraryUser.class);
        verify(userRepository, times(1)).save(captor.capture());
        assertEquals("$2a$10$newhash", captor.getValue().getPassword());
    }

    // --- changePassword ---

    @Test
    @DisplayName("changePassword should return false when the current password is wrong")
    void changePasswordShouldReturnFalseWhenCurrentPasswordIsWrong() {
        when(userRepository.findByUsername("testgebruiker")).thenReturn(Optional.of(testUser()));
        when(passwordEncoder.matches("wrongpassword", "$2a$10$encodedpassword")).thenReturn(false);

        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setCurrentPassword("wrongpassword");
        dto.setNewPassword("nieuwwachtwoord");
        dto.setCheckPassword("nieuwwachtwoord");

        boolean result = libraryUserService.changePassword("testgebruiker", dto);

        assertFalse(result);
    }

    @Test
    @DisplayName("changePassword should not save when the current password is wrong")
    void changePasswordShouldNotSaveWhenCurrentPasswordIsWrong() {
        when(userRepository.findByUsername("testgebruiker")).thenReturn(Optional.of(testUser()));
        when(passwordEncoder.matches("wrongpassword", "$2a$10$encodedpassword")).thenReturn(false);

        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setCurrentPassword("wrongpassword");
        dto.setNewPassword("nieuwwachtwoord");
        dto.setCheckPassword("nieuwwachtwoord");

        libraryUserService.changePassword("testgebruiker", dto);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("changePassword should return true when the current password is correct")
    void changePasswordShouldReturnTrueWhenCurrentPasswordIsCorrect() {
        when(userRepository.findByUsername("testgebruiker")).thenReturn(Optional.of(testUser()));
        when(passwordEncoder.matches("juistwachtwoord", "$2a$10$encodedpassword")).thenReturn(true);
        when(passwordEncoder.encode("nieuwwachtwoord")).thenReturn("$2a$10$newhash");

        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setCurrentPassword("juistwachtwoord");
        dto.setNewPassword("nieuwwachtwoord");
        dto.setCheckPassword("nieuwwachtwoord");

        boolean result = libraryUserService.changePassword("testgebruiker", dto);

        assertTrue(result);
    }

    @Test
    @DisplayName("changePassword should save the user with the new encoded password when current password is correct")
    void changePasswordShouldSaveNewPasswordWhenCurrentPasswordIsCorrect() {
        when(userRepository.findByUsername("testgebruiker")).thenReturn(Optional.of(testUser()));
        when(passwordEncoder.matches("juistwachtwoord", "$2a$10$encodedpassword")).thenReturn(true);
        when(passwordEncoder.encode("nieuwwachtwoord")).thenReturn("$2a$10$newhash");

        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setCurrentPassword("juistwachtwoord");
        dto.setNewPassword("nieuwwachtwoord");
        dto.setCheckPassword("nieuwwachtwoord");

        libraryUserService.changePassword("testgebruiker", dto);

        ArgumentCaptor<LibraryUser> captor = ArgumentCaptor.forClass(LibraryUser.class);
        verify(userRepository, times(1)).save(captor.capture());
        assertEquals("$2a$10$newhash", captor.getValue().getPassword());
    }

    @Test
    @DisplayName("changePassword should throw when the user is not found")
    void changePasswordShouldThrowWhenUserNotFound() {
        when(userRepository.findByUsername("onbekend")).thenReturn(Optional.empty());

        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setCurrentPassword("wachtwoord");
        dto.setNewPassword("nieuw");
        dto.setCheckPassword("nieuw");

        assertThrows(UsernameNotFoundException.class,
                () -> libraryUserService.changePassword("onbekend", dto));
    }
}