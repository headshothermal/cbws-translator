package com.psas.function;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/** Represents a CBWS function attribute. */
public record Attribute(String name, String value, int index) implements Comparable<Attribute> {
    /**
     * Constructs a CBWS function attribute.
     *
     * @param name  Attribute name.
     * @param value Attribute value.
     * @param index Attribute index within the function hex string.
     */
    public Attribute {
        if (isEmpty(name)) throw new IllegalArgumentException("No attribute name provided.");
        if (isEmpty(value)) throw new IllegalArgumentException("No attribute value provided.");
        if (index < 0) throw new IllegalArgumentException("Invalid attribute index.");
    }

    @Override
    public String toString() {
        return String.format("%s: %s", name, value);
    }

    @Override
    public int compareTo(final Attribute attribute) {
        return Integer.compare(index, attribute.index);
    }
}
