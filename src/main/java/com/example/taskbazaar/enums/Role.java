package com.example.taskbazaar.enums;

public enum Role {
    ADMIN("admin"),
    FREELANCER("freelancer"),
    BUYER("buyer");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

