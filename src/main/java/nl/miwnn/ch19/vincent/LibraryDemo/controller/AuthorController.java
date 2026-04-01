package nl.miwnn.ch19.vincent.LibraryDemo.controller;

import jakarta.validation.Valid;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Author;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Image;
import nl.miwnn.ch19.vincent.LibraryDemo.repository.ImageRepository;
import nl.miwnn.ch19.vincent.LibraryDemo.service.AuthorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

/**
 * @author Vincent Velthuizen
 * Handles requests regarding authors
 */
@Controller
@RequestMapping("/author")
public class AuthorController {

    private static final Logger log = LoggerFactory.getLogger(AuthorController.class);
    private final AuthorService authorService;
    private final ImageRepository imageRepository;

    public AuthorController(AuthorService authorService, ImageRepository imageRepository) {
        this.authorService = authorService;
        this.imageRepository = imageRepository;
    }

    @GetMapping("/all")
    public String showAuthorOverview(Model model) {
        model.addAttribute("allAuthors", authorService.getAllAuthors());
        model.addAttribute("activePage", "authors");
        return "author-overview";
    }

    @GetMapping("/detail/{lastName}/{firstName}")
    public String showAuthorDetail(@PathVariable String lastName, @PathVariable String firstName, Model model) {
        model.addAttribute("author", authorService.findByLastNameAndFirstName(lastName, firstName));
        return "author-detail";
    }

    @GetMapping("/add")
    public String showAddAuthorForm(Model model) {
        model.addAttribute("author", new Author());
        return "author-form";
    }

    @GetMapping("/edit/{lastName}/{firstName}")
    public String showEditAuthorForm(@PathVariable String lastName, @PathVariable String firstName, Model model) {
        model.addAttribute("author", authorService.findByLastNameAndFirstName(lastName, firstName));
        return "author-form";
    }

    @PostMapping("/save")
    public String saveOrUpdateAuthor(@Valid @ModelAttribute Author author,
                                     BindingResult bindingResult,
                                     Model model,
                                     RedirectAttributes redirectAttributes,
                                     @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        log.info("Auteur opslaan: {}", author.getFullName());

        if (bindingResult.hasErrors()) {
            log.warn("Validatiefouten bij opslaan: {}", bindingResult.getErrorCount());
            return "author-form";
        }

        if (!imageFile.isEmpty()) {
            Image image = new Image();
            image.setData(imageFile.getBytes());
            image.setContentType(imageFile.getContentType());
            imageRepository.save(image);
            author.setImage(image);
        }

        authorService.saveAuthor(author);
        log.info("Auteur opgeslagen: {}", author.getFullName());
        redirectAttributes.addFlashAttribute("successMessage", "Auteur succesvol opgeslagen!");

        if (author.getId() != null) {
            String redirectUrl = UriComponentsBuilder.fromPath("/author/detail/{lastName}/{firstName}")
                    .buildAndExpand(author.getLastName(), author.getFirstName()).toUriString();
            return "redirect:" + redirectUrl;
        }
        return "redirect:/author/all";
    }
}