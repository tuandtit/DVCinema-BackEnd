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
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
}
