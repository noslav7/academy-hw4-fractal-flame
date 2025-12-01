package academy.render;

import academy.variation.VariationDefinition;
import java.util.ArrayList;
import java.util.List;

final class VariationSelector {

    private final List<VariationSelection> selections;
    private final double totalWeight;

    VariationSelector(List<VariationDefinition> definitions) {
        this.selections = new ArrayList<>(definitions.size());
        double cumulative = 0.0;
        for (VariationDefinition definition : definitions) {
            cumulative += definition.weight();
            selections.add(new VariationSelection(definition, cumulative));
        }
        this.totalWeight = cumulative;
    }

    VariationSelection pick(double randomValue) {
        double scaled = randomValue * totalWeight;
        for (VariationSelection selection : selections) {
            if (scaled <= selection.cumulativeWeight()) {
                return selection;
            }
        }
        return selections.get(selections.size() - 1);
    }
}

