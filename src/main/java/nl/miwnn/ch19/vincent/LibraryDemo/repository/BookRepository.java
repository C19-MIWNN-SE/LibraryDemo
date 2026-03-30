package nl.miwnn.ch19.vincent.LibraryDemo.repository;

import nl.miwnn.ch19.vincent.LibraryDemo.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * @author Vincent Velthuizen
 */
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findBookByTitle(String title);
    List<Book> findBooksByTitleContainingIgnoreCase(String titleFragment);

    @Query("SELECT b FROM Book b JOIN b.authors a " +
            "WHERE LOWER(a.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Book> findByAuthorLastNameContaining(@Param("name") String name);

    boolean existsByTitle(String title);
}
