package nl.miwnn.ch19.vincent.LibraryDemo.repository;

import nl.miwnn.ch19.vincent.LibraryDemo.model.Copy;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Vincent Velthuizen
 */
public interface CopyRepository extends JpaRepository<Copy, Long> {
}
