package academy.variation;

import academy.color.RgbColor;
import academy.config.AffineParams;
import java.util.Objects;

/** Couples a variation type with weight, color and a local affine transform. */
public record VariationDefinition(
        VariationType type,
        double weight,
        RgbColor color,
        double colorIndex,
        AffineParams localAffine,
        VariationParameters parameters) {

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

    public VariationDefinition(
            VariationType type, double weight, RgbColor color, double colorIndex, AffineParams localAffine) {
        this(type, weight, color, colorIndex, localAffine, VariationParameters.empty());
    }
}
