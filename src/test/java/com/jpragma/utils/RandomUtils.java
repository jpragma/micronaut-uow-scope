package com.jpragma.utils;

import java.util.Random;

public class RandomUtils {
    public static String randomAlphanumeric(int length) {
        int leftLimitAscii = 48; // numeric '0'
        int rightLimitAscii = 122; // letter 'z'
        Random random = new Random();

        return random.ints(leftLimitAscii, rightLimitAscii + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
