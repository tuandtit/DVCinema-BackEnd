package com.cinema.booking_app.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.text.Normalizer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TextUtils {
    public static String like(final String value) {
        return "%" + removeVietnameseAccent(value.toLowerCase()) + "%";
    }

    private static String removeVietnameseAccent(String input) {
        if (input == null) return null;

        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String noDiacritics = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return noDiacritics
                .replace("đ", "d")
                .replace("Đ", "D");
    }
}
