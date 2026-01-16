package academy.variation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Выбирает вариацию по весам. */
public final class VariationSelector {

    private final List<WeightedVariation> weightedVariations;
    private final double totalWeight;

    /**
     * Создаёт селектор для заданных вариаций.
     *
     * @param definitions список вариаций (не пустой)
     */
    public VariationSelector(List<VariationDefinition> definitions) {
        List<VariationDefinition> safeDefinitions = List.copyOf(Objects.requireNonNull(definitions, "definitions"));
        if (safeDefinitions.isEmpty()) {
            throw new IllegalArgumentException("At least one variation definition is required");
        }
        this.weightedVariations = buildWeightedVariations(safeDefinitions);
        this.totalWeight =
                this.weightedVariations.get(this.weightedVariations.size() - 1).cumulativeWeight();
    }

    /**
     * Выбирает вариацию по псевдослучайному значению из диапазона {@code [0, 1)}.
     *
     * @param randomValue значение, обычно получаемое из {@link java.util.SplittableRandom#nextDouble()}
     * @return выбранная вариация
     */
    public VariationDefinition pick(double randomValue) {
        if (weightedVariations.size() == 1) {
            return weightedVariations.get(0).definition();
        }
        double scaled = clampProbability(randomValue) * totalWeight;
        for (WeightedVariation variation : weightedVariations) {
            if (scaled <= variation.cumulativeWeight()) {
                return variation.definition();
            }
        }
        return weightedVariations.get(weightedVariations.size() - 1).definition();
    }

    /** Строит список кумулятивных весов. */
    private static List<WeightedVariation> buildWeightedVariations(List<VariationDefinition> definitions) {
        List<WeightedVariation> result = new ArrayList<>(definitions.size());
        double cumulative = 0.0;
        for (VariationDefinition definition : definitions) {
            cumulative += definition.weight();
            result.add(new WeightedVariation(definition, cumulative));
        }
        return List.copyOf(result);
    }

    /** Ограничивает вероятность диапазоном {@code [0, 1)}. */
    private static double clampProbability(double value) {
        if (Double.isNaN(value) || value < 0.0) {
            return 0.0;
        }
        if (value >= 1.0) {
            return Math.nextDown(1.0);
        }
        return value;
    }

    /** Пара вариации и накопленного веса. */
    private record WeightedVariation(VariationDefinition definition, double cumulativeWeight) {}
}
