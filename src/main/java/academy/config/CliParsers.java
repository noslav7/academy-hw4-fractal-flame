package academy.config;

import academy.color.RgbColor;
import academy.util.StringValidators;
import academy.variation.VariationDefinition;
import academy.variation.VariationParameters;
import academy.variation.VariationType;
import java.util.ArrayList;
import java.util.List;

/**
 * Утилиты для парсинга строковых параметров CLI.
 */
public final class CliParsers {
    private CliParsers() {}

    private static final int FUNCTIONS_MIN_LENGTH = 3;
    private static final int FUNCTIONS_MAX_LENGTH = 10_000;
    private static final int WEIGHT_MIN_LENGTH = 1;
    private static final int WEIGHT_MAX_LENGTH = 32;

    /**
     * Разбирает список вариаций формата {@code name:weight,name:weight}.
     *
     * @param raw строка из CLI
     * @return список определений вариаций или {@code null}, если строка пустая
     */
    public static List<VariationDefinition> parseFunctions(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String normalized = StringValidators.requireLength(raw, "functions", FUNCTIONS_MIN_LENGTH, FUNCTIONS_MAX_LENGTH);
        String[] tokens = normalized.split(",");
        List<VariationDefinition> variations = new ArrayList<>();
        int index = 0;
        for (String token : tokens) {
            if (token.isBlank()) continue;
            String[] pair = token.split(":");
            if (pair.length != 2) {
                throw new IllegalArgumentException("Invalid function token: " + token);
            }
            String name = pair[0];
            String weightValue =
                    StringValidators.requireLength(pair[1], "variation weight", WEIGHT_MIN_LENGTH, WEIGHT_MAX_LENGTH);
            double weight = Double.parseDouble(weightValue);
            VariationType type = VariationType.fromSymbol(name);
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

    /**
     * Разбирает строку аффинных коэффициентов {@code a,b,c,d,e,f}.
     *
     * @param raw строка из CLI
     * @return аффинные коэффициенты или {@code null}, если строка пустая
     */
    public static AffineParams parseAffine(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String normalized = StringValidators.requireLength(raw, "affine params", 3, 256);
        String[] parts = normalized.split(",");
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
