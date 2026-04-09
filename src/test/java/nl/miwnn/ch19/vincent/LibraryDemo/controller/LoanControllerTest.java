package nl.miwnn.ch19.vincent.LibraryDemo.controller;

import nl.miwnn.ch19.vincent.LibraryDemo.model.Book;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Copy;
import nl.miwnn.ch19.vincent.LibraryDemo.model.LibraryUser;
import nl.miwnn.ch19.vincent.LibraryDemo.service.CopyService;
import nl.miwnn.ch19.vincent.LibraryDemo.service.LoanService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author Vincent Velthuizen
 */
@ExtendWith(MockitoExtension.class)
class LoanControllerTest {

    @Mock
    private CopyService copyService;

    @Mock
    private LoanService loanService;

    @InjectMocks
    private LoanController loanController;

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
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String result = loanController.borrowCopy(1L, authOf(testUser()), redirectAttributes);

        assertTrue(result.startsWith("redirect:"));
        assertTrue(result.contains("The Hobbit"));
    }

    @Test
    @DisplayName("borrowCopy should set successMessage on success")
    void borrowCopyShouldSetSuccessMessageOnSuccess() {
        when(copyService.findById(1L)).thenReturn(copyOfBook("The Hobbit"));
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        loanController.borrowCopy(1L, authOf(testUser()), redirectAttributes);

        assertTrue(redirectAttributes.getFlashAttributes().containsKey("successMessage"));
    }

    @Test
    @DisplayName("borrowCopy should set errorMessage when copy is already borrowed")
    void borrowCopyShouldSetErrorMessageWhenAlreadyBorrowed() {
        when(copyService.findById(1L)).thenReturn(copyOfBook("The Hobbit"));
        doThrow(new IllegalStateException("Copy is already borrowed"))
                .when(copyService).borrowCopy(eq(1L), any());
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        loanController.borrowCopy(1L, authOf(testUser()), redirectAttributes);

        assertTrue(redirectAttributes.getFlashAttributes().containsKey("errorMessage"));
    }

    @Test
    @DisplayName("borrowCopy should still redirect even when copy is already borrowed")
    void borrowCopyShouldStillRedirectWhenAlreadyBorrowed() {
        when(copyService.findById(1L)).thenReturn(copyOfBook("The Hobbit"));
        doThrow(new IllegalStateException("Copy is already borrowed"))
                .when(copyService).borrowCopy(eq(1L), any());

        String result = loanController.borrowCopy(1L, authOf(testUser()), new RedirectAttributesModelMap());

        assertTrue(result.startsWith("redirect:"));
    }

    @Test
    @DisplayName("returnCopy should redirect to referer on success")
    void returnCopyShouldRedirectToReferer() {
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String result = loanController.returnCopy(1L, "/book/detail/The%20Hobbit", redirectAttributes);

        assertEquals("redirect:/book/detail/The%20Hobbit", result);
    }

    @Test
    @DisplayName("returnCopy should set successMessage on success")
    void returnCopyShouldSetSuccessMessageOnSuccess() {
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        loanController.returnCopy(1L, "/book/all", redirectAttributes);

        assertTrue(redirectAttributes.getFlashAttributes().containsKey("successMessage"));
    }

    @Test
    @DisplayName("returnCopy should set errorMessage when no active loan exists")
    void returnCopyShouldSetErrorMessageOnFailure() {
        doThrow(new IllegalStateException("No active loan"))
                .when(copyService).returnCopy(1L);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        loanController.returnCopy(1L, "/book/all", redirectAttributes);

        assertTrue(redirectAttributes.getFlashAttributes().containsKey("errorMessage"));
    }

    @Test
    @DisplayName("returnCopy should still redirect even when return fails")
    void returnCopyShouldStillRedirectOnFailure() {
        doThrow(new IllegalStateException("No active loan"))
                .when(copyService).returnCopy(1L);

        String result = loanController.returnCopy(1L, "/book/all", new RedirectAttributesModelMap());

        assertTrue(result.startsWith("redirect:"));
    }
}