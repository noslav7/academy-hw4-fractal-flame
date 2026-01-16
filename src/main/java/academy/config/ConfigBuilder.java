package academy.config;

import academy.camera.CameraSettings;
import academy.color.Palette;
import academy.variation.VariationDefinition;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * Сборщик конфигурации: объединяет дефолты, JSON-конфиг и CLI-переопределения.
 */
public final class ConfigBuilder {

    private final FractalConfig.Builder delegate = FractalConfig.builder();

    /**
     * Применяет значения из JSON-конфига.
     *
     * @param jsonConfig конфигурация из JSON
     * @return текущий сборщик
     */
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

    /**
     * Применяет значения из CLI поверх имеющихся.
     *
     * @param overrides значения, заданные в CLI
     * @return текущий сборщик
     */
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

    /**
     * Строит финальную конфигурацию.
     *
     * @return итоговая конфигурация
     */
    public FractalConfig build() {
        return delegate.build();
    }


    /**
     * Контейнер CLI-переопределений: применяется по принципу "CLI важнее всего".
     */
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

        /** @return ширина, заданная в CLI. */
        public Integer width() {
            return width;
        }

        /** @param width ширина изображения */
        public void setWidth(Integer width) {
            this.width = width;
        }

        /** @return высота, заданная в CLI. */
        public Integer height() {
            return height;
        }

        /** @param height высота изображения */
        public void setHeight(Integer height) {
            this.height = height;
        }

        /** @return количество итераций, заданное в CLI. */
        public Long iterations() {
            return iterations;
        }

        /** @param iterations число итераций */
        public void setIterations(Long iterations) {
            this.iterations = iterations;
        }

        /** @return seed генератора */
        public Double seed() {
            return seed;
        }

        /** @param seed seed генератора */
        public void setSeed(Double seed) {
            this.seed = seed;
        }

        /** @return путь к выходному файлу */
        public Path outputPath() {
            return outputPath;
        }

        /** @param outputPath путь к выходному файлу */
        public void setOutputPath(Path outputPath) {
            this.outputPath = outputPath;
        }

        /** @return число потоков */
        public Integer threads() {
            return threads;
        }

        /** @param threads число потоков */
        public void setThreads(Integer threads) {
            this.threads = threads;
        }

        /** @return глобальные аффинные параметры */
        public AffineParams affineParams() {
            return affineParams;
        }

        /** @param affineParams глобальные аффинные параметры */
        public void setAffineParams(AffineParams affineParams) {
            this.affineParams = affineParams;
        }

        /** @return список вариаций */
        public List<VariationDefinition> variations() {
            return variations;
        }

        /** @param variations список вариаций */
        public void setVariations(List<VariationDefinition> variations) {
            this.variations = variations;
        }

        /** @return палитра */
        public Palette palette() {
            return palette;
        }

        /** @param palette палитра */
        public void setPalette(Palette palette) {
            this.palette = palette;
        }

        /** @return настройки камеры */
        public CameraSettings camera() {
            return camera;
        }

        /** @param camera настройки камеры */
        public void setCamera(CameraSettings camera) {
            this.camera = camera;
        }

        /** @return яркость */
        public Double brightness() {
            return brightness;
        }

        /** @param brightness яркость */
        public void setBrightness(Double brightness) {
            this.brightness = brightness;
        }

        /** @return количество итераций прогрева */
        public Long burnIn() {
            return burnIn;
        }

        /** @param burnIn количество итераций прогрева */
        public void setBurnIn(Long burnIn) {
            this.burnIn = burnIn;
        }

        /** @return гамма */
        public Double gamma() {
            return gamma;
        }

        /** @param gamma гамма */
        public void setGamma(Double gamma) {
            this.gamma = gamma;
        }

        /** @return флаг гамма-коррекции */
        public Boolean gammaCorrection() {
            return gammaCorrection;
        }

        /** @param gammaCorrection флаг гамма-коррекции */
        public void setGammaCorrection(Boolean gammaCorrection) {
            this.gammaCorrection = gammaCorrection;
        }

        /** @return флаг логарифмической гамма-коррекции */
        public Boolean logGammaCorrection() {
            return logGammaCorrection;
        }

        /** @param logGammaCorrection флаг логарифмической гамма-коррекции */
        public void setLogGammaCorrection(Boolean logGammaCorrection) {
            this.logGammaCorrection = logGammaCorrection;
        }

        /** @return уровень симметрии */
        public Integer symmetryLevel() {
            return symmetryLevel;
        }

        /** @param symmetryLevel уровень симметрии */
        public void setSymmetryLevel(Integer symmetryLevel) {
            this.symmetryLevel = symmetryLevel;
        }
    }

}
