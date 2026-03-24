package nl.miwnn.ch19.vincent.LibraryDemo.controller;

import nl.miwnn.ch19.vincent.LibraryDemo.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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
    public String showBookOverview(Model model) {
        log.debug("Boekenoverzicht opgevraagd, {} boeken beschikbaar", books.size());
        model.addAttribute("paginaTitel", "Overzicht van onze boeken");
        model.addAttribute("allBooks", books);

        return "books";
    }

    @GetMapping("/books/add")
    public String showAddBookForm(Model model) {
        log.debug("Formulier voor nieuw boek opgevraagd");
        model.addAttribute("book", new Book());
        return "add-book";
    }

    @PostMapping("/books/add")
    public String processAddBook(@ModelAttribute Book book) {
        log.info("Nieuw boek toegevoegd: {}", book.getTitle());
        books.add(book);
        return "redirect:/books";
    }

}
