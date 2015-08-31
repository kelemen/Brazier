package com.github.kelemen.brazier;

import java.util.Locale;

public final class Priority implements Comparable<Priority> {
    public static final Priority LOWEST_PRIORITY = new Priority(Integer.MIN_VALUE);
    public static final Priority LOW_PRIORITY = new Priority(-1);
    public static final Priority NORMAL_PRIORITY = new Priority(0);
    public static final Priority HIGH_PRIORITY = new Priority(1);
    public static final Priority HIGHEST_PRIORITY = new Priority(Integer.MAX_VALUE);

    private final int value;

    public Priority(int value) {
        this.value = value;
    }

    public static Priority parse(String priorityStr) {
        String normStr = priorityStr.trim().toLowerCase(Locale.ROOT);
        switch (normStr) {
            case "lowest":
                return LOWEST_PRIORITY;
            case "low":
                return LOW_PRIORITY;
            case "normal":
                return NORMAL_PRIORITY;
            case "high":
                return HIGH_PRIORITY;
            case "highest":
                return HIGHEST_PRIORITY;
            default:
                return new Priority(Integer.parseInt(normStr));
        }
    }

    public static int parsePriorityValue(String priorityStr) {
        return parse(priorityStr).getValue();
    }

    public int getValue() {
        return value;
    }

    @Override
    public int compareTo(Priority o) {
        return Integer.compare(value, o.value);
    }
}
