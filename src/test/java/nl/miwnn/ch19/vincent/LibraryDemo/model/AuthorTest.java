package nl.miwnn.ch19.vincent.LibraryDemo.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Vincent Velthuizen
 */
class AuthorTest {

    @Test
    @DisplayName("getFullName should combine first and last names")
    void getFullNameShouldCombineFirstAndLastNames() {
        Author author = new Author();
        author.setFirstName("J.R.R.");
        author.setLastName("Tolkien");

        assertEquals("J.R.R. Tolkien", author.getFullName());
    }

}