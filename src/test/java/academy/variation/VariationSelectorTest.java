package academy.variation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import academy.color.RgbColor;
import academy.config.AffineParams;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Тесты селектора вариаций.
 */
class VariationSelectorTest {

    private static final VariationDefinition ONE =
            new VariationDefinition(VariationType.LINEAR, 1.0, RgbColor.of(1, 0, 0), 0.0, AffineParams.IDENTITY);
    private static final VariationDefinition THREE =
            new VariationDefinition(VariationType.SWIRL, 3.0, RgbColor.of(0, 1, 0), 0.0, AffineParams.IDENTITY);

    /**
     * Проверяет, что пустой список запрещён.
     */
    @Test
    void givenEmptyListWhenConstructedThenThrows() {
        assertThrows(IllegalArgumentException.class, () -> new VariationSelector(List.of()));
    }

    /**
     * Проверяет выбор при единственной вариации.
     */
    @Test
    void givenSingleVariationWhenPickThenAlwaysReturnsSame() {
        VariationSelector selector = new VariationSelector(List.of(ONE));

        assertEquals(ONE, selector.pick(0.0));
        assertEquals(ONE, selector.pick(0.9));
    }

    /**
     * Проверяет выбор с учётом весов.
     */
    @Test
    void givenWeightedVariationsWhenPickThenRespectsWeights() {
        VariationSelector selector = new VariationSelector(List.of(ONE, THREE));

        assertEquals(ONE, selector.pick(0.0)); // scaled = 0
        assertEquals(ONE, selector.pick(0.24)); // scaled just under cumulative 1.0
        assertEquals(THREE, selector.pick(0.26)); // scaled slightly above 1.0
        assertEquals(THREE, selector.pick(0.99)); // high probabilities clamp to last element
    }

    /**
     * Проверяет нормализацию вероятности вне диапазона.
     */
    @Test
    void givenOutOfRangeValuesWhenPickThenClampsGracefully() {
        VariationSelector selector = new VariationSelector(List.of(ONE, THREE));

        assertEquals(ONE, selector.pick(Double.NaN));
        assertEquals(ONE, selector.pick(-0.5));
        assertEquals(THREE, selector.pick(5.0));
    }
}
