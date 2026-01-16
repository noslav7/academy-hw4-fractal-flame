package academy.variation;

import java.util.Collections;
import java.util.Map;

/**
 * Параметры вариации в виде неизменяемой карты.
 */
public final class VariationParameters {
    private static final VariationParameters EMPTY = new VariationParameters(Collections.emptyMap());

    private final Map<String, Double> values;

    /**
     * Создаёт контейнер параметров.
     */
    private VariationParameters(Map<String, Double> values) {
        this.values = values;
    }

    /**
     * Возвращает пустой набор параметров.
     */
    public static VariationParameters empty() {
        return EMPTY;
    }

    /**
     * Создаёт параметры из карты.
     *
     * @param values карта параметров
     * @return контейнер параметров
     */
    public static VariationParameters of(Map<String, Double> values) {
        if (values == null || values.isEmpty()) {
            return EMPTY;
        }
        return new VariationParameters(Map.copyOf(values));
    }

    /**
     * Возвращает значение параметра или значение по умолчанию.
     *
     * @param name имя параметра
     * @param defaultValue значение по умолчанию
     * @return найденное значение или default
     */
    public double get(String name, double defaultValue) {
        Double value = values.get(name);
        return value != null ? value : defaultValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VariationParameters that)) return false;
        return values.equals(that.values);
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }

    @Override
    public String toString() {
        return "VariationParameters" + values;
    }
}
