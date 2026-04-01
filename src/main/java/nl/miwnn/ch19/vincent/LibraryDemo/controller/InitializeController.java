package nl.miwnn.ch19.vincent.LibraryDemo.controller;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Author;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Book;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Copy;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Image;
import nl.miwnn.ch19.vincent.LibraryDemo.model.LibraryUser;
import nl.miwnn.ch19.vincent.LibraryDemo.repository.AuthorRepository;
import nl.miwnn.ch19.vincent.LibraryDemo.repository.BookRepository;
import nl.miwnn.ch19.vincent.LibraryDemo.repository.CopyRepository;
import nl.miwnn.ch19.vincent.LibraryDemo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Vincent Velthuizen
 * Initialize the database when the application is started empty
 */
@Component
public class InitializeController {
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final CopyRepository copyRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final Logger log = LoggerFactory.getLogger(InitializeController.class);

    private final Map<String, Author> authorCache = new HashMap<>();

    public InitializeController(AuthorRepository authorRepository,
                                BookRepository bookRepository,
                                CopyRepository copyRepository,
                                UserRepository userRepository,
                                PasswordEncoder passwordEncoder) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.copyRepository = copyRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void seed() {
        if (authorRepository.count() == 0) {
            seedAuthors();
        }
        if (bookRepository.count() == 0) {
            seedBooks();
        }
        if (userRepository.count() == 0) {
            String password = UUID.randomUUID().toString();

            log.info("========================================================================================");
            log.info("Generated password for 'beheerder' : {}", password);
            log.info("========================================================================================");

            LibraryUser admin = new LibraryUser(
                    "beheerder",
                    passwordEncoder.encode(password),
                    true);
            userRepository.save(admin);
        }
    }

    private void seedAuthors() {
        try (CSVReader reader = new CSVReader(new InputStreamReader(
                new ClassPathResource("seedData/authors.csv").getInputStream()))) {
            reader.skip(1); // skip header
            for (String[] line : reader.readAll()) {
                String fullName = line[0].trim();
                String imageUrl = line[1].trim();

                int lastSpace = fullName.lastIndexOf(' ');
                String firstName = fullName.substring(0, lastSpace);
                String lastName = fullName.substring(lastSpace + 1);

                Author author = new Author();
                author.setFirstName(firstName);
                author.setLastName(lastName);
                author.setImage(loadImage(imageUrl));
                authorRepository.save(author);
                authorCache.put(fullName, author);
            }
        } catch (IOException | CsvException e) {
            throw new RuntimeException("Kon authors.csv niet inlezen", e);
        }
    }

    private Image loadImage(String imageUrl) throws IOException {
        String filename = "seedData" + imageUrl;
        ClassPathResource resource = new ClassPathResource(filename);

        String contentType = imageUrl.toLowerCase().endsWith(".png") ? "image/png" : "image/jpeg";

        Image image = new Image();
        image.setData(resource.getInputStream().readAllBytes());
        image.setContentType(contentType);
        return image;
    }

    private void seedBooks() {
        try (CSVReader reader = new CSVReader(new InputStreamReader(
                new ClassPathResource("seedData/books.csv").getInputStream()))) {
            reader.skip(1); // skip header
            for (String[] line : reader.readAll()) {
                String title = line[0].trim();
                String description = line[1].trim();
                String genre = line[2].trim();
                String coverImageUrl = line[3].trim();
                int totalCopies = Integer.parseInt(line[4].trim());
                String publicationYearRaw = line[5].trim();
                String authorsField = line[6].trim();

                Book book = new Book();
                book.setTitle(title);
                book.setDescription(description);
                book.setGenre(genre);
                book.setCoverImageUrl(coverImageUrl);

                if (!publicationYearRaw.isEmpty()) {
                    book.setPublicationYear(Integer.parseInt(publicationYearRaw));
                }

                for (String authorName : authorsField.split(", ")) {
                    Author author = authorCache.get(authorName.trim());
                    if (author != null) {
                        book.getAuthors().add(author);
                    } else {
                        log.warn("Auteur '{}' niet gevonden voor boek '{}'", authorName.trim(), title);
                    }
                }

                bookRepository.save(book);

                for (int i = 0; i < totalCopies; i++) {
                    copyRepository.save(new Copy(book));
                }
            }
        } catch (IOException | CsvException e) {
            throw new RuntimeException("Kon books.csv niet inlezen", e);
        }
    }
}