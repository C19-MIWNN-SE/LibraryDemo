package nl.miwnn.ch19.vincent.LibraryDemo.controller;

import jakarta.validation.Valid;
import nl.miwnn.ch19.vincent.LibraryDemo.dto.ChangePasswordDTO;
import nl.miwnn.ch19.vincent.LibraryDemo.dto.NewLibraryUserDTO;
import nl.miwnn.ch19.vincent.LibraryDemo.model.LibraryUser;
import nl.miwnn.ch19.vincent.LibraryDemo.service.LibraryUserService;
import nl.miwnn.ch19.vincent.LibraryDemo.service.LoanService;
import nl.miwnn.ch19.vincent.LibraryDemo.service.mapper.LibraryUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author Vincent Velthuizen
 * Handle all HTTP requests regarding LibaryUsers
 */
@Controller
@RequestMapping("/user")
public class LibraryUserController {
    private static final Logger log = LoggerFactory.getLogger(LibraryUserController.class);

    private final LibraryUserService libraryUserService;
    private final LoanService loanService;
    private final PasswordEncoder passwordEncoder;

    public LibraryUserController(
            LibraryUserService libraryUserService,
            LoanService loanService,
            PasswordEncoder passwordEncoder) {
        this.libraryUserService = libraryUserService;
        this.loanService = loanService;
        this.passwordEncoder = passwordEncoder;
    }
    @GetMapping("/home")
    public String showUserHome(@AuthenticationPrincipal LibraryUser currentUser, Model model) {
        log.debug("Gebruikerspagina opgevraagd voor: {}", currentUser.getUsername());
        LibraryUser libraryUser = (LibraryUser) libraryUserService.loadUserByUsername(currentUser.getUsername());
        model.addAttribute("activeLoans", loanService.getActiveLoansForUser(libraryUser));
        model.addAttribute("recentLoans", loanService.getRecentlyClosedLoansForUser(libraryUser));
        return "user-home";
    }

    @GetMapping("/all")
    public String showUserOverview(Model model) {
        log.debug("Gebruikersoverzicht opgevraagd");
        model.addAttribute("users", libraryUserService.getAllUsers());
        return "user-overview";
    }

    @GetMapping("/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("newUser", new NewLibraryUserDTO());
        return "user-form";
    }

    @PostMapping("/add")
    public String addUser(
            @Valid @ModelAttribute("newUser") NewLibraryUserDTO dto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (!dto.getPlainPassword().equals(dto.getCheckPassword())) {
            bindingResult
                    .rejectValue("plainPassword", "unequal", "Passwords should match.");
        }

        if (bindingResult.hasErrors()) {
            log.warn("Validatiefouten bij aanmaken gebruiker: {}", bindingResult.getErrorCount());
            return "users/add-user";
        }

        log.info("Nieuwe gebruiker aanmaken: {}", dto.getUsername());
        libraryUserService.saveNewUser(dto);
        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Gebruiker '" + dto.getUsername() + "' aangemaakt.");
        return "redirect:/user/all";
    }
    @PostMapping("/delete/{id}")
    public String deleteUser(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        log.info("Gebruiker verwijderen met id: {}", id);
        libraryUserService.deleteById(id);
        redirectAttributes.addFlashAttribute(
                "successMessage", "Gebruiker verwijderd.");
        return "redirect:/user/all";
    }

    @PostMapping("/reset-password/{id}")
    public String resetPassword(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        log.info("Wachtwoord resetten voor gebruiker met id: {}", id);
        String newPassword = libraryUserService.resetPassword(id);
        redirectAttributes.addFlashAttribute(
                "successMessage", "Wachtwoord gereset. Nieuw wachtwoord: " + newPassword);
        return "redirect:/user/all";
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm(Model model) {
        model.addAttribute("changePassword", new ChangePasswordDTO());
        return "change-password-form";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @ModelAttribute("changePassword") ChangePasswordDTO dto,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails currentUser,
            RedirectAttributes redirectAttributes) {

        if (!dto.getNewPassword().equals(dto.getCheckPassword())) {
            bindingResult.rejectValue("checkPassword", "unequal", "Wachtwoorden komen niet overeen.");
        }

        if (bindingResult.hasErrors()) {
            log.warn("Validatiefouten bij wijzigen wachtwoord voor gebruiker: {}", currentUser.getUsername());
            return "change-password-form";
        }

        log.info("Wachtwoord wijzigen voor gebruiker: {}", currentUser.getUsername());
        boolean success = libraryUserService.changePassword(currentUser.getUsername(), dto);
        if (!success) {
            log.warn("Onjuist huidig wachtwoord opgegeven door gebruiker: {}", currentUser.getUsername());
            bindingResult.rejectValue("currentPassword", "incorrect", "Huidig wachtwoord is onjuist.");
            return "change-password-form";
        }

        log.info("Wachtwoord succesvol gewijzigd voor gebruiker: {}", currentUser.getUsername());
        redirectAttributes.addFlashAttribute("successMessage", "Wachtwoord succesvol gewijzigd.");
        return "redirect:/book/all";
    }
}
