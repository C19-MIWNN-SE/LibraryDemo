package nl.miwnn.ch19.vincent.LibraryDemo.controller;

import nl.miwnn.ch19.vincent.LibraryDemo.model.Book;
import nl.miwnn.ch19.vincent.LibraryDemo.service.AuthorService;
import nl.miwnn.ch19.vincent.LibraryDemo.service.BookService;
import nl.miwnn.ch19.vincent.LibraryDemo.service.GenreService;
import nl.miwnn.ch19.vincent.LibraryDemo.service.LoanService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.ui.Model;

import java.util.List;

import static org.mockito.Mockito.*;

/**
 * @author Vincent Velthuizen
 */
@ExtendWith(MockitoExtension.class)
class BookControllerDetailTest {

    @Mock
    private BookService bookService;

    @Mock
    private AuthorService authorService;

    @Mock
    private GenreService genreService;

    @Mock
    private LoanService loanService;

    @InjectMocks
    private BookController bookController;

    @Test
    @DisplayName("showBookDetail should set borrowDisabled to false when not authenticated")
    void showBookDetailShouldSetBorrowDisabledFalseWhenNotAuthenticated() {
        when(bookService.findByTitle("The Hobbit")).thenReturn(new Book("The Hobbit", 1937));
        Model model = mock(Model.class);

        bookController.showBookDetail("The Hobbit", model, null);

        verify(model).addAttribute("borrowDisabled", false);
    }

    @Test
    @DisplayName("showBookDetail should set borrowDisabled to false for a regular user")
    void showBookDetailShouldSetBorrowDisabledFalseForRegularUser() {
        when(bookService.findByTitle("The Hobbit")).thenReturn(new Book("The Hobbit", 1937));
        Model model = mock(Model.class);
        Authentication userAuth = new UsernamePasswordAuthenticationToken(
                "user", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        bookController.showBookDetail("The Hobbit", model, userAuth);

        verify(model).addAttribute("borrowDisabled", false);
    }

    @Test
    @DisplayName("showBookDetail should set borrowDisabled to true for admin-only user")
    void showBookDetailShouldSetBorrowDisabledTrueForAdminOnlyUser() {
        when(bookService.findByTitle("The Hobbit")).thenReturn(new Book("The Hobbit", 1937));
        Model model = mock(Model.class);
        Authentication adminOnlyAuth = new UsernamePasswordAuthenticationToken(
                "admin", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        bookController.showBookDetail("The Hobbit", model, adminOnlyAuth);

        verify(model).addAttribute("borrowDisabled", true);
    }

    @Test
    @DisplayName("showBookDetail should set borrowDisabled to false for user with both roles")
    void showBookDetailShouldSetBorrowDisabledFalseForUserWithBothRoles() {
        when(bookService.findByTitle("The Hobbit")).thenReturn(new Book("The Hobbit", 1937));
        Model model = mock(Model.class);
        Authentication adminUserAuth = new UsernamePasswordAuthenticationToken(
                "admin", null, List.of(
                        new SimpleGrantedAuthority("ROLE_USER"),
                        new SimpleGrantedAuthority("ROLE_ADMIN")));

        bookController.showBookDetail("The Hobbit", model, adminUserAuth);

        verify(model).addAttribute("borrowDisabled", false);
    }
}