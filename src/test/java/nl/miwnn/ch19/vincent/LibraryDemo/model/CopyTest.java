package nl.miwnn.ch19.vincent.LibraryDemo.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Vincent Velthuizen
 */
class CopyTest {

    @Test
    @DisplayName("new Copy(book) should be available by default")
    void newCopyWithBookShouldBeAvailableByDefault() {
        Copy copy = new Copy(new Book("The Hobbit", 1937));

        assertTrue(copy.getAvailable());
    }

    @Test
    @DisplayName("setAvailable false should make copy unavailable")
    void setAvailableFalseShouldMakeCopyUnavailable() {
        Copy copy = new Copy(new Book("The Hobbit", 1937));
        copy.setAvailable(false);

        assertFalse(copy.getAvailable());
    }

    @Test
    @DisplayName("setAvailable true should make copy available again")
    void setAvailableTrueShouldMakeCopyAvailableAgain() {
        Copy copy = new Copy(new Book("The Hobbit", 1937));
        copy.setAvailable(false);
        copy.setAvailable(true);

        assertTrue(copy.getAvailable());
    }
}