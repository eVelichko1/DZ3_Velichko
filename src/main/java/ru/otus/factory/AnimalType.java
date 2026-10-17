package ru.otus.factory;

import java.util.ArrayList;
import java.util.List;

public enum AnimalType {

    CAT,
    DOG,
    DUCK;

    public static final List<String> VALUES = collectValues();

    private static List<String> collectValues() {
        List<String> result = new ArrayList<>();
        for (AnimalType type : AnimalType.values()) {
            result.add(type.name());
        }
        return result;
    }

    public static boolean doesNotContain(String value) {
        return value == null || !VALUES.contains(value.toUpperCase().trim());
    }

    public static AnimalType fromString(String value) {
        if (value == null) {
            return null;
        }
        return AnimalType.valueOf(value.toUpperCase().trim());
    }
}
