package academy.config;

import academy.camera.CameraSettings;
import academy.color.Palette;
import academy.variation.VariationDefinition;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** Effective configuration used by the renderer after merging defaults, JSON config and CLI overrides. */
public record FractalConfig(
        int width,
        int height,
        long iterationCount,
        double seed,
        Path outputPath,
        int threads,
        AffineParams affineParams,
        List<VariationDefinition> variations,
        long burnInIterations,
        Palette palette,
        CameraSettings camera,
        double brightness,
        double gamma,
        boolean gammaCorrection,
        boolean logGammaCorrection,
        int symmetryLevel) {

    public FractalConfig {
        Objects.requireNonNull(outputPath, "outputPath");
        Objects.requireNonNull(affineParams, "affineParams");
        Objects.requireNonNull(variations, "variations");
        Objects.requireNonNull(palette, "palette");
        Objects.requireNonNull(camera, "camera");
        if (width <= 0 || height <= 0) throw new IllegalArgumentException("Image size must be positive");
        if (iterationCount <= 0) throw new IllegalArgumentException("Iterations must be positive");
        if (threads <= 0) throw new IllegalArgumentException("Threads must be positive");
        if (burnInIterations < 0) throw new IllegalArgumentException("Burn-in must be >= 0");
        if (brightness <= 0.0) throw new IllegalArgumentException("Brightness must be > 0");
        if (gamma <= 0.0) throw new IllegalArgumentException("Gamma must be > 0");
        if (symmetryLevel < 1) throw new IllegalArgumentException("Symmetry level must be >= 1");
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return builder()
                .width(width)
                .height(height)
                .iterationCount(iterationCount)
                .seed(seed)
                .outputPath(outputPath)
                .threads(threads)
                .affineParams(affineParams)
                .variations(variations)
                .burnInIterations(burnInIterations)
                .palette(palette)
                .camera(camera)
                .brightness(brightness)
                .gamma(gamma)
                .gammaCorrection(gammaCorrection)
                .logGammaCorrection(logGammaCorrection)
                .symmetryLevel(symmetryLevel);
    }

    public static FractalConfig defaults() {
        return builder().build();
    }

    public static final class Builder {
        private Integer width;
        private Integer height;
        private Long iterationCount;
        private Double seed;
        private Path outputPath;
        private Integer threads;
        private AffineParams affineParams;
        private List<VariationDefinition> variations;
        private Long burnInIterations;
        private Palette palette;
        private CameraSettings camera;
        private Double brightness;
        private Double gamma;
        private Boolean gammaCorrection;
        private Boolean logGammaCorrection;
        private Integer symmetryLevel;

        private static final int DEFAULT_WIDTH = 1920;
        private static final int DEFAULT_HEIGHT = 1080;
        private static final long DEFAULT_ITERATIONS = 2500;
        private static final double DEFAULT_SEED = 5.1234;
        private static final Path DEFAULT_OUTPUT = Path.of("result.png");
        private static final int DEFAULT_THREADS = 1;
        private static final long DEFAULT_BURN_IN = 100;
        private static final Palette DEFAULT_PALETTE = Palette.defaultPalette();
        private static final CameraSettings DEFAULT_CAMERA = CameraSettings.DEFAULT;
        private static final double DEFAULT_BRIGHTNESS = 1.0;
        private static final double DEFAULT_GAMMA = 2.2;
        private static final boolean DEFAULT_GAMMA_CORRECTION = false;
        private static final boolean DEFAULT_LOG_GAMMA_CORRECTION = true;
        private static final int DEFAULT_SYMMETRY = 1;

        public Builder width(Integer value) {
            this.width = value;
            return this;
        }

        public Builder height(Integer value) {
            this.height = value;
            return this;
        }

        public Builder iterationCount(Long value) {
            this.iterationCount = value;
            return this;
        }

        public Builder seed(Double value) {
            this.seed = value;
            return this;
        }

        public Builder outputPath(Path value) {
            this.outputPath = value;
            return this;
        }

        public Builder threads(Integer value) {
            this.threads = value;
            return this;
        }

        public Builder affineParams(AffineParams value) {
            this.affineParams = value;
            return this;
        }

        public Builder variations(List<VariationDefinition> value) {
            if (value == null) {
                this.variations = null;
            } else {
                this.variations = new ArrayList<>(value);
            }
            return this;
        }

        public Builder burnInIterations(Long value) {
            this.burnInIterations = value;
            return this;
        }

        public Builder palette(Palette value) {
            this.palette = value;
            return this;
        }

        public Builder camera(CameraSettings value) {
            this.camera = value;
            return this;
        }

        public Builder brightness(Double value) {
            this.brightness = value;
            return this;
        }

        public Builder gamma(Double value) {
            this.gamma = value;
            return this;
        }

        public Builder gammaCorrection(Boolean value) {
            this.gammaCorrection = value;
            return this;
        }

        public Builder logGammaCorrection(Boolean value) {
            this.logGammaCorrection = value;
            return this;
        }

        public Builder symmetryLevel(Integer value) {
            this.symmetryLevel = value;
            return this;
        }

        public FractalConfig build() {
            int finalWidth = width != null ? width : DEFAULT_WIDTH;
            int finalHeight = height != null ? height : DEFAULT_HEIGHT;
            long finalIterations = iterationCount != null ? iterationCount : DEFAULT_ITERATIONS;
            double finalSeed = seed != null ? seed : DEFAULT_SEED;
            Path finalOutput = outputPath != null ? outputPath : DEFAULT_OUTPUT;
            int finalThreads = threads != null ? threads : DEFAULT_THREADS;
            AffineParams finalAffine = affineParams != null ? affineParams : AffineParams.IDENTITY;
            List<VariationDefinition> finalVariations =
                    variations != null ? List.copyOf(variations) : DefaultVariations.create();
            long finalBurnIn = burnInIterations != null ? burnInIterations : DEFAULT_BURN_IN;
            Palette finalPalette = palette != null ? palette : DEFAULT_PALETTE;
            CameraSettings finalCamera = camera != null ? camera : DEFAULT_CAMERA;
            double finalBrightness = brightness != null ? brightness : DEFAULT_BRIGHTNESS;
            double finalGamma = gamma != null ? gamma : DEFAULT_GAMMA;
            boolean finalGammaCorrection = gammaCorrection != null ? gammaCorrection : DEFAULT_GAMMA_CORRECTION;
            boolean finalLogGammaCorrection =
                    logGammaCorrection != null ? logGammaCorrection : DEFAULT_LOG_GAMMA_CORRECTION;
            int finalSymmetry = symmetryLevel != null ? symmetryLevel : DEFAULT_SYMMETRY;
            return new FractalConfig(
                    finalWidth,
                    finalHeight,
                    finalIterations,
                    finalSeed,
                    finalOutput,
                    finalThreads,
                    finalAffine,
                    finalVariations,
                    finalBurnIn,
                    finalPalette,
                    finalCamera,
                    finalBrightness,
                    finalGamma,
                    finalGammaCorrection,
                    finalLogGammaCorrection,
                    finalSymmetry);
        }
    }

    private static final class DefaultVariations {
        private static List<VariationDefinition> create() {
            return Collections.unmodifiableList(VariationFactory.defaultVariations());
        }
    }
}
