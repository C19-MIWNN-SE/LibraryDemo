package nl.miwnn.ch19.vincent.LibraryDemo.repository;

import nl.miwnn.ch19.vincent.LibraryDemo.model.Copy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Vincent Velthuizen
 */
public interface CopyRepository extends JpaRepository<Copy, Long> {
    List<Copy> findByBookTitle(String title);
}
