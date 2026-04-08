package nl.miwnn.ch19.vincent.LibraryDemo.controller;

import jakarta.validation.Valid;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Genre;
import nl.miwnn.ch19.vincent.LibraryDemo.service.BookService;
import nl.miwnn.ch19.vincent.LibraryDemo.service.GenreService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Vincent Velthuizen
 * Handle all requests regarding Genres
 */
@Controller
@RequestMapping("/genre")
public class GenreController {
    private final GenreService genreService;
    private final BookService bookService;

    public GenreController(GenreService genreService, BookService bookService) {
        this.genreService = genreService;
        this.bookService = bookService;
    }

    @GetMapping({"/", "/all"})
    public String showGenreOverview(Model model) {
        model.addAttribute("allGenres", genreService.getAllGenres());
        model.addAttribute("activePage", "genres");
        return "genre-overview";
    }

    @GetMapping("/{shortName}")
    public String showGenreDetail(@PathVariable String shortName, Model model) {
        Genre genre = genreService.findByShortName(shortName);
        model.addAttribute("genre", genre);
        model.addAttribute("activePage", "genres");
        return "genre-detail";
    }

    @PostMapping("/delete/{id}")
    public String deleteGenre(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        genreService.deleteGenre(id);
        redirectAttributes.addFlashAttribute("successMessage", "Genre succesvol verwijderd!");
        return "redirect:/genre/all";
    }

    @PostMapping("/save")
    public String saveGenre(@Valid @ModelAttribute Genre formGenre,
                            BindingResult bindingResult,
                            @RequestParam(required = false) Long exampleBookId,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            if (formGenre.getId() != null) {
                String detailUrl = UriComponentsBuilder.fromPath("/genre/{shortName}")
                        .buildAndExpand(formGenre.getShortName()).toUriString();
                return "redirect:" + detailUrl;
            }
            return "redirect:/genre/all";
        }

        Genre genre = formGenre.getId() != null ? genreService.findById(formGenre.getId()) : new Genre();
        genre.setName(formGenre.getName());
        genre.setShortName(formGenre.getShortName());
        if (exampleBookId != null) {
            genre.setExampleBook(bookService.findById(exampleBookId));
        }
        genreService.saveGenre(genre);
        redirectAttributes.addFlashAttribute("successMessage", "Genre succesvol opgeslagen!");
        String redirectUrl = UriComponentsBuilder.fromPath("/genre/{shortName}")
                .buildAndExpand(formGenre.getShortName()).toUriString();
        return "redirect:" + redirectUrl;
    }
}