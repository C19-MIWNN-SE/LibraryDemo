package nl.miwnn.ch19.vincent.LibraryDemo.service.mapper;

import nl.miwnn.ch19.vincent.LibraryDemo.dto.NewLibraryUserDTO;
import nl.miwnn.ch19.vincent.LibraryDemo.model.LibraryUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Vincent Velthuizen
 */
class LibraryUserMapperTest {

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    @DisplayName("toLibraryUser should encode the password")
    void toLibraryUserShouldEncodeThePassword() {
        NewLibraryUserDTO dto = new NewLibraryUserDTO();
        dto.setUsername("testgebruiker");
        dto.setPlainPassword("geheim123");
        dto.setAdministrator(false);

        LibraryUser result = LibraryUserMapper.toLibraryUser(dto, encoder);

        assertAll(
                () -> assertEquals(dto.getUsername(), result.getUsername()),
                () -> assertEquals(dto.isAdministrator(), result.getAdministrator()),
                () -> assertNotEquals(dto.getPlainPassword(), result.getPassword()),
                () -> assertTrue(encoder.matches(dto.getPlainPassword(), result.getPassword()))
        );
    }

}