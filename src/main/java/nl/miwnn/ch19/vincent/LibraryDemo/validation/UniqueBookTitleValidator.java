package nl.miwnn.ch19.vincent.LibraryDemo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Book;
import nl.miwnn.ch19.vincent.LibraryDemo.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

/**
 * @author Vincent Velthuizen
 * Checks that no other book already uses the same title
 */
public class UniqueBookTitleValidator implements ConstraintValidator<UniqueBookTitle, Book> {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public boolean isValid(Book book, ConstraintValidatorContext context) {
        if (book.getTitle() == null || book.getTitle().isBlank()) {
            return true; // @NotBlank handles this separately
        }

        Optional<Book> existing = bookRepository.findBookByTitle(book.getTitle());
        if (existing.isEmpty()) {
            return true;
        }

        boolean isOwnTitle = existing.get().getId().equals(book.getId());
        if (!isOwnTitle) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("title")
                    .addConstraintViolation();
        }
        return isOwnTitle;
    }
}