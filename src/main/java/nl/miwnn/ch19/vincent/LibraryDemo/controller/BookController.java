package nl.miwnn.ch19.vincent.LibraryDemo.controller;

import jakarta.validation.Valid;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Book;
import nl.miwnn.ch19.vincent.LibraryDemo.repository.AuthorRepository;
import nl.miwnn.ch19.vincent.LibraryDemo.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * @author Vincent Velthuizen
 * Handle requests regarding books
 */

@Controller
@RequestMapping("/book")
public class BookController {
    @Value("${library.name-of-the-library}")
    private String libraryName;

    private static final Logger log = LoggerFactory.getLogger(BookController.class);
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookController(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    @GetMapping({ "/", "/all"})
    public String showBookOverview(@RequestParam(required = false) String query,
                                   Model model) {
        List<Book> books = bookRepository.findAll();
        log.debug("Boekenoverzicht opgevraagd, {} boeken beschikbaar", books.size());

        List<Book> displayBooks;
        if (query != null && !query.isBlank()) {
            log.debug("Zoeken op query: {}", query);
            displayBooks = bookRepository.findBooksByTitleContainingIgnoreCase(query);
        } else {
            displayBooks = books;
        }

        model.addAttribute("paginaTitel", libraryName);
        model.addAttribute("allBooks", displayBooks);

        return "book-overview";
    }


    @GetMapping("/add")
    public String showCreateNewBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("allAuthors", authorRepository.findAll());
        return "book-add-edit";
    }

    @GetMapping({"/edit/{bookId}"})
    public String showEditForm(@PathVariable Long bookId, Model model, RedirectAttributes redirectAttributes) {
        log.info("Bewerkingsformulier geopend voor: {}", bookId);

        Optional<Book> bookToEdit = bookRepository.findById(bookId);

        if (bookToEdit.isEmpty()) {
            log.warn("Boek met id: {} is niet gevonden voor bewerking", bookId);
            redirectAttributes.addFlashAttribute("bookNotFoundForEditing",
                    String.format("Het boek met id: %d kon niet gevonden worden om te bewerken.", bookId));
            return "redirect:/book/all";
        }

        model.addAttribute("book", bookToEdit.get());
        model.addAttribute("allAuthors", authorRepository.findAll());
        return "book-add-edit";
    }

    @PostMapping("/save")
    public String saveBook(@Valid @ModelAttribute Book updatedBook,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        log.info("Boek opslaan: {}", updatedBook.getTitle());
        if (updatedBook.getId() != null &&
                bookRepository
                        .findById(updatedBook.getId())
                        .orElseThrow(() -> new IllegalArgumentException("heo"))
                        .getTitle().equals(updatedBook.getTitle())
        ) {
            log.debug("Updating book, title remains the same");
        } else {
            if (bookRepository.findBookByTitle(updatedBook.getTitle()).isPresent()) {
                log.warn("Updating book, title already exists in DB so should not be allowed");
                bindingResult.rejectValue(
                        "title",
                        "alreadyExists",
                        "Deze titel is al in gebruik");
            }
        }

        if (bindingResult.hasErrors()) {
            log.warn("Validatiefouten bij opslaan: {}",
                    bindingResult.getErrorCount());

            model.addAttribute("allAuthors", authorRepository.findAll());
            return "book-add-edit";
        }

        bookRepository.save(updatedBook);
        log.info("Nieuw boek toegevoegd: {}", updatedBook.getTitle());
        redirectAttributes.addFlashAttribute(
                "successMessage", "Boek succesvol opgeslagen!");
        return "redirect:/book/all";
    }

    @GetMapping("/delete/{bookId}")
    public String deleteBook(@PathVariable Long bookId,
                             RedirectAttributes redirectAttributes) {
        log.info("Verwijderen van boek: {}", bookId);

        bookRepository.deleteById(bookId);

        redirectAttributes.addFlashAttribute(
                "successMessage", "Boek succesvol verwijderd!");
        return "redirect:/book/all";
    }

    @GetMapping({"/{title}", "/detail/{title}"})
    public String showBookDetail(
            @PathVariable String title, Model model) {
        Optional<Book> book = bookRepository.findBookByTitle(title);
        if (book.isEmpty()) {
            return "redirect:/books";
        }
        model.addAttribute("book", book.get());
        return "book-detail";
    }
}
