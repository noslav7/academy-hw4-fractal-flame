package academy.variation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Weighted selector that picks {@link VariationDefinition} instances according to their weight. */
public final class VariationSelector {

    private final List<WeightedVariation> weightedVariations;
    private final double totalWeight;

    /**
     * Creates a selector for the provided variation definitions.
     *
     * @param definitions source variations, must not be {@code null} or empty
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
     * Picks a variation based on a pseudo-random value from the {@code [0, 1)} range.
     *
     * @param randomValue value typically produced by {@link java.util.SplittableRandom#nextDouble()}
     * @return variation definition selected in proportion to its weight
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

    /** Builds cumulative weights to simplify subsequent selection. */
    private static List<WeightedVariation> buildWeightedVariations(List<VariationDefinition> definitions) {
        List<WeightedVariation> result = new ArrayList<>(definitions.size());
        double cumulative = 0.0;
        for (VariationDefinition definition : definitions) {
            cumulative += definition.weight();
            result.add(new WeightedVariation(definition, cumulative));
        }
        return List.copyOf(result);
    }

    /** Ensures the provided probability stays within the {@code [0, 1)} interval. */
    private static double clampProbability(double value) {
        if (Double.isNaN(value) || value < 0.0) {
            return 0.0;
        }
        if (value >= 1.0) {
            return Math.nextDown(1.0);
        }
        return value;
    }

    private record WeightedVariation(VariationDefinition definition, double cumulativeWeight) {}
}
