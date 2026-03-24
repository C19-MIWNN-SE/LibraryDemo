package nl.miwnn.ch19.vincent.LibraryDemo.controller;

import nl.miwnn.ch19.vincent.LibraryDemo.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
        return "index";
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

    @GetMapping("/books/add")
    public String showAddBookForm(Model model) {
        log.debug("Formulier voor nieuw boek opgevraagd");
        model.addAttribute("book", new Book());
        return "add-book";
    }

    @GetMapping("/books/edit/{title}")
    public String showEditForm(@PathVariable String title, Model model) {
        log.info("Bewerkingsformulier geopend voor: {}", title);

        Book bookToEdit = books.stream()
                .filter(book -> book.getTitle().equals(title))
                .findFirst()
                .orElse(null);

        if (bookToEdit == null) {
            log.warn("Boek niet gevonden voor bewerken: {}", title);
            return "redirect:/books";
        }

        model.addAttribute("book", bookToEdit);
        return "add-edit-book";
    }

    @PostMapping("/books/save")
    public String saveBook(@ModelAttribute Book updatedBook, RedirectAttributes redirectAttributes) {
        log.info("Boek opslaan: {}", updatedBook.getTitle());
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
        log.debug("Nieuw boek toegevoegd: {}", updatedBook.getTitle());
        redirectAttributes.addFlashAttribute(
                "successMessage", "Boek succesvol toegevoegd!");
        return "redirect:/books";
    }

    @PostMapping("/books/add")
    public String processAddBook(@ModelAttribute Book book) {
        log.info("Nieuw boek toegevoegd: {}", book.getTitle());
        books.add(book);
        return "redirect:/books";
    }

    @GetMapping("/books/delete/{title}")
    public String deleteBook(@PathVariable String title) {
        log.info("Verwijderen van boek: {}", title);
        books.removeIf(book -> book.getTitle().equals(title));
        return "redirect:/books";
    }

}
