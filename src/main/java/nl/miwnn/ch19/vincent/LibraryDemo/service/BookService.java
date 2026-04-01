package nl.miwnn.ch19.vincent.LibraryDemo.service;

import jakarta.persistence.EntityNotFoundException;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Book;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Copy;
import nl.miwnn.ch19.vincent.LibraryDemo.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Vincent Velthuizen
 * Handle business logic for books
 */
@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> searchByTitle(String keyword) {
        return bookRepository.findBooksByTitleContainingIgnoreCase(keyword);
    }

    public Book findByTitle(String title) {
        return bookRepository.findBookByTitle(title).orElseThrow(
                () -> new EntityNotFoundException(String.format("No book found with title: %s", title)));
    }

    public void saveBook(Book book) {
        bookRepository.save(book);
    }

    public void deleteBook(String title) {
        bookRepository.delete(bookRepository.findBookByTitle(title).orElseThrow(
                () -> new EntityNotFoundException(String.format("No book found with title: %s", title))));
    }

    public void addCopyToBookWithTitle(String title) {
        Book book = findByTitle(title);
        book.getCopies().add(new Copy(book));
        bookRepository.save(book);
    }
}
