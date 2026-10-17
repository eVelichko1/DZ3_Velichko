package ru.otus.animals;

import java.util.Locale;

public enum Color {
    WHITE("белый"),
    BLACK("чёрный");

    private final String value;

    Color(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Color fromString(String text) {
        if (text == null) {
            return null;
        }
        String normalized = text.trim().toLowerCase(Locale.ROOT).replace('ё', 'е');
        for (Color color : values()) {
            String russian = color.value.toLowerCase(Locale.ROOT).replace('ё', 'е');
            if (russian.equals(normalized) || color.name().equalsIgnoreCase(text.trim())) {
                return color;
            }
        }
        return null;
    }
}
