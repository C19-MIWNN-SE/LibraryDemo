package nl.miwnn.ch19.vincent.LibraryDemo.controller;

import jakarta.validation.Valid;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Book;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Copy;
import nl.miwnn.ch19.vincent.LibraryDemo.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Vincent Velthuizen
 * Handle requests regarding books
 */

@Controller
public class BookController {
    @Value("${library.name-of-the-library}")
    private String libraryName;

    private static final Logger log = LoggerFactory.getLogger(BookController.class);
    private final BookRepository bookRepository;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }


    @GetMapping("/")
    public String showIndex() {
        return "redirect:/books";
    }

    @GetMapping("/books")
    public String showBookOverview(@RequestParam(required = false) String query,
                                   Model model) {
        List<Book> books = bookRepository.findAll();
        log.debug("Boekenoverzicht opgevraagd, {} boeken beschikbaar", books.size());

        List<Book> displayBooks;
        if (query != null && !query.isBlank()) {
            log.debug("Zoeken op query: {}", query);
            displayBooks = books.stream()
                    .filter(book -> book.getTitle()
                            .toLowerCase()
                            .contains(query.toLowerCase()))
                    .toList();
        } else {
            displayBooks = books;
        }

        model.addAttribute("paginaTitel", libraryName);
        model.addAttribute("allBooks", displayBooks);

        return "books";
    }

    @GetMapping("/book/{id}")
    public String showBookDetail(
            @PathVariable Long id, Model model) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isEmpty()) {
            return "redirect:/books";
        }
        model.addAttribute("book", book.get());
        return "book-detail";
    }

    @GetMapping("/books/new")
    public String showCreateNewBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "add-edit-book";
    }

    @GetMapping({"/books/edit/{bookId}"})
    public String showEditForm(@PathVariable Long bookId, Model model, RedirectAttributes redirectAttributes) {
        log.info("Bewerkingsformulier geopend voor: {}", bookId);

        Optional<Book> bookToEdit = bookRepository.findById(bookId);

        if (bookToEdit.isEmpty()) {
            log.warn("Boek met id: {} is niet gevonden voor bewerking", bookId);
            redirectAttributes.addFlashAttribute("bookNotFoundForEditing",
                    String.format("Het boek met id: %d kon niet gevonden worden om te bewerken.", bookId));
            return "redirect:/books";
        }

        model.addAttribute("book", bookToEdit);
        return "add-edit-book";
    }

    @PostMapping("/books/save")
    public String saveBook(@Valid @ModelAttribute Book updatedBook,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        log.info("Boek opslaan: {}", updatedBook.getTitle());

        if (bindingResult.hasErrors()) {
            log.warn("Validatiefouten bij opslaan: {}",
                    bindingResult.getErrorCount());
            return "add-edit-book";
        }

        if (updatedBook.getCopies().isEmpty()) {
            updatedBook.getCopies().add(new Copy(updatedBook));
            updatedBook.getCopies().add(new Copy(updatedBook));
            updatedBook.getCopies().add(new Copy(updatedBook));
        }

        bookRepository.save(updatedBook);
        log.info("Nieuw boek toegevoegd: {}", updatedBook.getTitle());
        redirectAttributes.addFlashAttribute(
                "successMessage", "Boek succesvol opgeslagen!");
        return "redirect:/books";
    }

    @GetMapping("/books/delete/{bookId}")
    public String deleteBook(@PathVariable Long bookId,
                             RedirectAttributes redirectAttributes) {
        log.info("Verwijderen van boek: {}", bookId);

        bookRepository.deleteById(bookId);

        redirectAttributes.addFlashAttribute(
                "successMessage", "Boek succesvol verwijderd!");
        return "redirect:/books";
    }
}
