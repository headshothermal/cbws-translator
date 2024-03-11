package com.psas.function;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public record Attribute(String name, String value) {
    public Attribute {
        if (isEmpty(name)) throw new IllegalArgumentException("No attribute name provided.");
        if (isEmpty(value)) throw new IllegalArgumentException("No attribute value provided.");
    }

    @Override
    public String toString() {
        return String.format("%s: %s", name, value);
    }
}
