package academy.fractal;

import academy.config.FractalConfig;
import academy.math.MutablePoint;
import academy.math.Point;
import academy.variation.VariationDefinition;
import academy.variation.VariationSelector;
import java.util.Objects;
import java.util.SplittableRandom;

public final class FractalSampler {
    private final FractalConfig config;
    private final VariationSelector selector;

    public FractalSampler(FractalConfig config) {
        this.config = Objects.requireNonNull(config, "config");
        this.selector = new VariationSelector(config.variations());
    }

    public StepResult step(
            Point current, SplittableRandom random, MutablePoint globalAffinePoint, MutablePoint localAffinePoint) {
        Objects.requireNonNull(current, "current");
        Objects.requireNonNull(random, "random");
        Objects.requireNonNull(globalAffinePoint, "globalAffinePoint");
        Objects.requireNonNull(localAffinePoint, "localAffinePoint");
        VariationDefinition variation = selector.pick(random.nextDouble());
        Point afterGlobal = config.affineParams().apply(current, globalAffinePoint).toImmutable();
        Point afterLocal = variation.localAffine().apply(afterGlobal, localAffinePoint).toImmutable();
        Point nextPoint = variation.type().apply(afterLocal, variation, random);
        return new StepResult(nextPoint, variation);
    }

    public record StepResult(Point point, VariationDefinition variation) {}
}
