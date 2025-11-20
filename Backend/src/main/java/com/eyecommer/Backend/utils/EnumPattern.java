package com.eyecommer.Backend.utils;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy =EnumPatternValidator.class )
public @interface EnumPattern {
    String message() default "{name} must match {regexp}";
    String name();
    String regexp();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
