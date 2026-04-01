package nl.miwnn.ch19.vincent.LibraryDemo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Vincent Velthuizen
 * Validates that a Book's title is unique across all books
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueBookTitleValidator.class)
public @interface UniqueBookTitle {
    String message() default "Deze titel is al in gebruik";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}