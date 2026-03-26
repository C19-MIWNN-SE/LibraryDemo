package nl.miwnn.ch19.vincent.LibraryDemo.controller;

import nl.miwnn.ch19.vincent.LibraryDemo.model.Copy;
import nl.miwnn.ch19.vincent.LibraryDemo.repository.CopyRepository;
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
    private final CopyRepository copyRepository;

    public CopyController(CopyRepository copyRepository) {
        this.copyRepository = copyRepository;
    }

    @PostMapping("/borrow/{copyId}")
    public String borrowCopy(@PathVariable("copyId") Long copyId, RedirectAttributes redirectAttributes) {
        return handleCopyStateChange(copyId, redirectAttributes, false, "borrow");
    }

    @PostMapping("/return/{copyId}")
    public String returnCopy(@PathVariable("copyId") Long copyId, RedirectAttributes redirectAttributes) {
        return handleCopyStateChange(copyId, redirectAttributes, true, "return");
    }

    private @NonNull String handleCopyStateChange(
            Long copyId,
            RedirectAttributes redirectAttributes,
            boolean newState,
            String stateName) {
        Optional<Copy> optionalCopy = copyRepository.findById(copyId);

        if (optionalCopy.isPresent() && optionalCopy.get().getAvailable() != newState) {
            Copy copy = optionalCopy.get();

            copy.setAvailable(newState);
            copyRepository.save(copy);

            log.info("Copy with id: {} for book with title: {} was just {}ed", copyId, copy.getBook().getTitle(), stateName);
            redirectAttributes.addFlashAttribute(
                    "copyStatechanged",
                    String.format("Exemplaar met id: %d voor boek met titel: %s is zojuist %sed",
                            copyId, copy.getBook().getTitle(), stateName));

            return "redirect:/book/detail/" + copy.getBook().getTitle();
        }

        log.warn("Copy with id: {} could not be {}ed, because it was not found or it was already {}}", copyId, stateName, stateName);
        redirectAttributes.addFlashAttribute("succesMessage", "Exemplaar is geleend of teruggebracht");

        return "redirect:/book/all";
    }
}
