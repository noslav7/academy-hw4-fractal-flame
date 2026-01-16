package academy.util;

import java.util.Objects;

/** Утилиты валидации строковых значений. */
public final class StringValidators {
    private StringValidators() {}

    /**
     * Проверяет строку на допустимую длину и возвращает обрезанное значение.
     *
     * @param value исходная строка
     * @param fieldName имя поля для сообщения об ошибке
     * @param minLength минимальная длина
     * @param maxLength максимальная длина
     * @return обрезанная строка
     */
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
