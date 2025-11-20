package com.eyecommer.Backend.utils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnumPatternValidator implements ConstraintValidator<EnumPattern, Enum<?>> {
    private Pattern pattern;
    @Override
    public void initialize(EnumPattern EnumPattern) {
        try{
//        Pattern.compile(...) dùng để chuyển một chuỗi regex (String) thành một đối tượng Pattern có thể dùng để kiểm tra chuỗi.
            pattern = Pattern.compile(EnumPattern.regexp());
        }
        catch (Exception e){
            throw new IllegalArgumentException("Given regex is invalid", e);
        }
    }

    @Override
    public boolean isValid(Enum<?> s, ConstraintValidatorContext constraintValidatorContext) {
        if(s == null){
            return true;
        }
        Matcher m = pattern.matcher(s.name()); //m là đối tượng đại diện cho Matcher để so sánh linh hoạt với chuỗi
        return m.matches(); //m.matches() lúc này mới thực hiện phép so sánh
    }
}
