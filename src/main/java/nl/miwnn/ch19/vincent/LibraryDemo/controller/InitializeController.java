package nl.miwnn.ch19.vincent.LibraryDemo.controller;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import nl.miwnn.ch19.vincent.LibraryDemo.model.*;
import nl.miwnn.ch19.vincent.LibraryDemo.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Vincent Velthuizen
 * Initialize the database when the application is started empty
 */
@Component
public class InitializeController {
    @Value("${library.seed.admin.password}")
    private String adminPassword;

    @Value("${library.seed.user.password}")
    private String userPassword;

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final CopyRepository copyRepository;
    private final GenreRepository genreRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final Logger log = LoggerFactory.getLogger(InitializeController.class);

    private final Map<String, Author> authorCache = new HashMap<>();
    private final Map<String, Genre> genreCache = new HashMap<>();

    public InitializeController(AuthorRepository authorRepository,
                                BookRepository bookRepository,
                                CopyRepository copyRepository,
                                GenreRepository genreRepository,
                                UserRepository userRepository,
                                PasswordEncoder passwordEncoder) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.copyRepository = copyRepository;
        this.genreRepository = genreRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void seed() {
        if (authorRepository.count() == 0) {
            seedAuthors();
        }
        if (genreRepository.count() == 0) {
            seedGenres();
        }
        if (bookRepository.count() == 0) {
            seedBooks();
        }
        if (userRepository.count() == 0) {
            seedUsers();
        }
    }

    private void seedUsers() {
        LibraryUser admin = new LibraryUser(
                "beheerder",
                passwordEncoder.encode(adminPassword),
                true);
        userRepository.save(admin);

        LibraryUser gebruiker = new LibraryUser(
                "gebruiker",
                passwordEncoder.encode(userPassword),
                false);
        userRepository.save(gebruiker);

        List<Copy> hobbitCopies = copyRepository.findByBookTitle("The Hobbit");
        if (!hobbitCopies.isEmpty()) {
            Copy copy = hobbitCopies.get(0);
            copy.setBorrower(gebruiker);
            copy.setBorrowedAt(LocalDateTime.now().minusDays(3));
            copyRepository.save(copy);
        } else {
            log.warn("Kon geen exemplaar van 'The Hobbit' vinden voor 'gebruiker'");
        }

        log.info("Gebruikers aangemaakt: 2 (beheerder, gebruiker)");
    }

    private void seedGenres() {
        addGenre(new Genre("Science Fiction and Fantasy", "SFF"));
        addGenre(new Genre("Young Adult", "YA"));
        addGenre(new Genre("Children's books", "Child"));
        log.info("Genres aangemaakt: {}", genreCache.size());
    }

    private void addGenre(Genre genre) {
        genreRepository.save(genre);
        genreCache.put(genre.getShortName(), genre);
    }

    private void seedAuthors() {
        int count = 0;
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
                count++;
            }
        } catch (IOException | CsvException e) {
            throw new RuntimeException("Kon authors.csv niet inlezen", e);
        }
        log.info("Auteurs aangemaakt: {}", count);
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
        int bookCount = 0;
        int copyCount = 0;
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
                book.setGenre(genreCache.get(genre));
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
                bookCount++;

                for (int i = 0; i < totalCopies; i++) {
                    copyRepository.save(new Copy(book));
                    copyCount++;
                }
            }
        } catch (IOException | CsvException e) {
            throw new RuntimeException("Kon books.csv niet inlezen", e);
        }
        log.info("Boeken aangemaakt: {}, exemplaren aangemaakt: {}", bookCount, copyCount);
    }
}