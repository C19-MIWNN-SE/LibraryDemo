package nl.miwnn.ch19.vincent.LibraryDemo.repository;

import nl.miwnn.ch19.vincent.LibraryDemo.model.Author;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Vincent Velthuizen
 */
@DataJpaTest
@ActiveProfiles("test")
class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("saving a book with authors should persist the ManyToMany relationship")
    void savingBookWithAuthorsShouldPersistRelationship() {
        Author tolkien = new Author();
        tolkien.setFirstName("J.R.R.");
        tolkien.setLastName("Tolkien");
        entityManager.persist(tolkien);

        Book lotr = new Book("The Lord of the Rings", 1954);
        lotr.getAuthors().add(tolkien);
        entityManager.persist(lotr);
        entityManager.flush();
        entityManager.clear();

        Book found = bookRepository.findBookByTitle("The Lord of the Rings").orElseThrow();
        assertEquals(1, found.getAuthors().size());
        assertEquals("Tolkien", found.getAuthors().get(0).getLastName());
    }

    @Test
    @DisplayName("removing an author from a book should not delete the author")
    void removingAuthorFromBookShouldNotDeleteAuthor() {
        Author tolkien = new Author();
        tolkien.setFirstName("J.R.R.");
        tolkien.setLastName("Tolkien");
        entityManager.persist(tolkien);
        Long authorId = tolkien.getId();

        Book lotr = new Book("The Lord of the Rings", 1954);
        lotr.getAuthors().add(tolkien);
        entityManager.persist(lotr);
        entityManager.flush();

        lotr.getAuthors().clear();
        bookRepository.save(lotr);
        entityManager.flush();
        entityManager.clear();

        assertTrue(authorRepository.findById(authorId).isPresent());
    }

    @Test
    @DisplayName("findByLastNameAndFirstName should return the matching author")
    void findByLastNameAndFirstNameShouldReturnMatchingAuthor() {
        Author tolkien = new Author();
        tolkien.setFirstName("J.R.R.");
        tolkien.setLastName("Tolkien");
        entityManager.persist(tolkien);
        entityManager.flush();

        assertTrue(authorRepository.findByLastNameAndFirstName("Tolkien", "J.R.R.").isPresent());
    }

    @Test
    @DisplayName("findByLastNameAndFirstName should return empty for unknown author")
    void findByLastNameAndFirstNameShouldReturnEmptyForUnknownAuthor() {
        assertTrue(authorRepository.findByLastNameAndFirstName("Doe", "John").isEmpty());
    }
}