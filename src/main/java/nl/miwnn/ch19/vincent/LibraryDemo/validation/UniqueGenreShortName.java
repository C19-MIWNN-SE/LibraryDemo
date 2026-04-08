package nl.miwnn.ch19.vincent.LibraryDemo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Vincent Velthuizen
 * Validates that a Genre's shortName is unique across all genres
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueGenreShortNameValidator.class)
public @interface UniqueGenreShortName {
    String message() default "Deze afkorting is al in gebruik";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}