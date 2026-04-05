package nl.miwnn.ch19.vincent.LibraryDemo.repository;

import nl.miwnn.ch19.vincent.LibraryDemo.model.Book;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Copy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
class CopyRepositoryTest {

    @Autowired
    private CopyRepository copyRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        Book hobbit = new Book("The Hobbit", 1937);
        entityManager.persist(hobbit);
        entityManager.persist(new Copy(hobbit));
        entityManager.persist(new Copy(hobbit));

        Book lotr = new Book("The Lord of the Rings", 1954);
        entityManager.persist(lotr);
        entityManager.persist(new Copy(lotr));

        entityManager.flush();
    }

    @Test
    @DisplayName("findByBookTitle should return all copies for a matching title")
    void findByBookTitleShouldReturnCopiesForMatchingTitle() {
        List<Copy> copies = copyRepository.findByBookTitle("The Hobbit");

        assertEquals(2, copies.size());
    }

    @Test
    @DisplayName("findByBookTitle should return empty list for nonexistent title")
    void findByBookTitleShouldReturnEmptyListForNonexistentTitle() {
        List<Copy> copies = copyRepository.findByBookTitle("Bestaat niet");

        assertTrue(copies.isEmpty());
    }

    @Test
    @DisplayName("findByBookTitle should not return copies belonging to a different book")
    void findByBookTitleShouldNotReturnCopiesForDifferentBook() {
        List<Copy> copies = copyRepository.findByBookTitle("The Hobbit");

        assertTrue(copies.stream().allMatch(copy -> copy.getBook().getTitle().equals("The Hobbit")));
    }
}