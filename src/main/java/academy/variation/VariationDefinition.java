package academy.variation;

import academy.color.RgbColor;
import academy.config.AffineParams;
import java.util.Objects;

/**
 * Описание вариации: тип, вес, цвет и локальная аффинная матрица.
 *
 * @param type тип вариации
 * @param weight вес вариации
 * @param color цвет
 * @param colorIndex индекс цвета
 * @param localAffine локальные аффинные коэффициенты
 * @param parameters параметры вариации
 */
public record VariationDefinition(
        VariationType type,
        double weight,
        RgbColor color,
        double colorIndex,
        AffineParams localAffine,
        VariationParameters parameters) {

    /** Проверяет корректность значений. */
    public VariationDefinition {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(color, "color");
        Objects.requireNonNull(localAffine, "localAffine");
        Objects.requireNonNull(parameters, "parameters");
        if (weight <= 0.0) {
            throw new IllegalArgumentException("weight must be positive");
        }
        if (Double.isNaN(colorIndex)) {
            throw new IllegalArgumentException("colorIndex must be a valid number");
        }
    }

    /** Создаёт вариацию без дополнительных параметров. */
    public VariationDefinition(
            VariationType type, double weight, RgbColor color, double colorIndex, AffineParams localAffine) {
        this(type, weight, color, colorIndex, localAffine, VariationParameters.empty());
    }
}
