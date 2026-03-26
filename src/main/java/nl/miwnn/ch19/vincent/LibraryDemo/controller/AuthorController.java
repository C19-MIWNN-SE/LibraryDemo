package nl.miwnn.ch19.vincent.LibraryDemo.controller;

import jakarta.validation.Valid;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Author;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Copy;
import nl.miwnn.ch19.vincent.LibraryDemo.repository.AuthorRepository;
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
    private final AuthorRepository authorRepository;

    public AuthorController(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }


    @GetMapping("/all")
    public String showAuthorOverviewWithForm(Model model) {
        model.addAttribute("allAuthors", authorRepository.findAll());
        model.addAttribute("newAuthor", new Author());

        return "author-overview";
    }

    @PostMapping("/save")
    public String saveOrUpdateAuthor(@Valid @ModelAttribute Author author,
                                     BindingResult bindingResult,
                                     RedirectAttributes redirectAttributes) {
        log.info("Auteur opslaan: {}", author.getFullName());

        if (bindingResult.hasErrors()) {
            log.warn("Validatiefouten bij opslaan: {}",
                    bindingResult.getErrorCount());
            return "author-overview";
        }

        authorRepository.save(author);
        log.info("Nieuw boek toegevoegd: {}", author.getFullName());
        redirectAttributes.addFlashAttribute(
                "successMessage", "Auteur succesvol opgeslagen!");
        return "redirect:/author/all";
    }


}
