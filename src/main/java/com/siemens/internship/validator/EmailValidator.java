package com.siemens.internship.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<ValidEmail, String> {
    // ^ ... $ - begin and end sequence
    // [A-Za-z0-9]+ - first part from an email which has 0 or more alphanumerics, before '@'
    // (?:[._%+-][A-Za-z0-9]+)* - allows a single special character, followed by 0 or more alphanumerics
    // '@' - must have exactly one
    // [A-Za-z0-9]+ - second part from an email which has 0 or more alphanumerics, after '@' and before .)
    // (?:[._%+-][A-Za-z0-9]+)* - allows a single special character, followed by 0 or more alphanumerics
    // (?:\.[A-Za-z]{2,})+ - at least two characters after '.' and allows subdomains (e.g ab.cd, ab.cd.ef)

    private static final String EMAIL_REGEX =
            "^[A-Za-z0-9]+(?:[._%+-][A-Za-z0-9]+)*@" +
                    "[A-Za-z0-9]+(?:-[A-Za-z0-9]+)*(?:\\.[A-Za-z]{2,})+$";

    /**
     * Validates a given string if it is an email address.
     * @param email the given string
     * @return true if valid, otherwise false
     */
    public boolean isValid(String email, ConstraintValidatorContext context) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        return email != null && pattern.matcher(email).matches();
    }
}
