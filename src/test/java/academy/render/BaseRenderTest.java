package academy.render;

import academy.camera.CameraSettings;
import academy.color.Palette;
import academy.color.RgbColor;
import academy.config.AffineParams;
import academy.config.FractalConfig;
import java.nio.file.Path;
import java.util.List;

/**
 * Базовые настройки для тестов рендера.
 */
abstract class BaseRenderTest {
    protected FractalConfig.Builder baseConfig(Path output, double seed, int threads) {
        return FractalConfig.builder()
                .width(64)
                .height(64)
                .iterationCount(5_000L)
                .threads(threads)
                .seed(seed)
                .outputPath(output)
                .affineParams(AffineParams.IDENTITY)
                .burnInIterations(0L)
                .palette(new Palette(List.of(RgbColor.of(1.0, 0.5, 0.0))))
                .gammaCorrection(false)
                .logGammaCorrection(false)
                .symmetryLevel(1)
                .camera(new CameraSettings(0.0, 0.0, 1.0, 0.0, false, 0.1, 10_000L));
    }
}
