package nl.miwnn.ch19.vincent.LibraryDemo.service;

import nl.miwnn.ch19.vincent.LibraryDemo.model.Book;
import nl.miwnn.ch19.vincent.LibraryDemo.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Vincent Velthuizen
 */
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    @DisplayName("getAllBooks should return what the repository returns")
    void getAllBooksShouldReturnWhatRepositoryReturns() {
        List<Book> books = List.of(
                new Book("De Aanslag", 1982),
                new Book("Max Havelaar", 1860));
        when(bookRepository.findAll()).thenReturn(books);

        List<Book> result = bookService.getAllBooks();

        assertEquals(books, result);
    }

    @Test
    @DisplayName("deleteBook should call repository delete with the matching book")
    void deleteBookShouldCallRepositoryDelete() {
        Book book = new Book("The Hobbit", 1937);
        when(bookRepository.findBookByTitle("The Hobbit")).thenReturn(Optional.of(book));

        bookService.deleteBook("The Hobbit");

        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    @DisplayName("deleteBook should throw when the book does not exist")
    void deleteBookShouldThrowWhenBookNotFound() {
        when(bookRepository.findBookByTitle("Bestaat niet")).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> bookService.deleteBook("Bestaat niet"));
        verify(bookRepository, never()).delete(any());
    }
}