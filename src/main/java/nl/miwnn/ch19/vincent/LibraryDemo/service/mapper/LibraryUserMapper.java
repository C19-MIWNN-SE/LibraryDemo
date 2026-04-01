package nl.miwnn.ch19.vincent.LibraryDemo.service.mapper;

import nl.miwnn.ch19.vincent.LibraryDemo.dto.NewLibraryUserDTO;
import nl.miwnn.ch19.vincent.LibraryDemo.model.LibraryUser;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Vincent Velthuizen
 */
public class LibraryUserMapper {

    public static LibraryUser toLibraryUser(NewLibraryUserDTO dto, PasswordEncoder passwordEncoder) {
        LibraryUser user = new LibraryUser();

        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPlainPassword()));
        user.setAdministrator(dto.isAdministrator());

        return user;
    }

}
