package nl.miwnn.ch19.vincent.LibraryDemo.controller;

import jakarta.validation.Valid;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vincent Velthuizen
 * Handle requests regarding books
 */

@Controller
public class BookController {

    private static final Logger log = LoggerFactory.getLogger(BookController.class);

    private final List<Book> books = new ArrayList<>();

    public BookController() {
        books.add(new Book("De ontdekking van de hemel",
                "Harry Mulisch", 1992));
        books.add(new Book("Het Bureau",
                "J.J. Voskuil", 1996));
        books.add(new Book("Turks fruit",
                "Jan Wolkers", 1969));
    }

    @GetMapping("/")
    public String showIndex() {
        return "redirect:/books";
    }

    @GetMapping("/books")
    public String showBookOverview(@RequestParam(required = false) String query,
                                   Model model) {
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

        model.addAttribute("paginaTitel", "Overzicht van onze boeken");
        model.addAttribute("allBooks", displayBooks);

        return "books";
    }

    @GetMapping({"/books/edit", "/books/edit/{title}"})
    public String showEditForm(@PathVariable(required = false) String title, Model model) {
        log.info("Bewerkingsformulier geopend voor: {}", title);

        Book bookToEdit = books.stream()
                .filter(book -> book.getTitle().equals(title))
                .findFirst()
                .orElse(new Book());

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

        // Zoek het boek in de lijst en vervang het
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getTitle().equals(updatedBook.getTitle())) {
                books.set(i, updatedBook);
                log.debug("Bestaand boek bijgewerkt op index {}", i);
                return "redirect:/books";
            }
        }
        // Niet gevonden: voeg toe als nieuw boek
        books.add(updatedBook);
        log.info("Nieuw boek toegevoegd: {}", updatedBook.getTitle());
        redirectAttributes.addFlashAttribute(
                "successMessage", "Boek succesvol opgeslagen!");
        return "redirect:/books";
    }

    @GetMapping("/books/delete/{title}")
    public String deleteBook(@PathVariable String title,
                             RedirectAttributes redirectAttributes) {
        log.info("Verwijderen van boek: {}", title);
        books.removeIf(book -> book.getTitle().equals(title));

        redirectAttributes.addFlashAttribute(
                "successMessage", "Boek succesvol verwijderd!");
        return "redirect:/books";
    }
}
