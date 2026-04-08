package nl.miwnn.ch19.vincent.LibraryDemo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import nl.miwnn.ch19.vincent.LibraryDemo.model.Genre;
import nl.miwnn.ch19.vincent.LibraryDemo.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

/**
 * @author Vincent Velthuizen
 * Checks that no other genre already uses the same shortName
 */
public class UniqueGenreShortNameValidator implements ConstraintValidator<UniqueGenreShortName, Genre> {

    @Autowired
    private GenreRepository genreRepository;

    @Override
    public boolean isValid(Genre genre, ConstraintValidatorContext context) {
        if (genre.getShortName() == null || genre.getShortName().isBlank()) {
            return true; // @NotBlank handles this separately
        }

        Optional<Genre> existing = genreRepository.findByShortNameIgnoreCase(genre.getShortName());
        if (existing.isEmpty()) {
            return true;
        }

        boolean isOwnShortName = existing.get().getId().equals(genre.getId());
        if (!isOwnShortName) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("shortName")
                    .addConstraintViolation();
        }
        return isOwnShortName;
    }
}