package nl.miwnn.ch19.vincent.LibraryDemo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Vincent Velthuizen
 */
class BookTest {

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book("De Aanslag", 1982);
    }

    @Test
    @DisplayName("getNumberOfCopies should return zero for new book")
    void getNumberOfCopiesShouldReturnZeroForNewBook() {
        assertEquals(0, book.getNumberOfCopies());
    }

    @Test
    @DisplayName("getNumberOfCopies should return 1 for book with 1 available copy")
    void getNumberOfCopiesShouldReturn1ForBookWith1AvailableCopy() {
        Copy copyAvailable = new Copy(book);
        book.getCopies().add(copyAvailable);

        assertEquals(1, book.getNumberOfCopies());
    }

    @Test
    @DisplayName("getNumberOfCopies should return 1 for book with 1 borrowed copy")
    void getNumberOfCopiesShouldReturn1ForBookWith1BorrowedCopy() {
        Copy copyBorrowed = new Copy(book);
        copyBorrowed.setBorrower(new LibraryUser());
        book.getCopies().add(copyBorrowed);

        assertEquals(1, book.getNumberOfCopies());
    }

    @Test
    @DisplayName("getAvailableNumberOfCopies should return zero when all are borrowed")
    void getAvailableNumberOfCopiesShouldReturnZeroWhenAllAreBorrowed() {
        Copy copyBorrowed = new Copy(book);
        copyBorrowed.setBorrower(new LibraryUser());
        book.getCopies().add(copyBorrowed);

        assertEquals(0, book.getAvailableNumberOfCopies());
    }

    @Test
    @DisplayName("getAvailableNumberOfCopies should return 1 when 1 all 1 copies are available")
    void getAvailableNumberOfCopiesShouldReturn1When1All1CopiesAreAvailable() {
        Copy copyAvailable = new Copy(book);
        copyAvailable.setBorrower(new LibraryUser());
        book.getCopies().add(copyAvailable);

        assertEquals(0, book.getAvailableNumberOfCopies());
    }
}