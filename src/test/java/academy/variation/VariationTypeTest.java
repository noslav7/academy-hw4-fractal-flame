package academy.variation;

import academy.config.AffineParams;
import academy.math.Point;
import academy.color.RgbColor;
import java.util.SplittableRandom;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class VariationTypeTest {

    @Test
    void swirlShouldRotatePoint() {
        Point result =
                VariationType.SWIRL.apply(new Point(1.0, 0.0), definition(VariationType.SWIRL), new SplittableRandom(1));
        Assertions.assertThat(result.x()).isNotEqualTo(1.0);
        Assertions.assertThat(result.y()).isNotZero();
    }

    @Test
    void sphericalShouldInvertDistance() {
        Point point = new Point(2.0, 0.0);
        Point result =
                VariationType.SPHERICAL.apply(point, definition(VariationType.SPHERICAL), new SplittableRandom(2));
        Assertions.assertThat(result.x()).isEqualTo(0.5);
        Assertions.assertThat(result.y()).isZero();
    }

    private VariationDefinition definition(VariationType type) {
        return new VariationDefinition(type, 1.0, new RgbColor(1, 1, 1), 0.5, AffineParams.IDENTITY);
    }
}

