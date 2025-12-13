package academy.config;

import academy.color.RgbColor;
import academy.variation.VariationDefinition;
import academy.variation.VariationParameters;
import academy.variation.VariationType;
import java.util.ArrayList;
import java.util.List;

final class VariationFactory {

    private VariationFactory() {}

    static List<VariationDefinition> defaultVariations() {
        List<VariationDefinition> defaults = new ArrayList<>();
        defaults.add(new VariationDefinition(
                VariationType.LINEAR,
                1.0,
                paletteColor(0),
                0.05,
                new AffineParams(0.8, 0, -0.5, 0, 0.8, -0.5),
                VariationParameters.empty()));
        defaults.add(new VariationDefinition(
                VariationType.SWIRL,
                0.9,
                paletteColor(1),
                0.25,
                new AffineParams(0.6, 0, 0.6, 0, 0.6, -0.4),
                VariationParameters.empty()));
        defaults.add(new VariationDefinition(
                VariationType.HORSESHOE,
                0.8,
                paletteColor(2),
                0.45,
                new AffineParams(0.5, 0.2, -0.3, -0.2, 0.5, 0.5),
                VariationParameters.empty()));
        defaults.add(new VariationDefinition(
                VariationType.SINUSOIDAL,
                0.6,
                paletteColor(3),
                0.65,
                new AffineParams(0.7, -0.1, 0.4, 0.1, 0.7, 0.3),
                VariationParameters.empty()));
        defaults.add(new VariationDefinition(
                VariationType.SPHERICAL,
                0.5,
                paletteColor(4),
                0.85,
                new AffineParams(0.4, 0.3, -0.2, -0.3, 0.4, 0.2),
                VariationParameters.empty()));
        return defaults;
    }

    private static RgbColor paletteColor(int index) {
        float hue = (index % 6) / 6.0f;
        float saturation = 0.8f;
        float brightness = 0.9f;
        int rgb = java.awt.Color.HSBtoRGB(hue, saturation, brightness);
        double r = ((rgb >> 16) & 0xFF) / 255.0;
        double g = ((rgb >> 8) & 0xFF) / 255.0;
        double b = (rgb & 0xFF) / 255.0;
        return new RgbColor(r, g, b);
    }
}
