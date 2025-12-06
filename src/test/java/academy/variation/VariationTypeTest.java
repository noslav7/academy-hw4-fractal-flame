package academy.variation;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import academy.color.RgbColor;
import academy.config.AffineParams;
import academy.math.Point;
import java.util.SplittableRandom;
import org.junit.jupiter.api.Test;

class VariationTypeTest {

    private static final double EPSILON = 1.0e-12;
    private static final SplittableRandom UNUSED_RANDOM = new SplittableRandom(0);

    @Test
    void givenUnitPointWhenSwirlAppliedThenCoordinatesRotate() {
        Point point = new Point(1.0, 0.0);

        Point result = VariationType.SWIRL.apply(point, definition(VariationType.SWIRL), UNUSED_RANDOM);

        assertAll(
                () -> assertEquals(Math.sin(1.0), result.x(), EPSILON),
                () -> assertEquals(Math.cos(1.0), result.y(), EPSILON));
    }

    @Test
    void givenPointWhenSphericalAppliedThenDistanceInverts() {
        Point point = new Point(2.0, 0.0);

        Point result = VariationType.SPHERICAL.apply(point, definition(VariationType.SPHERICAL), UNUSED_RANDOM);

        assertAll(
                () -> assertEquals(0.5, result.x(), EPSILON), () -> assertEquals(0.0, result.y(), EPSILON));
    }

    private VariationDefinition definition(VariationType type) {
        return new VariationDefinition(type, 1.0, new RgbColor(1, 1, 1), 0.5, AffineParams.IDENTITY);
    }
}

