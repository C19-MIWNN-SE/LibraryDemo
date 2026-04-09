package nl.miwnn.ch19.vincent.LibraryDemo.repository;

import nl.miwnn.ch19.vincent.LibraryDemo.model.Book;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Copy;
import nl.miwnn.ch19.vincent.LibraryDemo.model.LibraryUser;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Loan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Vincent Velthuizen
 */
@DataJpaTest
@ActiveProfiles("test")
class LoanRepositoryTest {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Copy copy;
    private LibraryUser borrower;

    @BeforeEach
    void setUp() {
        Book book = new Book("The Hobbit", 1937);
        entityManager.persist(book);

        copy = new Copy(book);
        entityManager.persist(copy);

        borrower = new LibraryUser("jan", "hash", false);
        entityManager.persist(borrower);

        entityManager.flush();
    }

    @Test
    @DisplayName("findByCopyAndReturnDateIsNull should find active loan")
    void findByCopyAndReturnDateIsNullShouldFindActiveLoan() {
        entityManager.persist(new Loan(copy, borrower));
        entityManager.flush();

        assertTrue(loanRepository.findByCopyAndReturnDateIsNull(copy).isPresent());
    }

    @Test
    @DisplayName("findByCopyAndReturnDateIsNull should return empty for closed loan")
    void findByCopyAndReturnDateIsNullShouldReturnEmptyForClosedLoan() {
        Loan loan = new Loan(copy, borrower);
        loan.setReturnDate(LocalDate.now());
        entityManager.persist(loan);
        entityManager.flush();

        assertTrue(loanRepository.findByCopyAndReturnDateIsNull(copy).isEmpty());
    }

    @Test
    @DisplayName("existsByCopyBookAndBorrowerAndReturnDateIsNull should return true for active loan")
    void existsByCopyBookAndBorrowerShouldReturnTrueForActiveLoan() {
        entityManager.persist(new Loan(copy, borrower));
        entityManager.flush();

        assertTrue(loanRepository.existsByCopyBookAndBorrowerAndReturnDateIsNull(
                copy.getBook(), borrower));
    }

    @Test
    @DisplayName("existsByCopyBookAndBorrowerAndReturnDateIsNull should return false after return")
    void existsByCopyBookAndBorrowerShouldReturnFalseAfterReturn() {
        Loan loan = new Loan(copy, borrower);
        loan.setReturnDate(LocalDate.now());
        entityManager.persist(loan);
        entityManager.flush();

        assertFalse(loanRepository.existsByCopyBookAndBorrowerAndReturnDateIsNull(
                copy.getBook(), borrower));
    }
}