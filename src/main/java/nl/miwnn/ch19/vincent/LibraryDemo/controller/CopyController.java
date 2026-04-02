package nl.miwnn.ch19.vincent.LibraryDemo.controller;

import nl.miwnn.ch19.vincent.LibraryDemo.model.Book;
import nl.miwnn.ch19.vincent.LibraryDemo.model.LibraryUser;
import nl.miwnn.ch19.vincent.LibraryDemo.service.CopyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Vincent Velthuizen
 * Handle all requests regarding copies
 */
@Controller
@RequestMapping("/copies")
public class CopyController {

    private static final Logger log = LoggerFactory.getLogger(CopyController.class);
    private final CopyService copyService;

    public CopyController(CopyService copyService) {
        this.copyService = copyService;
    }

    @PostMapping("/borrow/{copyId}")
    public String borrowCopy(@PathVariable Long copyId,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        Book book = copyService.findById(copyId).getBook();
        String redirectUrl = UriComponentsBuilder.fromPath("/book/detail/{title}")
                .buildAndExpand(book.getTitle()).toUriString();
        try {
            LibraryUser currentUser = (LibraryUser) authentication.getPrincipal();

            copyService.borrowCopy(copyId, currentUser);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Boek succesvol geleend.");
        } catch (IllegalStateException illegalStateException) {
            redirectAttributes.addFlashAttribute("errorMessage", illegalStateException.getMessage());
        }
        return "redirect:" + redirectUrl;
    }

    @PostMapping("/return/{copyId}")
    public String returnCopy(@PathVariable Long copyId, RedirectAttributes redirectAttributes) {
        Book book = copyService.findById(copyId).getBook();
        String redirectUrl = UriComponentsBuilder.fromPath("/book/detail/{title}")
                .buildAndExpand(book.getTitle()).toUriString();
        try {
            copyService.returnCopy(copyId);
            redirectAttributes.addFlashAttribute("successMessage", "Boek succesvol teruggebracht.");
        } catch (IllegalStateException illegalStateException) {
            redirectAttributes.addFlashAttribute("errorMessage", illegalStateException.getMessage());
        }
        return "redirect:" + redirectUrl;
    }
}
