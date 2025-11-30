package com.eyecommer.Backend.utils;

import java.security.SecureRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GenerateCodeRandom {
    // Sử dụng chữ cái in hoa và số
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Tạo một chuỗi ngẫu nhiên với độ dài chỉ định.
     * @param length Độ dài của chuỗi ngẫu nhiên.
     * @return Chuỗi ngẫu nhiên.
     */
    private static String generateRandomString(int length) {
        return IntStream.range(0, length)
                .mapToObj(i -> CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())))
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    /**
     * Tạo mã voucher theo format VOUCHER-XX-XXX.
     * @return Mã voucher theo format quy định.
     */
    public static String generateCustomCode(String s) {
        String segment1 = generateRandomString(2); // Hai ký tự ngẫu nhiên
        String segment2 = generateRandomString(3); // Ba ký tự ngẫu nhiên

        return s + "-" + segment1 + "-" + segment2;
    }
}
