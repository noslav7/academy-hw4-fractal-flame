package academy.config;

import academy.camera.CameraSettings;
import academy.color.Palette;
import academy.color.RgbColor;
import academy.variation.VariationDefinition;
import academy.variation.VariationParameters;
import academy.variation.VariationType;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Helper that merges defaults, JSON config and CLI overrides into a {@link FractalConfig}. */
public final class ConfigBuilder {

    private final FractalConfig.Builder delegate = FractalConfig.builder();

    public ConfigBuilder apply(JsonFractalConfig jsonConfig) {
        if (jsonConfig == null) {
            return this;
        }
        if (jsonConfig.size != null) {
            delegate.width(jsonConfig.size.width());
            delegate.height(jsonConfig.size.height());
        }
        delegate.iterationCount(jsonConfig.iterationCount);
        delegate.threads(jsonConfig.threads);
        delegate.seed(jsonConfig.seed);
        delegate.outputPath(jsonConfig.outputPath());
        delegate.affineParams(asAffine(jsonConfig.affineParams));
        delegate.variations(asVariations(jsonConfig.functions));
        delegate.burnInIterations(jsonConfig.burnIn);
        delegate.palette(asPalette(jsonConfig.palette));
        delegate.camera(asCamera(jsonConfig.camera));
        delegate.brightness(jsonConfig.brightness);
        delegate.gamma(jsonConfig.gamma);
        delegate.gammaCorrection(jsonConfig.gammaCorrection);
        delegate.logGammaCorrection(jsonConfig.logGammaCorrection);
        delegate.symmetryLevel(jsonConfig.symmetryLevel);
        return this;
    }

    public ConfigBuilder apply(CliOverrides overrides) {
        Objects.requireNonNull(overrides, "overrides");
        if (overrides.width() != null) delegate.width(overrides.width());
        if (overrides.height() != null) delegate.height(overrides.height());
        if (overrides.iterations() != null) delegate.iterationCount(overrides.iterations());
        if (overrides.seed() != null) delegate.seed(overrides.seed());
        if (overrides.outputPath() != null) delegate.outputPath(overrides.outputPath());
        if (overrides.threads() != null) delegate.threads(overrides.threads());
        if (overrides.affineParams() != null) delegate.affineParams(overrides.affineParams());
        if (overrides.variations() != null) delegate.variations(overrides.variations());
        if (overrides.burnIn() != null) delegate.burnInIterations(overrides.burnIn());
        if (overrides.palette() != null) delegate.palette(overrides.palette());
        if (overrides.camera() != null) delegate.camera(overrides.camera());
        if (overrides.brightness() != null) delegate.brightness(overrides.brightness());
        if (overrides.gamma() != null) delegate.gamma(overrides.gamma());
        if (overrides.gammaCorrection() != null) delegate.gammaCorrection(overrides.gammaCorrection());
        if (overrides.logGammaCorrection() != null) {
            delegate.logGammaCorrection(overrides.logGammaCorrection());
        }
        if (overrides.symmetryLevel() != null) delegate.symmetryLevel(overrides.symmetryLevel());
        return this;
    }

    public FractalConfig build() {
        return delegate.build();
    }

