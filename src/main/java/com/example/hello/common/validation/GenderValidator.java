package com.example.hello.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 性别值校验器
 */
public class GenderValidator implements ConstraintValidator<Gender, Integer> {

    @Override
    public void initialize(Gender constraintAnnotation) {
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // null值由@NotNull处理
        }
        return value == 1 || value == 2; // 1:男, 2:女
    }
}

