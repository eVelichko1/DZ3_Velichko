package ru.otus;

import java.util.ArrayList;
import java.util.List;

public enum Command {

    ADD,
    LIST,
    EDIT,
    FILTER,
    DELETE,
    EXIT;

    public static final List<String> VALUES = collectValues();

    private static List<String> collectValues() {
        List<String> result = new ArrayList<>();
        for (Command type : Command.values()) {
            result.add(type.name());
        }
        return result;
    }

    public static boolean doesNotContain(String value) {
        return value == null || !VALUES.contains(value.toUpperCase().trim());
    }

    public static Command fromString(String value) {
        if (value == null) {
            return null;
        }
        return Command.valueOf(value.toUpperCase().trim());
    }
}
