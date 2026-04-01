package nl.miwnn.ch19.vincent.LibraryDemo.repository;

import nl.miwnn.ch19.vincent.LibraryDemo.model.LibraryUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author Vincent Velthuizen
 */
public interface UserRepository extends JpaRepository<LibraryUser, Long> {
    Optional<LibraryUser> findByUsername(String username);
}
