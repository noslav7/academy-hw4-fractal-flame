package academy.config;

import academy.camera.CameraSettings;
import academy.color.Palette;
import academy.color.RgbColor;
import academy.variation.VariationDefinition;
import academy.variation.VariationParameters;
import academy.variation.VariationType;
import java.util.ArrayList;
import java.util.List;

/** Преобразует JSON-структуры в внутренние доменные объекты конфигурации. */
final class JsonConfigMapper {
    private JsonConfigMapper() {}

    /** Преобразует JSON-аффинные коэффициенты. */
    static AffineParams toAffine(JsonFractalConfig.JsonAffineParams params) {
        if (params == null) return null;
        Double a = params.a;
        Double b = params.b;
        Double c = params.c;
        Double d = params.d;
        Double e = params.e;
        Double f = params.f;
        if (a == null && b == null && c == null && d == null && e == null && f == null) {
            return null;
        }
        return new AffineParams(
                valueOrDefault(a, 1.0),
                valueOrDefault(b, 0.0),
                valueOrDefault(c, 0.0),
                valueOrDefault(d, 0.0),
                valueOrDefault(e, 1.0),
                valueOrDefault(f, 0.0));
    }

    /** Преобразует список JSON-функций в вариации. */
    static List<VariationDefinition> toVariations(List<JsonFractalConfig.JsonFunction> functions) {
        if (functions == null) {
            return null;
        }
        if (functions.isEmpty()) {
            throw new IllegalArgumentException("Config functions list must not be empty");
        }
        List<VariationDefinition> result = new ArrayList<>();
        int index = 0;
        for (JsonFractalConfig.JsonFunction function : functions) {
            if (function == null || function.name == null || function.weight == null) {
                continue;
            }
            VariationType type = VariationType.fromSymbol(function.name);
            RgbColor color = colorFrom(function.color, index);
            double colorIndex = function.colorIndex != null ? function.colorIndex : Math.min(0.99, index / 12.0);
            AffineParams localAffine = toAffine(function.affine);
            if (localAffine == null) {
                localAffine = AffineParams.IDENTITY;
            }
            VariationParameters parameters = VariationParameters.of(function.params);
            result.add(new VariationDefinition(type, function.weight, color, colorIndex, localAffine, parameters));
            index++;
        }
        if (result.isEmpty()) {
            throw new IllegalArgumentException("Config functions list contains no valid entries");
        }
        return List.copyOf(result);
    }

    /** Преобразует JSON-палитру. */
    static Palette toPalette(JsonFractalConfig.JsonPalette palette) {
        if (palette == null || palette.colors == null || palette.colors.isEmpty()) {
            return null;
        }
        List<RgbColor> colors = new ArrayList<>();
        for (JsonFractalConfig.JsonColor color : palette.colors) {
            colors.add(RgbColor.of(
                    valueOrDefault(color.r(), 0.0), valueOrDefault(color.g(), 0.0), valueOrDefault(color.b(), 0.0)));
        }
        return colors.isEmpty() ? null : new Palette(colors);
    }

    /** Преобразует JSON-настройки камеры. */
    static CameraSettings toCamera(JsonFractalConfig.JsonCamera camera) {
        if (camera == null) {
            return null;
        }
        double centerX = camera.centerX != null ? camera.centerX : 0.0;
        double centerY = camera.centerY != null ? camera.centerY : 0.0;
        double scale = camera.scale != null ? camera.scale : 1.0;
        double rotation = camera.rotation != null ? camera.rotation : 0.0;
        boolean autoFit = camera.autoFit == null || camera.autoFit;
        double fitMargin = camera.fitMargin != null ? camera.fitMargin : 0.1;
        long fitSamples = camera.fitSamples != null ? camera.fitSamples : 200_000L;
        return new CameraSettings(centerX, centerY, scale, rotation, autoFit, fitMargin, fitSamples);
    }

    /** Возвращает цвет функции или вычисляет запасной цвет по индексу. */
    private static RgbColor colorFrom(JsonFractalConfig.JsonColor color, int paletteIndex) {
        if (color != null && color.r() != null && color.g() != null && color.b() != null) {
            return RgbColor.of(color.r(), color.g(), color.b());
        }
        double hue = paletteIndex % 12 / 12.0;
        return RgbColor.fromHsb(hue, 0.7, 0.9);
    }

    /** Возвращает значение или дефолт. */
    private static double valueOrDefault(Double value, double defaultValue) {
        return value != null ? value : defaultValue;
    }
}
