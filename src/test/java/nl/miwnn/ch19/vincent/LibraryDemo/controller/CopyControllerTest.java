package nl.miwnn.ch19.vincent.LibraryDemo.controller;

import nl.miwnn.ch19.vincent.LibraryDemo.model.Book;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Copy;
import nl.miwnn.ch19.vincent.LibraryDemo.model.LibraryUser;
import nl.miwnn.ch19.vincent.LibraryDemo.service.CopyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * @author Vincent Velthuizen
 */
@ExtendWith(MockitoExtension.class)
class CopyControllerTest {

    @Mock
    private CopyService copyService;

    @InjectMocks
    private CopyController copyController;

    private LibraryUser testUser() {
        return new LibraryUser("testuser", "password", false);
    }

    private Authentication authOf(LibraryUser user) {
        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }

    private Copy copyOfBook(String title) {
        Book book = new Book(title, 2000);
        return new Copy(book);
    }

    @Test
    @DisplayName("borrowCopy should redirect to book detail on success")
    void borrowCopyShouldRedirectToBookDetailOnSuccess() {
        when(copyService.findById(1L)).thenReturn(copyOfBook("The Hobbit"));
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String result = copyController.borrowCopy(1L, authOf(testUser()), redirectAttributes);

        assertTrue(result.startsWith("redirect:"));
        assertTrue(result.contains("The Hobbit"));
    }

    @Test
    @DisplayName("borrowCopy should set successMessage on success")
    void borrowCopyShouldSetSuccessMessageOnSuccess() {
        when(copyService.findById(1L)).thenReturn(copyOfBook("The Hobbit"));
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        copyController.borrowCopy(1L, authOf(testUser()), redirectAttributes);

        assertTrue(redirectAttributes.getFlashAttributes().containsKey("successMessage"));
    }

    @Test
    @DisplayName("borrowCopy should set errorMessage when copy is already borrowed")
    void borrowCopyShouldSetErrorMessageWhenAlreadyBorrowed() {
        when(copyService.findById(1L)).thenReturn(copyOfBook("The Hobbit"));
        doThrow(new IllegalStateException("Copy is already borrowed"))
                .when(copyService).borrowCopy(eq(1L), any());
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        copyController.borrowCopy(1L, authOf(testUser()), redirectAttributes);

        assertTrue(redirectAttributes.getFlashAttributes().containsKey("errorMessage"));
    }

    @Test
    @DisplayName("borrowCopy should still redirect even when copy is already borrowed")
    void borrowCopyShouldStillRedirectWhenAlreadyBorrowed() {
        when(copyService.findById(1L)).thenReturn(copyOfBook("The Hobbit"));
        doThrow(new IllegalStateException("Copy is already borrowed"))
                .when(copyService).borrowCopy(eq(1L), any());

        String result = copyController.borrowCopy(1L, authOf(testUser()), new RedirectAttributesModelMap());

        assertTrue(result.startsWith("redirect:"));
    }

    @Test
    @DisplayName("returnCopy should redirect to book detail on success")
    void returnCopyShouldRedirectToBookDetailOnSuccess() {
        when(copyService.findById(1L)).thenReturn(copyOfBook("The Hobbit"));
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String result = copyController.returnCopy(1L, redirectAttributes);

        assertTrue(result.startsWith("redirect:"));
        assertTrue(result.contains("The Hobbit"));
    }

    @Test
    @DisplayName("returnCopy should set successMessage on success")
    void returnCopyShouldSetSuccessMessageOnSuccess() {
        when(copyService.findById(1L)).thenReturn(copyOfBook("The Hobbit"));
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        copyController.returnCopy(1L, redirectAttributes);

        assertTrue(redirectAttributes.getFlashAttributes().containsKey("successMessage"));
    }

    @Test
    @DisplayName("returnCopy should set errorMessage when copy is already available")
    void returnCopyShouldSetErrorMessageWhenAlreadyAvailable() {
        when(copyService.findById(1L)).thenReturn(copyOfBook("The Hobbit"));
        doThrow(new IllegalStateException("Copy is already available"))
                .when(copyService).returnCopy(1L);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        copyController.returnCopy(1L, redirectAttributes);

        assertTrue(redirectAttributes.getFlashAttributes().containsKey("errorMessage"));
    }

    @Test
    @DisplayName("returnCopy should still redirect even when copy is already available")
    void returnCopyShouldStillRedirectWhenAlreadyAvailable() {
        when(copyService.findById(1L)).thenReturn(copyOfBook("The Hobbit"));
        doThrow(new IllegalStateException("Copy is already available"))
                .when(copyService).returnCopy(1L);

        String result = copyController.returnCopy(1L, new RedirectAttributesModelMap());

        assertTrue(result.startsWith("redirect:"));
    }
}