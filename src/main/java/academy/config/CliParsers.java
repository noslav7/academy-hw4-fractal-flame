package academy.config;

import academy.color.RgbColor;
import academy.variation.VariationDefinition;
import academy.variation.VariationParameters;
import academy.variation.VariationType;
import java.util.ArrayList;
import java.util.List;

public final class CliParsers {
    private CliParsers() {}

    public static List<VariationDefinition> parseFunctions(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String[] tokens = raw.split(",");
        List<VariationDefinition> variations = new ArrayList<>();
        int index = 0;
        for (String token : tokens) {
            if (token.isBlank()) continue;
            String[] pair = token.split(":");
            if (pair.length != 2) {
                throw new IllegalArgumentException("Invalid function token: " + token);
            }
            String name = pair[0].trim();
            double weight = Double.parseDouble(pair[1].trim());
            VariationType type = VariationType.fromName(name);
            double hue = index % 12 / 12.0;
            variations.add(new VariationDefinition(
                    type,
                    weight,
                    RgbColor.fromHsb(hue, 0.7, 0.9),
                    Math.min(0.99, index / 12.0),
                    AffineParams.IDENTITY,
                    VariationParameters.empty()));
            index++;
        }
        if (variations.isEmpty()) {
            throw new IllegalArgumentException("At least one variation must be specified");
        }
        return variations;
    }

    public static AffineParams parseAffine(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String[] parts = raw.split(",");
        if (parts.length != 6) {
            throw new IllegalArgumentException("Affine params must contain 6 numbers");
        }
        double[] values = new double[6];
        for (int i = 0; i < parts.length; i++) {
            values[i] = Double.parseDouble(parts[i].trim());
        }
        return new AffineParams(values[0], values[1], values[2], values[3], values[4], values[5]);
    }
}
