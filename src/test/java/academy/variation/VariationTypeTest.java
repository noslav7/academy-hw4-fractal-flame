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

        assertPointEquals(Math.sin(1.0), Math.cos(1.0), result);
    }

    @Test
    void givenPointWhenSphericalAppliedThenDistanceInverts() {
        Point point = new Point(2.0, 0.0);

        Point result = VariationType.SPHERICAL.apply(point, definition(VariationType.SPHERICAL), UNUSED_RANDOM);

        assertPointEquals(0.5, 0.0, result);
    }

    @Test
    void givenAxisPointWhenDiscAppliedThenProducesHalfArc() {
        Point result = VariationType.DISC.apply(new Point(0.0, 1.0), definition(VariationType.DISC), UNUSED_RANDOM);

        assertPointEquals(0.0, -0.5, result);
    }

    @Test
    void givenUnitPointWhenSpiralAppliedThenUnwrapsAroundOrigin() {
        Point result = VariationType.SPIRAL.apply(new Point(1.0, 0.0), definition(VariationType.SPIRAL), UNUSED_RANDOM);

        assertPointEquals(1.8414709848078965, -0.5403023058681398, result);
    }

    @Test
    void givenUnitPointWhenHeartAppliedThenMirrorsAcrossAxis() {
        Point result = VariationType.HEART.apply(new Point(1.0, 0.0), definition(VariationType.HEART), UNUSED_RANDOM);

        assertPointEquals(0.0, -1.0, result);
    }

    @Test
    void givenDiagonalPointWhenHyperbolicAppliedThenMatchesFormula() {
        Point result = VariationType.HYPERBOLIC.apply(
                new Point(1.0, 1.0), definition(VariationType.HYPERBOLIC), UNUSED_RANDOM);

        assertPointEquals(0.5, 1.0, result);
    }

    @Test
    void givenUnitPointWhenFisheyeAppliedThenSwapsAndScales() {
        Point result =
                VariationType.FISHEYE.apply(new Point(1.0, 0.0), definition(VariationType.FISHEYE), UNUSED_RANDOM);

        assertPointEquals(0.0, 1.0, result);
    }

    private VariationDefinition definition(VariationType type) {
        return new VariationDefinition(type, 1.0, new RgbColor(1, 1, 1), 0.5, AffineParams.IDENTITY);
    }

    private void assertPointEquals(double expectedX, double expectedY, Point result) {
        assertAll(
                () -> assertEquals(expectedX, result.x(), EPSILON), () -> assertEquals(expectedY, result.y(), EPSILON));
    }
}
