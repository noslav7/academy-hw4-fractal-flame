package academy.render;

import static org.junit.jupiter.api.Assertions.assertEquals;

import academy.camera.CameraSettings;
import academy.color.Palette;
import academy.color.RgbColor;
import academy.config.AffineParams;
import academy.config.FractalConfig;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.List;

/** Базовые настройки для тестов рендера. */
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

    protected BufferedImage buildExpectedImage(int width, int height, int backgroundArgb, Pixel... pixels) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                image.setRGB(x, y, backgroundArgb);
            }
        }
        for (Pixel pixel : pixels) {
            image.setRGB(pixel.x(), pixel.y(), pixel.argb());
        }
        return image;
    }

    protected void assertImageEquals(BufferedImage expected, BufferedImage actual) {
        assertEquals(expected.getWidth(), actual.getWidth(), "Image width mismatch");
        assertEquals(expected.getHeight(), actual.getHeight(), "Image height mismatch");
        for (int y = 0; y < expected.getHeight(); y++) {
            for (int x = 0; x < expected.getWidth(); x++) {
                int expectedRgb = expected.getRGB(x, y);
                int actualRgb = actual.getRGB(x, y);
                assertEquals(expectedRgb, actualRgb, "Pixel mismatch at (" + x + "," + y + ")");
            }
        }
    }

    protected record Pixel(int x, int y, int argb) {}
}
