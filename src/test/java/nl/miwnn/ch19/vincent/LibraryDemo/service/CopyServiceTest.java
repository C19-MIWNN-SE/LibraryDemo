package nl.miwnn.ch19.vincent.LibraryDemo.service;

import nl.miwnn.ch19.vincent.LibraryDemo.model.Copy;
import nl.miwnn.ch19.vincent.LibraryDemo.model.LibraryUser;
import nl.miwnn.ch19.vincent.LibraryDemo.repository.CopyRepository;
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
class CopyServiceTest {

    @Mock
    private CopyRepository copyRepository;

    @Mock
    private LoanService loanService;

    @InjectMocks
    private CopyService copyService;

    @Test
    @DisplayName("borrowCopy should mark copy unavailable and save")
    void borrowCopyShouldMarkCopyUnavailableAndSave() {
        Copy copy = new Copy();
        copy.setId(1L);
        copy.setAvailable(true);

        when(copyRepository.findById(1L)).thenReturn(Optional.of(copy));

        copyService.borrowCopy(1L, new LibraryUser());

        assertFalse(copy.getAvailable());
        verify(copyRepository, times(1)).save(copy);
    }

    @Test
    @DisplayName("borrowCopy should call loanService.startLoan after saving")
    void borrowCopyShouldDelegateToLoanService() {
        Copy copy = new Copy();
        copy.setId(1L);
        LibraryUser borrower = new LibraryUser();

        when(copyRepository.findById(1L)).thenReturn(Optional.of(copy));

        copyService.borrowCopy(1L, borrower);

        verify(loanService, times(1)).startLoan(copy, borrower);
    }

    @Test
    @DisplayName("returnCopy should mark copy available and save")
    void returnCopyShouldMarkCopyAvailableAndSave() {
        Copy copy = new Copy();
        copy.setId(2L);
        copy.setAvailable(false);

        when(copyRepository.findById(2L)).thenReturn(Optional.of(copy));

        copyService.returnCopy(2L);

        assertTrue(copy.getAvailable());
        verify(copyRepository, times(1)).save(copy);
    }

    @Test
    @DisplayName("returnCopy should call loanService.closeLoan after saving")
    void returnCopyShouldDelegateToLoanService() {
        Copy copy = new Copy();
        copy.setId(2L);

        when(copyRepository.findById(2L)).thenReturn(Optional.of(copy));

        copyService.returnCopy(2L);

        verify(loanService, times(1)).closeLoan(copy);
    }
}