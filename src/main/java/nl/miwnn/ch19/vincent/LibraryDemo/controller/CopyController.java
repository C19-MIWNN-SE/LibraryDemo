package nl.miwnn.ch19.vincent.LibraryDemo.controller;

import nl.miwnn.ch19.vincent.LibraryDemo.model.Copy;
import nl.miwnn.ch19.vincent.LibraryDemo.service.CopyService;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

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
            copyService.borrowCopy(copyId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Boek succesvol geleend.");
        } catch (IllegalStateException illegalStateException) {
            redirectAttributes.addFlashAttribute("errorMessage", illegalStateException.getMessage());
        }
        return "redirect:/book/all";
    }

    @PostMapping("/return/{copyId}")
    public String returnCopy(@PathVariable("copyId") Long copyId, RedirectAttributes redirectAttributes) {
        try {
            copyService.returnCopy(copyId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Boek succesvol teruggebracht.");
        } catch (IllegalStateException illegalStateException) {
            redirectAttributes.addFlashAttribute("errorMessage", illegalStateException.getMessage());
        }
        return "redirect:/book/all";
    }
}
