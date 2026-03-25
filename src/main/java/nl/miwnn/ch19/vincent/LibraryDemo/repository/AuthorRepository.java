package nl.miwnn.ch19.vincent.LibraryDemo.repository;

import nl.miwnn.ch19.vincent.LibraryDemo.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Vincent Velthuizen
 */
public interface AuthorRepository extends JpaRepository<Author, Long> {
}
