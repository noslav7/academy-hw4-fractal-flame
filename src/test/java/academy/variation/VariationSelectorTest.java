package academy.variation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import academy.color.RgbColor;
import academy.config.AffineParams;
import java.util.List;
import java.util.stream.Stream;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/** Тесты селектора вариаций. */
class VariationSelectorTest {

    private static final VariationDefinition ONE =
            new VariationDefinition(VariationType.LINEAR, 1.0, RgbColor.of(1, 0, 0), 0.0, AffineParams.IDENTITY);
    private static final VariationDefinition THREE =
            new VariationDefinition(VariationType.SWIRL, 3.0, RgbColor.of(0, 1, 0), 0.0, AffineParams.IDENTITY);

    /** Проверяет, что пустой список запрещён. */
    @Test
    void givenEmptyListWhenConstructedThenThrows() {
        assertThrows(IllegalArgumentException.class, () -> new VariationSelector(List.of()));
    }

    /** Проверяет, что null-список запрещён. */
    @Test
    void givenNullListWhenConstructedThenThrows() {
        assertThrows(NullPointerException.class, () -> new VariationSelector(null));
    }

    /** Проверяет выбор при единственной вариации. */
    @ParameterizedTest
    @MethodSource("singleVariationPicks")
    void givenSingleVariationWhenPickThenAlwaysReturnsSame(double value) {
        VariationSelector selector = new VariationSelector(List.of(ONE));

        assertEquals(ONE, selector.pick(value));
    }

    /** Проверяет выбор с учётом весов. */
    @ParameterizedTest
    @MethodSource("weightedVariationPicks")
    void givenWeightedVariationsWhenPickThenRespectsWeights(double value, VariationDefinition expected) {
        VariationSelector selector = new VariationSelector(List.of(ONE, THREE));

        assertEquals(expected, selector.pick(value));
    }

    /** Проверяет нормализацию вероятности вне диапазона. */
    @ParameterizedTest
    @MethodSource("outOfRangePicks")
    void givenOutOfRangeValuesWhenPickThenClampsGracefully(double value, VariationDefinition expected) {
        VariationSelector selector = new VariationSelector(List.of(ONE, THREE));

        assertEquals(expected, selector.pick(value));
    }

    /** Проверяет fallback на последнюю вариацию при NaN весах. */
    @Test
    void givenNaNWeightsWhenPickThenFallsBackToLast() {
        VariationDefinition broken = new VariationDefinition(
                VariationType.LINEAR, Double.NaN, RgbColor.of(0, 0, 1), 0.0, AffineParams.IDENTITY);
        VariationDefinition fallback =
                new VariationDefinition(VariationType.SWIRL, 1.0, RgbColor.of(1, 1, 0), 0.0, AffineParams.IDENTITY);
        VariationSelector selector = new VariationSelector(List.of(broken, fallback));

        assertEquals(fallback, selector.pick(0.5));
    }

    private static Stream<@NonNull Arguments> singleVariationPicks() {
        return Stream.of(Arguments.of(0.0), Arguments.of(0.9));
    }

    private static Stream<@NonNull Arguments> weightedVariationPicks() {
        return Stream.of(
                Arguments.of(0.0, ONE), Arguments.of(0.24, ONE), Arguments.of(0.26, THREE), Arguments.of(0.99, THREE));
    }

    private static Stream<@NonNull Arguments> outOfRangePicks() {
        return Stream.of(
                Arguments.of(Double.NaN, ONE),
                Arguments.of(-0.5, ONE),
                Arguments.of(1.0, THREE),
                Arguments.of(5.0, THREE));
    }
}
