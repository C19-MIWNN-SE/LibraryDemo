package nl.miwnn.ch19.vincent.LibraryDemo.controller;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Vincent Velthuizen
 * Handles exceptions
 */
@ControllerAdvice
public class LibraryDemoExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(LibraryDemoExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(
            EntityNotFoundException entityNotFoundException,
            Model model
    ) {
        log.warn("Entity not found, exception message: {}", entityNotFoundException.getMessage());
        model.addAttribute("message", entityNotFoundException.getMessage());
        return "error/404";
    }
}
