package com.keakimleang.digital_menu.utils;

import java.text.*;
import java.util.regex.*;

public class Slugify {
    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^\\w-]");

    public static String slugify(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        // Normalize the string
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);

        // Replace non-alphanumeric characters with empty string
        String slug = NON_ALPHANUMERIC.matcher(normalized).replaceAll("");

        // Convert to lowercase and replace spaces with hyphens
        slug = slug.toLowerCase().replaceAll("\\s+", "-");

        // Remove leading and trailing hyphens
        slug = slug.replaceAll("^-+|-+$", "");

        return slug;
    }
}

