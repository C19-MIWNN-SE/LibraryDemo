package nl.miwnn.ch19.vincent.LibraryDemo.controller;

import jakarta.validation.Valid;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Author;
import nl.miwnn.ch19.vincent.LibraryDemo.service.AuthorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author Vincent Velthuizen
 * Handles requests regarding authors
 */
@Controller
@RequestMapping("/author")
public class AuthorController {

    private static final Logger log = LoggerFactory.getLogger(AuthorController.class);
    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }


    @GetMapping("/all")
    public String showAuthorOverviewWithForm(Model model) {
        model.addAttribute("allAuthors", authorService.getAllAuthors());
        model.addAttribute("newAuthor", new Author());
        model.addAttribute("activePage", "authors");

        return "author-overview";
    }

    @PostMapping("/save")
    public String saveOrUpdateAuthor(@Valid @ModelAttribute Author author,
                                     BindingResult bindingResult,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        log.info("Auteur opslaan: {}", author.getFullName());

        if (bindingResult.hasErrors()) {
            log.warn("Validatiefouten bij opslaan: {}",
                    bindingResult.getErrorCount());
            model.addAttribute("allAuthors", authorService.getAllAuthors());
            return "author-overview";
        }

        authorService.saveAuthor(author);
        log.info("Nieuwe auteur toegevoegd: {}", author.getFullName());
        redirectAttributes.addFlashAttribute(
                "successMessage", "Auteur succesvol opgeslagen!");
        return "redirect:/author/all";
    }


}
