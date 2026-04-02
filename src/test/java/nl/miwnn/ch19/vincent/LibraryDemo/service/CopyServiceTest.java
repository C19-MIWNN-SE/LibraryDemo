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

    @InjectMocks
    private CopyService copyService;

    @Test
    @DisplayName("borrowCopy should make copy unavailable and save")
    void borrowCopy() {
        Copy copy = new Copy();
        copy.setId(1L);

        when(copyRepository.findById(1L)).thenReturn(Optional.of(copy));

        copyService.borrowCopy(1L, new LibraryUser());

        // TODO check is assertAll works here
        assertNotNull(copy.getBorrower());
        verify(copyRepository, times(1)).save(copy);
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
}