    private static AffineParams asAffine(JsonFractalConfig.JsonAffineParams params) {
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

    private static double valueOrDefault(Double value, double defaultValue) {
        return value != null ? value : defaultValue;
    }

    private static List<VariationDefinition> asVariations(List<JsonFractalConfig.JsonFunction> functions) {
        if (functions == null || functions.isEmpty()) {
            return null;
        }
        List<VariationDefinition> result = new ArrayList<>();
        int index = 0;
        for (JsonFractalConfig.JsonFunction function : functions) {
            if (function == null || function.name == null || function.weight == null) {
                continue;
            }
            VariationType type = VariationType.fromName(function.name);
            RgbColor color = colorFrom(function.color, index);
            double colorIndex = function.colorIndex != null ? function.colorIndex : Math.min(0.99, index / 12.0);
            AffineParams localAffine = asAffine(function.affine);
            if (localAffine == null) {
                localAffine = AffineParams.IDENTITY;
            }
            VariationParameters parameters = VariationParameters.of(function.params);
            result.add(new VariationDefinition(type, function.weight, color, colorIndex, localAffine, parameters));
            index++;
        }
        return result.isEmpty() ? null : result;
    }

    private static Palette asPalette(JsonFractalConfig.JsonPalette palette) {
        if (palette == null || palette.colors == null || palette.colors.isEmpty()) {
            return null;
        }
        List<RgbColor> colors = new ArrayList<>();
        for (JsonFractalConfig.JsonColor color : palette.colors) {
            colors.add(RgbColor.of(
                    valueOrDefault(color.r(), 0.0),
                    valueOrDefault(color.g(), 0.0),
                    valueOrDefault(color.b(), 0.0)));
        }
        return colors.isEmpty() ? null : new Palette(colors);
    }

    private static CameraSettings asCamera(JsonFractalConfig.JsonCamera camera) {
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

    private static RgbColor colorFrom(JsonFractalConfig.JsonColor color, int paletteIndex) {
        if (color != null && color.r() != null && color.g() != null && color.b() != null) {
            return RgbColor.of(color.r(), color.g(), color.b());
        }
        float hue = paletteIndex % 12 / 12.0f;
        int rgb = java.awt.Color.HSBtoRGB(hue, 0.7f, 0.9f);
        double r = ((rgb >> 16) & 0xFF) / 255.0;
        double g = ((rgb >> 8) & 0xFF) / 255.0;
        double b = (rgb & 0xFF) / 255.0;
        return new RgbColor(r, g, b);
    }

    /** Thin wrapper for CLI-sourced values: merged using "CLI wins over everything" policy. */
    public static final class CliOverrides {
        private Integer width;
        private Integer height;
        private Long iterations;
        private Double seed;
        private Path outputPath;
        private Integer threads;
        private AffineParams affineParams;
        private List<VariationDefinition> variations;
        private Long burnIn;
        private Palette palette;
        private CameraSettings camera;
        private Double brightness;
        private Double gamma;
        private Boolean gammaCorrection;
        private Boolean logGammaCorrection;
        private Integer symmetryLevel;

        public Integer width() {
            return width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }

        public Integer height() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }

        public Long iterations() {
            return iterations;
        }

        public void setIterations(Long iterations) {
            this.iterations = iterations;
        }

        public Double seed() {
            return seed;
        }

        public void setSeed(Double seed) {
            this.seed = seed;
        }

        public Path outputPath() {
            return outputPath;
        }

        public void setOutputPath(Path outputPath) {
            this.outputPath = outputPath;
        }

        public Integer threads() {
            return threads;
        }

        public void setThreads(Integer threads) {
            this.threads = threads;
        }

        public AffineParams affineParams() {
            return affineParams;
        }

        public void setAffineParams(AffineParams affineParams) {
            this.affineParams = affineParams;
        }

        public List<VariationDefinition> variations() {
            return variations;
        }

        public void setVariations(List<VariationDefinition> variations) {
            this.variations = variations;
        }

        public Palette palette() {
            return palette;
        }

        public void setPalette(Palette palette) {
            this.palette = palette;
        }

        public CameraSettings camera() {
            return camera;
        }

        public void setCamera(CameraSettings camera) {
            this.camera = camera;
        }

        public Double brightness() {
            return brightness;
        }

        public void setBrightness(Double brightness) {
            this.brightness = brightness;
        }

        public Long burnIn() {
            return burnIn;
        }

        public void setBurnIn(Long burnIn) {
            this.burnIn = burnIn;
        }

        public Double gamma() {
            return gamma;
        }

        public void setGamma(Double gamma) {
            this.gamma = gamma;
        }

        public Boolean gammaCorrection() {
            return gammaCorrection;
        }

        public void setGammaCorrection(Boolean gammaCorrection) {
            this.gammaCorrection = gammaCorrection;
        }

        public Boolean logGammaCorrection() {
            return logGammaCorrection;
        }

        public void setLogGammaCorrection(Boolean logGammaCorrection) {
            this.logGammaCorrection = logGammaCorrection;
        }

        public Integer symmetryLevel() {
            return symmetryLevel;
        }

        public void setSymmetryLevel(Integer symmetryLevel) {
            this.symmetryLevel = symmetryLevel;
        }
    }

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
            variations.add(new VariationDefinition(
                    type,
                    weight,
                    colorFrom(null, index),
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
