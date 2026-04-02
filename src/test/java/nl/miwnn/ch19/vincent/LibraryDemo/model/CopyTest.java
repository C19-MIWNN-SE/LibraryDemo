package nl.miwnn.ch19.vincent.LibraryDemo.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Vincent Velthuizen
 */
class CopyTest {

    @Test
    @DisplayName("getAvailable should return false when a borrower is present")
    void getAvailableShouldReturnFalseWhenABorrowerIsPresent() {
        Copy copy = new Copy();
        copy.setBorrower(new LibraryUser());

        assertFalse(copy.getAvailable());
    }

    @Test
    @DisplayName("getAvailable should return true when no borrower is present")
    void getAvailableShouldReturnTrueWhenNoBorrowerIsPresent() {
        Copy copy = new Copy();
        assertTrue(copy.getAvailable());
    }

}