package academy.render;

import academy.color.RgbColor;
import academy.config.AffineParams;
import academy.config.FractalConfig;
import academy.variation.VariationDefinition;
import academy.variation.VariationType;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PerformanceBenchmarkTest extends BaseRenderTest {

    /** Проверяет, что рендер даёт ожидаемое изображение при разных количествах потоков. */
    @Test
    void givenThreadCountsWhenRenderThenImageMatchesExpected(@TempDir Path tempDir) throws Exception {
        RgbColor red = RgbColor.of(1.0, 0.0, 0.0);
        VariationDefinition variation =
                new VariationDefinition(VariationType.LINEAR, 1.0, red, 0.0, AffineParams.IDENTITY);
        int width = 16;
        int height = 16;
        int background = new RgbColor(0.0, 0.0, 0.0).toArgb(1.0);
        int expectedColor = red.toArgb(1.0);
        BufferedImage expected = buildExpectedImage(width, height, background, new Pixel(8, 8, expectedColor));

        for (int threads : List.of(1, 2, 4, 8)) {
            Path output = tempDir.resolve("bench-" + threads + ".png");
            FractalConfig config = baseConfig(output, 123.0, threads)
                    .width(width)
                    .height(height)
                    .iterationCount(10L)
                    .affineParams(new AffineParams(0.0, 0.0, 0.0, 0.0, 0.0, 0.0))
                    .variations(List.of(variation))
                    .palette(new academy.color.Palette(List.of(red)))
                    .camera(new academy.camera.CameraSettings(0.0, 0.0, 1.0, 0.0, false, 0.1, 10_000L))
                    .build();

            new FractalRenderer().render(config);
            BufferedImage actual = javax.imageio.ImageIO.read(output.toFile());
            assertImageEquals(expected, actual);
        }
    }
}
