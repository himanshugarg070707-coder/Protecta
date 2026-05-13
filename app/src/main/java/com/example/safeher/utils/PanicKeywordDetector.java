package com.example.safeher.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class PanicKeywordDetector {

    private static final List<String> PANIC_KEYWORDS = Arrays.asList(
            "help",
            "save me",
            "emergency",
            "danger",
            "i am unsafe"
    );

    private PanicKeywordDetector() {
    }

    public static String detectKeyword(String speechText) {
        if (speechText == null) {
            return null;
        }

        String normalized = speechText.toLowerCase(Locale.US);
        for (String keyword : PANIC_KEYWORDS) {
            if (normalized.contains(keyword)) {
                return keyword;
            }
        }
        return null;
    }
}
