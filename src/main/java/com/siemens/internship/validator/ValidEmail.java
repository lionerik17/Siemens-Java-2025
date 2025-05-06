package com.siemens.internship.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom annotation for validating email addresses
 */
@Documented
@Constraint(validatedBy = EmailValidator.class) // check EmailValidator class for implementation
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEmail {
    /**
     * Default validation message
     * @return "Invalid email format" if a string does not respect an email address format
     */
    String message() default "Invalid email format";

    /**
     * Used to separate validations by context (for example, create and update)
     * @return validation groups
     */

    Class<?>[] groups() default {};

    /**
     * Get extra information from this constraint
     * @return metadata
     */
    Class<? extends Payload>[] payload() default {};
}
