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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Vincent Velthuizen
 * Initialize the database when the application is started empty
 */
@Component
public class InitializeController {
    @Value("${library.seed.admin.password:#{T(java.util.UUID).randomUUID().toString().replace('-','').substring(0,12)}}")
    private String adminPassword;

    @Value("${library.seed.user.password:#{T(java.util.UUID).randomUUID().toString().replace('-','').substring(0,12)}}")
    private String userPassword;

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final CopyRepository copyRepository;
    private final GenreRepository genreRepository;
    private final UserRepository userRepository;
    private final LoanRepository loanRepository;
    private final PasswordEncoder passwordEncoder;

    private final Logger log = LoggerFactory.getLogger(InitializeController.class);

    private final Map<String, Author> authorCache = new HashMap<>();
    private final Map<String, Genre> genreCache = new HashMap<>();

    public InitializeController(AuthorRepository authorRepository,
                                BookRepository bookRepository,
                                CopyRepository copyRepository,
                                GenreRepository genreRepository,
                                UserRepository userRepository,
                                LoanRepository loanRepository,
                                PasswordEncoder passwordEncoder) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.copyRepository = copyRepository;
        this.genreRepository = genreRepository;
        this.userRepository = userRepository;
        this.loanRepository = loanRepository;
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
        if (loanRepository.count() == 0) {
            seedLoans();
        }
    }

    private void seedUsers() {
        userRepository.save(new LibraryUser(
                "beheerder", passwordEncoder.encode(adminPassword), true));

        userRepository.save(new LibraryUser(
                "gebruiker", passwordEncoder.encode(userPassword), false));

        System.out.println("=== SEED WACHTWOORDEN ===");
        System.out.println("beheerder : " + adminPassword);
        System.out.println("gebruiker : " + userPassword);
        System.out.println("========================");

        String emmaPassword = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        userRepository.save(new LibraryUser(
                "Emma", passwordEncoder.encode(emmaPassword), false));

        String thomasPassword = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        userRepository.save(new LibraryUser(
                "Thomas", passwordEncoder.encode(thomasPassword), false));

        log.info("Gebruikers aangemaakt: 4 (beheerder, gebruiker, Emma, Thomas)");
    }

    private void seedLoans() {
        LibraryUser gebruiker = userRepository.findByUsername("gebruiker").orElseThrow();
        LibraryUser emma = userRepository.findByUsername("Emma").orElseThrow();
        LibraryUser thomas = userRepository.findByUsername("Thomas").orElseThrow();

        // gebruiker: Hobbit → LotR → Mistborn 1 → Mistborn 2 (actief)
        createClosedLoan(firstCopy("The Hobbit"), gebruiker, -120, -100);
        createClosedLoan(firstCopy("The Lord of the Rings"), gebruiker, -85, -72);
        createClosedLoan(firstCopy("The Final Empire"), gebruiker, -60, -46);
        createActiveLoan(firstAvailableCopy("The Well of Ascension"), gebruiker, -10);

        // emma: Paper Towns → Looking for Alaska → Fault in Our Stars → Turtles (actief)
        createClosedLoan(firstCopy("Paper Towns"), emma, -150, -135);
        createClosedLoan(firstCopy("Looking for Alaska"), emma, -120, -104);
        createClosedLoan(firstCopy("The Fault in Our Stars"), emma, -80, -66);
        createActiveLoan(firstAvailableCopy("Turtles All the Way Down"), emma, -5);

        // thomas: Name of the Wind → Wise Man's Fear → LotR → Final Name of the Rings (actief)
        createClosedLoan(firstCopy("The Name of the Wind"), thomas, -200, -183);
        createClosedLoan(firstCopy("The Wise Man's Fear"), thomas, -160, -145);
        createClosedLoan(firstCopy("The Lord of the Rings"), thomas, -100, -86);
        createActiveLoan(firstAvailableCopy("The Final Name of the Rings"), thomas, -8);

        log.info("Leengeschiedenis aangemaakt voor gebruiker, Emma en Thomas");
    }

    private Copy firstCopy(String bookTitle) {
        List<Copy> copies = copyRepository.findByBookTitle(bookTitle);
        if (copies.isEmpty()) {
            throw new IllegalStateException("Geen exemplaren gevonden voor: " + bookTitle);
        }
        return copies.get(0);
    }

    private Copy firstAvailableCopy(String bookTitle) {
        return copyRepository.findByBookTitle(bookTitle).stream()
                .filter(Copy::getAvailable)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Geen beschikbaar exemplaar voor: " + bookTitle));
    }

    private void createClosedLoan(Copy copy, LibraryUser borrower, int startDaysAgo, int endDaysAgo) {
        Loan loan = new Loan(copy, borrower);
        loan.setBorrowDate(LocalDate.now().plusDays(startDaysAgo));
        loan.setReturnDate(LocalDate.now().plusDays(endDaysAgo));
        loanRepository.save(loan);
    }

    private void createActiveLoan(Copy copy, LibraryUser borrower, int startDaysAgo) {
        Loan loan = new Loan(copy, borrower);
        loan.setBorrowDate(LocalDate.now().plusDays(startDaysAgo));
        loanRepository.save(loan);
        copy.setAvailable(false);
        copyRepository.save(copy);
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