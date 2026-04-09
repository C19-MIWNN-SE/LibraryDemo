package nl.miwnn.ch19.vincent.LibraryDemo.service;

import jakarta.persistence.EntityNotFoundException;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Book;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Copy;
import nl.miwnn.ch19.vincent.LibraryDemo.model.LibraryUser;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Loan;
import nl.miwnn.ch19.vincent.LibraryDemo.repository.LoanRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Vincent Velthuizen
 */
@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanService loanService;

    @Test
    @DisplayName("startLoan should save a new loan")
    void startLoanShouldSaveNewLoan() {
        Book book = new Book("The Hobbit", 1937);
        Copy copy = new Copy(book);
        copy.setId(1L);
        LibraryUser borrower = new LibraryUser("jan", "hash", false);

        when(loanRepository.existsByCopyBookAndBorrowerAndReturnDateIsNull(book, borrower)).thenReturn(false);

        loanService.startLoan(copy, borrower);

        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    @DisplayName("startLoan should throw when copy is already borrowed")
    void startLoanShouldThrowWhenCopyAlreadyBorrowed() {
        Copy copy = new Copy();
        copy.setId(1L);
        LibraryUser borrower = new LibraryUser();

        when(loanRepository.findByCopyAndReturnDateIsNull(copy))
                .thenReturn(Optional.of(new Loan(copy, borrower)));

        assertThrows(IllegalStateException.class, () -> loanService.startLoan(copy, borrower));
        verify(loanRepository, never()).save(any());
    }

    @Test
    @DisplayName("startLoan should throw when user already has active loan for same book")
    void startLoanShouldThrowWhenUserAlreadyHasActiveLoanForSameBook() {
        Book book = new Book("The Hobbit", 1937);
        Copy copy = new Copy(book);
        LibraryUser borrower = new LibraryUser("jan", "hash", false);

        when(loanRepository.findByCopyAndReturnDateIsNull(copy)).thenReturn(Optional.empty());
        when(loanRepository.existsByCopyBookAndBorrowerAndReturnDateIsNull(book, borrower)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> loanService.startLoan(copy, borrower));
        verify(loanRepository, never()).save(any());
    }

    @Test
    @DisplayName("closeLoan should set returnDate and save")
    void closeLoanShouldSetReturnDateAndSave() {
        Copy copy = new Copy();
        copy.setId(1L);
        LibraryUser borrower = new LibraryUser("jan", "hash", false);
        Loan loan = new Loan(copy, borrower);

        when(loanRepository.findByCopyAndReturnDateIsNull(copy)).thenReturn(Optional.of(loan));

        loanService.closeLoan(copy);

        assertNotNull(loan.getReturnDate());
        verify(loanRepository, times(1)).save(loan);
    }

    @Test
    @DisplayName("closeLoan should throw when no active loan exists")
    void closeLoanShouldThrowWhenNoActiveLoanExists() {
        Copy copy = new Copy();
        copy.setId(99L);

        when(loanRepository.findByCopyAndReturnDateIsNull(copy)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> loanService.closeLoan(copy));
    }
}