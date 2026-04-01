package nl.miwnn.ch19.vincent.LibraryDemo.controller;

import jakarta.validation.Valid;
import nl.miwnn.ch19.vincent.LibraryDemo.dto.NewLibraryUserDTO;
import nl.miwnn.ch19.vincent.LibraryDemo.repository.UserRepository;
import nl.miwnn.ch19.vincent.LibraryDemo.service.LibraryUserService;
import nl.miwnn.ch19.vincent.LibraryDemo.service.mapper.LibraryUserMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private final LibraryUserService libraryUserService;
    private final PasswordEncoder passwordEncoder;

    public LibraryUserController(
            LibraryUserService libraryUserService,
            PasswordEncoder passwordEncoder) {
        this.libraryUserService = libraryUserService;
        this.passwordEncoder = passwordEncoder;
    }
    @GetMapping("/all")
    public String showUserOverview(Model model) {
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
            return "users/add-user";
        }

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
        libraryUserService.deleteById(id);
        redirectAttributes.addFlashAttribute(
                "successMessage", "Gebruiker verwijderd.");
        return "redirect:/user/all";
    }
}
