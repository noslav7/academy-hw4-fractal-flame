package academy.render;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import academy.color.Palette;
import academy.color.RgbColor;
import academy.config.AffineParams;
import academy.config.FractalConfig;
import academy.camera.CameraSettings;
import academy.variation.VariationDefinition;
import academy.variation.VariationType;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Тесты рендера и сохранения изображения.
 */
class FractalRendererTest extends BaseRenderTest {

    /**
     * Проверяет, что PNG записывается на диск.
     */
    @Test
    void givenValidConfigWhenRenderThenPngIsWritten(@TempDir Path tempDir) throws Exception {
        Path output = tempDir.resolve("flame.png");
        FractalConfig config = baseConfig(output, 42.0, 2)
                .affineParams(new AffineParams(0.8, 0.0, 0.0, 0.0, 0.8, 0.0))
                .burnInIterations(500L)
                .gamma(2.2)
                .gammaCorrection(true)
                .build();

        new FractalRenderer().render(config);

        assertAll(
                () -> assertTrue(Files.exists(output), "Rendered image file should exist"),
                () -> assertTrue(Files.size(output) > 0L, "Rendered image file should not be empty"));
    }

    /**
     * Проверяет поведение симметрии (дублирование точек).
     */
    @Test
    void givenSymmetryLevelWhenRenderThenRotatesPointCopies(@TempDir Path tempDir) throws Exception {
        Path output = tempDir.resolve("symmetry.png");
        VariationDefinition variation = new VariationDefinition(
                VariationType.LINEAR, 1.0, RgbColor.of(1.0, 0.0, 0.0), 0.0, AffineParams.IDENTITY);

        FractalConfig config = baseConfig(output, 1.0, 1)
                .width(32)
                .height(32)
                .iterationCount(10L)
                // Force all iterations to land at (0.5, 0) for deterministic symmetry
                .affineParams(new AffineParams(0.0, 0.0, 0.5, 0.0, 0.0, 0.0))
                .variations(List.of(variation))
                .palette(new Palette(List.of(RgbColor.of(1.0, 0.0, 0.0))))
                .symmetryLevel(4)
                .camera(new CameraSettings(0.0, 0.0, 1.0, 0.0, false, 0.1, 200_000L))
                .build();

        new FractalRenderer().render(config);

        BufferedImage image = ImageIO.read(output.toFile());
        int background = new RgbColor(0.0, 0.0, 0.0).toArgb(1.0);
        int nonBlackPixels = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (image.getRGB(x, y) != background) {
                    nonBlackPixels++;
                }
            }
        }

        assertEquals(4, nonBlackPixels, "Symmetry should replicate a point four times");
    }

    /**
     * Проверяет, что палитра применяется при рендеринге.
     */
    @Test
    void givenSingleColorPaletteWhenRenderThenImageMatchesPalette(@TempDir Path tempDir) throws Exception {
        Path output = tempDir.resolve("palette.png");
        RgbColor red = RgbColor.of(1.0, 0.0, 0.0);
        VariationDefinition variation =
                new VariationDefinition(VariationType.LINEAR, 1.0, red, 0.0, AffineParams.IDENTITY);

        FractalConfig config = baseConfig(output, 2.0, 1)
                .width(24)
                .height(24)
                .iterationCount(500L)
                .affineParams(new AffineParams(0.0, 0.0, 0.4, 0.0, 0.0, 0.0))
                .variations(List.of(variation))
                .palette(new Palette(List.of(red)))
                .build();

        new FractalRenderer().render(config);

        BufferedImage image = ImageIO.read(output.toFile());
        int expected = red.toArgb(1.0);
        int redPixels = 0;
        int otherColors = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                if (rgb == expected) {
                    redPixels++;
                } else if (rgb != new RgbColor(0.0, 0.0, 0.0).toArgb(1.0)) {
                    otherColors++;
                }
            }
        }

        assertTrue(redPixels > 0, "Rendered image should contain palette color");
        assertEquals(0, otherColors, "Only palette colors should appear in the image");
    }
}
