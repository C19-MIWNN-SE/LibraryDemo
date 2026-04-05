package nl.miwnn.ch19.vincent.LibraryDemo.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

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

    @Test
    @DisplayName("getDaysOut should return 0 when borrowedAt is null")
    void getDaysOutShouldReturnZeroWhenBorrowedAtIsNull() {
        Copy copy = new Copy();

        assertEquals(0, copy.getDaysOut());
    }

    @Test
    @DisplayName("getDaysOut should return 0 when borrowed today")
    void getDaysOutShouldReturnZeroWhenBorrowedToday() {
        Copy copy = new Copy();
        copy.setBorrowedAt(LocalDateTime.now());

        assertEquals(0, copy.getDaysOut());
    }

    @Test
    @DisplayName("getDaysOut should return correct number of days when borrowed in the past")
    void getDaysOutShouldReturnCorrectDaysWhenBorrowedInThePast() {
        Copy copy = new Copy();
        copy.setBorrowedAt(LocalDateTime.now().minusDays(5));

        assertEquals(5, copy.getDaysOut());
    }

}