package nl.miwnn.ch19.vincent.LibraryDemo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Author;
import nl.miwnn.ch19.vincent.LibraryDemo.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

/**
 * @author Vincent Velthuizen
 * Checks that no other author already has the same first name + last name combination
 */
public class UniqueAuthorNameValidator implements ConstraintValidator<UniqueAuthorName, Author> {

    @Autowired
    private AuthorRepository authorRepository;

    @Override
    public boolean isValid(Author author, ConstraintValidatorContext context) {
        if (author.getFirstName() == null || author.getLastName() == null) {
            return true; // @NotBlank handles blank fields separately
        }

        Optional<Author> existing = authorRepository.findByLastNameAndFirstName(
                author.getLastName(), author.getFirstName());
        if (existing.isEmpty()) {
            return true;
        }

        boolean isOwnName = existing.get().getId().equals(author.getId());
        if (!isOwnName) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("lastName")
                    .addConstraintViolation();
        }
        return isOwnName;
    }
}