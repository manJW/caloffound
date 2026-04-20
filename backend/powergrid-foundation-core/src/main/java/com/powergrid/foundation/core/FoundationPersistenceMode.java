package com.powergrid.foundation.core;

public enum FoundationPersistenceMode {
    DB("db"),
    MEMORY("memory");

    private final String value;

    FoundationPersistenceMode(String value) {
        this.value = value;
    }

    public static FoundationPersistenceMode from(String value) {
        if (value == null || value.isBlank()) {
            return DB;
        }
        for (FoundationPersistenceMode mode : values()) {
            if (mode.value.equalsIgnoreCase(value.trim())) {
                return mode;
            }
        }
        return DB;
    }

    public boolean isDatabase() {
        return this == DB;
    }

    public String value() {
        return value;
    }
}
