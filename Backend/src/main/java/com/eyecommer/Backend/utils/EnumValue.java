package com.eyecommer.Backend.utils;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValueValidator.class )
public @interface EnumValue {
    String name();

    //truyền 1 class bất kì nào đó kế thừa từ Enum (Hiểu đơn giản thì class này cũng là enum)
    Class<? extends Enum<?>> enumClass();
    String message() default "{name} must be any of enum {enumClass}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
