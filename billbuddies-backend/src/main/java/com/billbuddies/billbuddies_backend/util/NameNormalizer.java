package com.billbuddies.billbuddies_backend.util;

public final class NameNormalizer {

    private NameNormalizer() {
        // utility class
    }

    public static String capitalizeFirstLetter(String input) {
        if (input == null) {
            return null;
        }

        String trimmed = input.trim();

        if (trimmed.isEmpty()) {
            return trimmed;
        }

        return Character.toUpperCase(trimmed.charAt(0)) + trimmed.substring(1);
    }
}
