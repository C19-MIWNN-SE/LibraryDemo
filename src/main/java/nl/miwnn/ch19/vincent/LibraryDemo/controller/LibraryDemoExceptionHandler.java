package nl.miwnn.ch19.vincent.LibraryDemo.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author Vincent Velthuizen
 * Handles exceptions
 */
@ControllerAdvice
public class LibraryDemoExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleNotFound(
            EntityNotFoundException entityNotFoundException,
            Model model
    ) {
        model.addAttribute("message", entityNotFoundException.getMessage());
        return "error/404";
    }
}
