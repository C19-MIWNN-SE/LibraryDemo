package nl.miwnn.ch19.vincent.LibraryDemo.controller;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Author;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Book;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Copy;
import nl.miwnn.ch19.vincent.LibraryDemo.repository.AuthorRepository;
import nl.miwnn.ch19.vincent.LibraryDemo.repository.BookRepository;
import nl.miwnn.ch19.vincent.LibraryDemo.repository.CopyRepository;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

/**
 * @author Vincent Velthuizen
 * Initialize the database when the application is started empty
 */
@Controller
public class InitializeController {
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final CopyRepository copyRepository;

    public InitializeController(AuthorRepository authorRepository, BookRepository bookRepository, CopyRepository copyRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.copyRepository = copyRepository;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void seed() {
        if (authorRepository.count() == 0) {
            seedAuthors();
        }
        if (bookRepository.count() == 0) {
            seedBooks();
        }
    }

    private void seedAuthors() {
        try {
            ClassPathResource resource = new ClassPathResource("seedData/authors.csv");
            Reader reader = new InputStreamReader(resource.getInputStream());

            CsvToBean<Author> csvToBean = new CsvToBeanBuilder<Author>(reader)
                    .withType(Author.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            authorRepository.saveAll(csvToBean.parse());
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    private void seedBooks() {
        try {
            ClassPathResource resource =
                    new ClassPathResource("seedData/books.csv");
            Reader reader = new InputStreamReader(
                    resource.getInputStream());
            CsvToBean<Book> csvToBean =
                    new CsvToBeanBuilder<Book>(reader)
                            .withType(Book.class)
                            .withIgnoreLeadingWhiteSpace(true)
                            .build();
            List<Book> books = csvToBean.parse();
            List<Author> authors = authorRepository.findAll();
            for (int i = 0; i < books.size(); i++) {
                Book book = books.get(i);
                book.getAuthors().add(authors.get(i % authors.size()));
                bookRepository.save(book);
                copyRepository.save(new Copy(book));
                copyRepository.save(new Copy(book));
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Kon books.csv niet inlezen", e);
        }
    }
}
