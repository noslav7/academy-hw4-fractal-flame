package academy.variation;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import academy.color.RgbColor;
import academy.config.AffineParams;
import academy.math.Point;
import java.util.Map;
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

    @Test
    void givenPointWhenLinearAppliedThenReturnsSamePoint() {
        Point point = new Point(0.25, -0.5);

        Point result = VariationType.LINEAR.apply(point, definition(VariationType.LINEAR), UNUSED_RANDOM);

        assertPointEquals(point.x(), point.y(), result);
    }

    @Test
    void givenPointWhenHorseshoeAppliedThenMatchesFormula() {
        Point result =
                VariationType.HORSESHOE.apply(new Point(1.0, 0.0), definition(VariationType.HORSESHOE), UNUSED_RANDOM);

        assertPointEquals(1.0, 0.0, result);
    }

    @Test
    void givenPointWhenSinusoidalAppliedThenUsesSineOfCoordinates() {
        Point result = VariationType.SINUSOIDAL.apply(
                new Point(Math.PI / 2.0, 0.0), definition(VariationType.SINUSOIDAL), UNUSED_RANDOM);

        assertPointEquals(1.0, 0.0, result);
    }

    @Test
    void givenPointWhenBubbleAppliedThenScalesByBubbleFactor() {
        Point result = VariationType.BUBBLE.apply(new Point(1.0, 1.0), definition(VariationType.BUBBLE), UNUSED_RANDOM);

        assertPointEquals(2.0 / 3.0, 2.0 / 3.0, result);
    }

    @Test
    void givenParametersWhenPdjAppliedThenUsesCustomCoefficients() {
        VariationDefinition definition =
                definition(VariationType.PDJ, VariationParameters.of(Map.of("a", 1.0, "b", 2.0, "c", 3.0, "d", 4.0)));

        Point point = new Point(1.0, 1.0);
        double expectedX = Math.sin(1.0 * point.y()) - Math.cos(2.0 * point.x());
        double expectedY = Math.sin(3.0 * point.x()) - Math.cos(4.0 * point.y());

        Point result = VariationType.PDJ.apply(point, definition, UNUSED_RANDOM);

        assertPointEquals(expectedX, expectedY, result);
    }

    @Test
    void givenParametersWhenFan2AppliedThenOffsetsAngle() {
        VariationDefinition definition =
                definition(VariationType.FAN2, VariationParameters.of(Map.of("x", 0.5, "y", 0.0)));

        Point result = VariationType.FAN2.apply(new Point(1.0, 0.0), definition, UNUSED_RANDOM);

        assertPointEquals(0.0, 1.0, result);
    }

    @Test
    void givenJulianParametersWhenApplyThenUsesPowerAndDist() {
        VariationParameters params = VariationParameters.of(Map.of("power", 3.0, "dist", 1.0));
        assertJulianMatchesFormula(new Point(0.0, 1.0), params, 0L);
    }

    @Test
    void givenJulianPowerTwoWhenApplyThenScalesByInversePower() {
        VariationParameters params = VariationParameters.of(Map.of("power", 2.0, "dist", 1.0));
        assertJulianMatchesFormula(new Point(1.0, 0.0), params, 0L);
    }

    private VariationDefinition definition(VariationType type) {
        return new VariationDefinition(type, 1.0, new RgbColor(1, 1, 1), 0.5, AffineParams.IDENTITY);
    }

    private VariationDefinition definition(VariationType type, VariationParameters parameters) {
        return new VariationDefinition(type, 1.0, new RgbColor(1, 1, 1), 0.5, AffineParams.IDENTITY, parameters);
    }

    private void assertJulianMatchesFormula(Point input, VariationParameters params, long seed) {
        SplittableRandom random = new SplittableRandom(seed);
        Point result = VariationType.JULIAN.apply(input, definition(VariationType.JULIAN, params), random);

        // Recompute expected value using the same deterministic random stream.
        SplittableRandom expectedRandom = new SplittableRandom(seed);
        double powerParam = Math.abs(params.get("power", 2.0));
        powerParam = powerParam < 1.0e-6 ? 1.0 : powerParam;
        int power = (int) Math.max(1, Math.round(powerParam));
        double dist = params.get("dist", 1.0);
        double r = Math.hypot(input.x(), input.y());
        double theta = Math.atan2(input.y(), input.x());
        double magnitude = Math.pow(r, dist / powerParam);
        int k = expectedRandom.nextInt(power);
        double angle = (theta + 2.0 * Math.PI * k) / powerParam;
        assertPointEquals(magnitude * Math.cos(angle), magnitude * Math.sin(angle), result);
    }

    private void assertPointEquals(double expectedX, double expectedY, Point result) {
        assertAll(
                () -> assertEquals(expectedX, result.x(), EPSILON), () -> assertEquals(expectedY, result.y(), EPSILON));
    }
}
