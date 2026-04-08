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
        model.addAttribute("formAuthor", new Author());
        model.addAttribute("activePage", "authors");
        return "author-overview";
    }

    @GetMapping("/detail/{lastName}/{firstName}")
    public String showAuthorDetail(@PathVariable String lastName, @PathVariable String firstName, Model model) {
        Author author = authorService.findByLastNameAndFirstName(lastName, firstName);
        model.addAttribute("author", author);
        model.addAttribute("formAuthor", author);
        return "author-detail";
    }

    @GetMapping("/add")
    public String showAddAuthorForm(Model model) {
        if (!model.containsAttribute("formAuthor")) {
            model.addAttribute("formAuthor", new Author());
        }
        return "author-form";
    }

    @GetMapping("/edit/{lastName}/{firstName}")
    public String showEditAuthorForm(@PathVariable String lastName, @PathVariable String firstName, Model model) {
        model.addAttribute("formAuthor", authorService.findByLastNameAndFirstName(lastName, firstName));
        return "author-form";
    }

    @PostMapping("/delete/{id}")
    public String deleteAuthor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.info("Verwijderen van auteur met id: {}", id);
        authorService.deleteAuthor(id);
        redirectAttributes.addFlashAttribute("successMessage", "Auteur succesvol verwijderd!");
        return "redirect:/author/all";
    }

    @PostMapping("/save")
    public String saveOrUpdateAuthor(@Valid @ModelAttribute Author formAuthor,
                                     BindingResult bindingResult,
                                     RedirectAttributes redirectAttributes,
                                     @RequestParam("imageFile") MultipartFile imageFile,
                                     @RequestParam(value = "deleteImage", defaultValue = "false") boolean deleteImage) throws IOException {
        log.info("Auteur opslaan: {}", formAuthor.getFullName());

        if (bindingResult.hasErrors()) {
            log.warn("Validatiefouten bij opslaan: {}", bindingResult.getErrorCount());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.formAuthor", bindingResult);
            redirectAttributes.addFlashAttribute("formAuthor", formAuthor);
            return "redirect:/author/add";
        }

        if (formAuthor.getId() != null) {
            Author existingAuthor = authorService.findById(formAuthor.getId());
            existingAuthor.setFirstName(formAuthor.getFirstName());
            existingAuthor.setLastName(formAuthor.getLastName());

            if (!imageFile.isEmpty()) {
                Image image = new Image();
                image.setData(imageFile.getBytes());
                image.setContentType(imageFile.getContentType());
                imageRepository.save(image);
                existingAuthor.setImage(image);
            } else if (deleteImage) {
                existingAuthor.setImage(null);
            }

            authorService.saveAuthor(existingAuthor);
            log.info("Auteur bijgewerkt: {}", existingAuthor.getFullName());
            String redirectUrl = UriComponentsBuilder.fromPath("/author/detail/{lastName}/{firstName}")
                    .buildAndExpand(existingAuthor.getLastName(), existingAuthor.getFirstName()).toUriString();
            return "redirect:" + redirectUrl;
        }

        if (!imageFile.isEmpty()) {
            Image image = new Image();
            image.setData(imageFile.getBytes());
            image.setContentType(imageFile.getContentType());
            imageRepository.save(image);
            formAuthor.setImage(image);
        }

        authorService.saveAuthor(formAuthor);
        log.info("Auteur opgeslagen: {}", formAuthor.getFullName());
        redirectAttributes.addFlashAttribute("successMessage", "Auteur succesvol opgeslagen!");
        return "redirect:/author/all";
    }
}