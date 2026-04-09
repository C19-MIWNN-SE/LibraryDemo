package nl.miwnn.ch19.vincent.LibraryDemo.controller;

import nl.miwnn.ch19.vincent.LibraryDemo.model.LibraryUser;
import nl.miwnn.ch19.vincent.LibraryDemo.service.CopyService;
import nl.miwnn.ch19.vincent.LibraryDemo.service.LoanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Vincent Velthuizen
 * Handle all requests regarding Loans
 */
@Controller
@RequestMapping("/loan")
public class LoanController {
    private static final Logger log = LoggerFactory.getLogger(LoanController.class);

    private final CopyService copyService;
    private final LoanService loanService;

    public LoanController(CopyService copyService, LoanService loanService) {
        this.copyService = copyService;
        this.loanService = loanService;
    }

    @PostMapping("/borrow/{copyId}")
    public String borrowCopy(@PathVariable Long copyId,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        String bookTitle = copyService.findById(copyId).getBook().getTitle();
        String redirectUrl = UriComponentsBuilder.fromPath("/book/detail/{title}")
                .buildAndExpand(bookTitle)
                .toUriString();
        LibraryUser borrower = (LibraryUser) authentication.getPrincipal();
        log.info("Uitleenverzoek: exemplaar {} door gebruiker {}", copyId, borrower.getUsername());
        try {
            copyService.borrowCopy(copyId, borrower);
            redirectAttributes.addFlashAttribute("successMessage", "Boek succesvol geleend.");
        } catch (IllegalStateException e) {
            log.warn("Uitlenen mislukt voor exemplaar {}: {}", copyId, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:" + redirectUrl;
    }

    @GetMapping("/all")
    public String showLoanOverview(Model model) {
        model.addAttribute("activeLoans", loanService.getActiveLoans());
        model.addAttribute("activePage", "loans");
        return "loan-overview";
    }

    @PostMapping("/return/{copyId}")
    public String returnCopy(@PathVariable Long copyId,
                             @RequestHeader(value = "Referer",
                                     defaultValue = "/book/all") String referer,
                             RedirectAttributes redirectAttributes) {
        log.info("Terugbrengverzoek: exemplaar {}", copyId);
        try {
            copyService.returnCopy(copyId);
            redirectAttributes.addFlashAttribute("successMessage", "Boek succesvol teruggebracht.");
        } catch (IllegalStateException e) {
            log.warn("Terugbrengen mislukt voor exemplaar {}: {}", copyId, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:" + referer;
    }
}
