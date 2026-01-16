package academy.config;

import academy.camera.CameraSettings;
import academy.color.Palette;
import academy.variation.VariationDefinition;
import java.nio.file.Path;
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
        delegate.affineParams(JsonConfigMapper.toAffine(jsonConfig.affineParams));
        delegate.variations(JsonConfigMapper.toVariations(jsonConfig.functions));
        delegate.burnInIterations(jsonConfig.burnIn);
        delegate.palette(JsonConfigMapper.toPalette(jsonConfig.palette));
        delegate.camera(JsonConfigMapper.toCamera(jsonConfig.camera));
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

}
