package nl.miwnn.ch19.vincent.LibraryDemo.controller;

import nl.miwnn.ch19.vincent.LibraryDemo.model.Book;
import nl.miwnn.ch19.vincent.LibraryDemo.service.CopyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String borrowCopy(@PathVariable("copyId") Long copyId, RedirectAttributes redirectAttributes) {
        try {
            Book book = copyService.borrowCopy(copyId).getBook();
            redirectAttributes.addFlashAttribute("successMessage",
                    "Boek succesvol geleend.");
            return "redirect:/book/detail/" + book.getTitle();
        } catch (IllegalStateException illegalStateException) {
            redirectAttributes.addFlashAttribute("errorMessage", illegalStateException.getMessage());
        }
        return "redirect:/book/all";
    }

    @PostMapping("/return/{copyId}")
    public String returnCopy(@PathVariable("copyId") Long copyId, RedirectAttributes redirectAttributes) {
        try {
            Book book = copyService.returnCopy(copyId).getBook();
            redirectAttributes.addFlashAttribute("successMessage",
                    "Boek succesvol teruggebracht.");
            return "redirect:/book/detail/" + book.getTitle();
        } catch (IllegalStateException illegalStateException) {
            redirectAttributes.addFlashAttribute("errorMessage", illegalStateException.getMessage());
        }
        return "redirect:/book/all";
    }
}
