package nl.miwnn.ch19.vincent.LibraryDemo.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Vincent Velthuizen
 */
class LibraryUserTest {

    private LibraryUser userWithId(Long id) {
        LibraryUser user = new LibraryUser("gebruiker", "wachtwoord", false);
        user.setId(id);
        return user;
    }

    @Test
    @DisplayName("equals should return true for two users with the same id")
    void equalsShouldReturnTrueForSameId() {
        LibraryUser a = userWithId(1L);
        LibraryUser b = userWithId(1L);

        assertEquals(a, b);
    }

    @Test
    @DisplayName("equals should return false for two users with different ids")
    void equalsShouldReturnFalseForDifferentIds() {
        LibraryUser a = userWithId(1L);
        LibraryUser b = userWithId(2L);

        assertNotEquals(a, b);
    }

    @Test
    @DisplayName("equals should return false when compared to null")
    void equalsShouldReturnFalseForNull() {
        LibraryUser user = userWithId(1L);

        assertNotEquals(null, user);
    }

    @Test
    @DisplayName("equals should return false for a user without an id")
    void equalsShouldReturnFalseForUnsavedUser() {
        LibraryUser a = new LibraryUser("gebruiker", "wachtwoord", false);
        LibraryUser b = new LibraryUser("gebruiker", "wachtwoord", false);

        assertNotEquals(a, b);
    }

    @Test
    @DisplayName("equals should return true when comparing an object to itself")
    void equalsShouldReturnTrueForSameReference() {
        LibraryUser user = userWithId(1L);

        assertEquals(user, user);
    }
}