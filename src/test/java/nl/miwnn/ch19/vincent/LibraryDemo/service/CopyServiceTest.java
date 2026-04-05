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

import java.time.LocalDateTime;
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

    @InjectMocks
    private CopyService copyService;

    @Test
    @DisplayName("borrowCopy should make copy unavailable and save")
    void borrowCopy() {
        Copy copy = new Copy();
        copy.setId(1L);

        when(copyRepository.findById(1L)).thenReturn(Optional.of(copy));

        copyService.borrowCopy(1L, new LibraryUser());

        assertAll(
                () -> assertNotNull(copy.getBorrower()),
                () -> verify(copyRepository, times(1)).save(copy)
        );
    }

    @Test
    @DisplayName("borrowCopy should set borrowedAt to approximately now")
    void borrowCopyShouldSetBorrowedAt() {
        Copy copy = new Copy();
        copy.setId(1L);
        LocalDateTime before = LocalDateTime.now();

        when(copyRepository.findById(1L)).thenReturn(Optional.of(copy));

        copyService.borrowCopy(1L, new LibraryUser());

        assertNotNull(copy.getBorrowedAt());
        assertFalse(copy.getBorrowedAt().isBefore(before));
    }

    @Test
    @DisplayName("borrowCopy should throw an exception when already borrowed")
    void borrowCopyShouldThrowAnExceptionWhenAlreadyBorrowed() {
        Copy copy = new Copy();
        copy.setId(2L);
        copy.setBorrower(new LibraryUser());

        when(copyRepository.findById(2L)).thenReturn(Optional.of(copy));

        assertThrows(IllegalStateException.class, () -> copyService.borrowCopy(2L, new LibraryUser()));
        verify(copyRepository, never()).save(any());
    }

    @Test
    @DisplayName("returnCopy should make copy available and save")
    void returnCopyShouldMakeCopyAvailableAndSave() {
        Copy copy = new Copy();
        copy.setId(3L);
        copy.setBorrower(new LibraryUser());
        copy.setBorrowedAt(LocalDateTime.now().minusDays(2));

        when(copyRepository.findById(3L)).thenReturn(Optional.of(copy));

        copyService.returnCopy(3L);

        assertAll(
                () -> assertNull(copy.getBorrower()),
                () -> verify(copyRepository, times(1)).save(copy)
        );
    }

    @Test
    @DisplayName("returnCopy should clear borrowedAt")
    void returnCopyShouldClearBorrowedAt() {
        Copy copy = new Copy();
        copy.setId(4L);
        copy.setBorrower(new LibraryUser());
        copy.setBorrowedAt(LocalDateTime.now().minusDays(2));

        when(copyRepository.findById(4L)).thenReturn(Optional.of(copy));

        copyService.returnCopy(4L);

        assertNull(copy.getBorrowedAt());
    }

    @Test
    @DisplayName("returnCopy should throw an exception when already available")
    void returnCopyShouldThrowAnExceptionWhenAlreadyAvailable() {
        Copy copy = new Copy();
        copy.setId(5L);

        when(copyRepository.findById(5L)).thenReturn(Optional.of(copy));

        assertThrows(IllegalStateException.class, () -> copyService.returnCopy(5L));
        verify(copyRepository, never()).save(any());
    }
}