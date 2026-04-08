package nl.miwnn.ch19.vincent.LibraryDemo.repository;

import nl.miwnn.ch19.vincent.LibraryDemo.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author Vincent Velthuizen
 */
public interface GenreRepository extends JpaRepository<Genre, Long> {
    Optional<Genre> findByShortNameIgnoreCase(String shortName);
}
