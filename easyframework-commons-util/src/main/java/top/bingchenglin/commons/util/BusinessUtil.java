package top.bingchenglin.commons.util;

import java.util.Random;
import java.util.UUID;

public class BusinessUtil {
    public static final String CHARS_NUMERIC = "0123456789";
    public static final String CHARS_LOWER = "abcdefghijklmnopqrstuvwxyz";
    public static final String CHARS_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String CHARS_SYMBOL = "!@#$^&*()-=_+,./;'[]\\<>?:\"{}|";
    public static final String CHARS_LETTER = CHARS_LOWER + CHARS_UPPER;//"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String CHARS_NUMERIC_LETTER = CHARS_NUMERIC + CHARS_LETTER;//"0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String CHARS_NUMERIC_LETTER_SYMBOL = CHARS_NUMERIC_LETTER + CHARS_SYMBOL;//"0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String generateSerialNumber() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String generateRandomCode(int length) {
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            builder.append(random.nextInt(10));
        }
        return builder.toString();
    }

    public static String generateRandomCode(int length, String chars) {
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            builder.append(chars.charAt(random.nextInt(chars.length())));
        }
        return builder.toString();
    }

    public static String generateRandomPassword(int length) {
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        String[] chars = new String[]{CHARS_NUMERIC, CHARS_LOWER, CHARS_UPPER, CHARS_SYMBOL};
        boolean[] flags = new boolean[]{false, false, false, false};
        int i = 0, j;
        for (int len = chars.length; i < len; ++i) {
            while (flags[j = random.nextInt(len)]) ;
            flags[j] = true;
            builder.append(chars[j].charAt(random.nextInt(chars[j].length())));
        }
        for (; i < length; ++i) {
            builder.append(CHARS_NUMERIC_LETTER_SYMBOL.charAt(random.nextInt(CHARS_NUMERIC_LETTER_SYMBOL.length())));
        }
        return builder.toString();
    }

    public static void main(String[] args) throws Exception {
        String serialNumber = generateSerialNumber();
        System.out.println("serialNumber = " + serialNumber);

        String randomCode = generateRandomCode(6);
        System.out.println("randomCode = " + randomCode);

        String randomCode_chars = generateRandomCode(8, CHARS_NUMERIC_LETTER);
        System.out.println("randomCode_chars = " + randomCode_chars);

        String randomPassword = generateRandomPassword(8);
        System.out.println("randomPassword = " + randomPassword);
    }
}
