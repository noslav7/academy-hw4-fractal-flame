package academy.util;

import java.util.Objects;

public final class StringValidators {
    private StringValidators() {}

    public static String requireLength(String value, String fieldName, int minLength, int maxLength) {
        Objects.requireNonNull(fieldName, "fieldName");
        Objects.requireNonNull(value, fieldName);
        String trimmed = value.trim();
        if (trimmed.length() < minLength || trimmed.length() > maxLength) {
            throw new IllegalArgumentException(
                    fieldName + " length must be between " + minLength + " and " + maxLength);
        }
        return trimmed;
    }
}
