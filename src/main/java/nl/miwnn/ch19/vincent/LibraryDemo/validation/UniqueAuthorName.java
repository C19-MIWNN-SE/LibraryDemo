package nl.miwnn.ch19.vincent.LibraryDemo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Vincent Velthuizen
 * Validates that a Author's first name + last name combination is unique
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueAuthorNameValidator.class)
public @interface UniqueAuthorName {
    String message() default "Deze naam is al in gebruik";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}