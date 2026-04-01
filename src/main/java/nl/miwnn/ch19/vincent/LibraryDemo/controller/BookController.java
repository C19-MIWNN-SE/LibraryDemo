package nl.miwnn.ch19.vincent.LibraryDemo.controller;

import jakarta.validation.Valid;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Book;
import nl.miwnn.ch19.vincent.LibraryDemo.service.AuthorService;
import nl.miwnn.ch19.vincent.LibraryDemo.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * @author Vincent Velthuizen
 * Handle requests regarding books
 */

@Controller
@RequestMapping("/book")
public class BookController {
    private static final Logger log = LoggerFactory.getLogger(BookController.class);

    private final AuthorService authorService;
    private final BookService bookService;

    public BookController(AuthorService authorService, BookService bookService) {
        this.authorService = authorService;
        this.bookService = bookService;
    }

    @GetMapping("/all")
    public String showBookOverview(@RequestParam(required = false) String query,
                                   Model model) {

        log.debug("Boekenoverzicht opgevraagd");

        List<Book> books;
        if (query != null && !query.isBlank()) {
            log.debug("Zoeken op query: {}", query);
            books = bookService.searchByTitle(query);
        } else {
            books = bookService.getAllBooks();
        }

        log.debug("toon {} boeken in boekenoverzicht", books.size());
        model.addAttribute("allBooks", books);
        model.addAttribute("activePage", "books");

        return "book-overview";
    }

    @GetMapping("/add")
    public String showCreateNewBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("allAuthors", authorService.getAllAuthors());
        return "book-form";
    }

    @GetMapping({"/edit/{title}"})
    public String showEditForm(@PathVariable String title, Model model) {
        log.info("Bewerkingsformulier geopend voor boek: {}", title);

        Book book = bookService.findByTitle(title);
        model.addAttribute("book", book);

        model.addAttribute("allAuthors", authorService.getAllAuthors());

        return "book-form";
    }

    @PostMapping("/save")
    public String saveBook(@Valid @ModelAttribute Book updatedBook,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            log.warn("Aantal validatiefouten bij opslaan: {}",
                    bindingResult.getErrorCount());

            model.addAttribute("allAuthors", authorService.getAllAuthors());
            return "book-form";
        }

        log.info("Boek opslaan: {}", updatedBook.getTitle());
        bookService.saveBook(updatedBook);
        redirectAttributes.addFlashAttribute(
                "successMessage", "Boek succesvol opgeslagen!");
        String redirectUrl = UriComponentsBuilder.fromPath("/book/detail/{title}")
                .buildAndExpand(updatedBook.getTitle()).toUriString();
        return "redirect:" + redirectUrl;
    }

    @PostMapping("/delete/{title}")
    public String deleteBook(@PathVariable String title,
                             RedirectAttributes redirectAttributes) {
        log.info("Verwijderen van boek: {}", title);
        bookService.deleteBook(title);
        redirectAttributes.addFlashAttribute(
                "successMessage", "Boek succesvol verwijderd!");
        return "redirect:/book/all";
    }

    @PostMapping("/add-copy/{title}")
    public String addCopyToBook(@PathVariable String title,
                             RedirectAttributes redirectAttributes) {
        log.info("Voeg exemplaar toe van boek: {}", title);

        bookService.addCopyToBookWithTitle(title);

        redirectAttributes.addFlashAttribute(
                "successMessage", "Exemplaar succesvol toegevoegd!");
        String redirectUrl = UriComponentsBuilder.fromPath("/book/detail/{title}")
                .buildAndExpand(title).toUriString();
        return "redirect:" + redirectUrl;
    }

    @GetMapping({"/detail/{title}"})
    public String showBookDetail(@PathVariable String title, Model model,
                                 org.springframework.security.core.Authentication authentication) {
        Book book = bookService.findByTitle(title);
        model.addAttribute("book", book);

        boolean borrowDisabled = authentication != null
                && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
                && authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_USER"));
        model.addAttribute("borrowDisabled", borrowDisabled);

        return "book-detail";
    }
}
