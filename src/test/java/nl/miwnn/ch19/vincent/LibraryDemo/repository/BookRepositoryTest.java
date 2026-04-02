package nl.miwnn.ch19.vincent.LibraryDemo.repository;

import nl.miwnn.ch19.vincent.LibraryDemo.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Vincent Velthuizen
 */

@DataJpaTest
@ActiveProfiles("test")
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        Book book1 = new Book("De Aanslag", 1982);
        Book book2 = new Book("Max Havelaar", 1860);
        Book book3 = new Book("De ontdekking van de hemel", 1992);
        entityManager.persist(book1);
        entityManager.persist(book2);
        entityManager.persist(book3);
        entityManager.flush();
    }

    @Test
    @DisplayName("findBookByTitle should return a book when exists")
    void findBookByTitleShouldReturnABookWhenExists() {
        assertTrue(bookRepository.findBookByTitle("De Aanslag").isPresent());
    }

    @Test
    @DisplayName("findBookByTitle should return empty when not exists")
    void findBookByTitleShouldReturnEmptyWhenNotExists() {
        assertTrue(bookRepository.findBookByTitle("Bestaat niet").isEmpty());
    }

    @Test
    @DisplayName("findBooksByTitleContainingIgnoreCase shoud be case insensitive")
    void findBooksByTitleContainingIgnoreCaseShoudBeCaseInsensitive() {
        List<Book> results = bookRepository.findBooksByTitleContainingIgnoreCase("de");
        assertEquals(2, results.size());
    }

}