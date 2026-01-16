package academy.render;

import academy.camera.CameraSettings;
import academy.color.Palette;
import academy.color.RgbColor;
import academy.config.AffineParams;
import academy.config.FractalConfig;
import academy.variation.VariationDefinition;
import academy.variation.VariationType;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.List;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Тесты рендера и сохранения изображения. */
class FractalRendererTest extends BaseRenderTest {

    /** Проверяет, что PNG записывается на диск. */
    @Test
    void givenValidConfigWhenRenderThenPngIsWritten(@TempDir Path tempDir) throws Exception {
        Path output = tempDir.resolve("flame.png");
        RgbColor red = RgbColor.of(1.0, 0.0, 0.0);
        VariationDefinition variation =
                new VariationDefinition(VariationType.LINEAR, 1.0, red, 0.0, AffineParams.IDENTITY);
        FractalConfig config = baseConfig(output, 42.0, 1)
                .width(8)
                .height(8)
                .iterationCount(10L)
                .affineParams(new AffineParams(0.0, 0.0, 0.0, 0.0, 0.0, 0.0))
                .variations(List.of(variation))
                .palette(new Palette(List.of(red)))
                .camera(new CameraSettings(0.0, 0.0, 1.0, 0.0, false, 0.1, 10_000L))
                .build();

        new FractalRenderer().render(config);

        BufferedImage actual = ImageIO.read(output.toFile());
        int background = new RgbColor(0.0, 0.0, 0.0).toArgb(1.0);
        int expectedColor = red.toArgb(1.0);
        BufferedImage expected = buildExpectedImage(8, 8, background, new Pixel(4, 4, expectedColor));
        assertImageEquals(expected, actual);
    }

    /** Проверяет поведение симметрии (дублирование точек). */
    @Test
    void givenSymmetryLevelWhenRenderThenRotatesPointCopies(@TempDir Path tempDir) throws Exception {
        Path output = tempDir.resolve("symmetry.png");
        RgbColor red = RgbColor.of(1.0, 0.0, 0.0);
        VariationDefinition variation =
                new VariationDefinition(VariationType.LINEAR, 1.0, red, 0.0, AffineParams.IDENTITY);

        FractalConfig config = baseConfig(output, 1.0, 1)
                .width(32)
                .height(32)
                .iterationCount(10L)
                // Force all iterations to land at (0.5, 0) for deterministic symmetry
                .affineParams(new AffineParams(0.0, 0.0, 0.5, 0.0, 0.0, 0.0))
                .variations(List.of(variation))
                .palette(new Palette(List.of(red)))
                .symmetryLevel(4)
                .camera(new CameraSettings(0.0, 0.0, 1.0, 0.0, false, 0.1, 200_000L))
                .build();

        new FractalRenderer().render(config);

        BufferedImage actual = ImageIO.read(output.toFile());
        int background = new RgbColor(0.0, 0.0, 0.0).toArgb(1.0);
        int expectedColor = red.toArgb(1.0);
        double scale = 1.0 * (32 / 3.0);
        Pixel[] expectedPixels = new Pixel[4];
        double angleStep = 2 * Math.PI / 4;
        for (int i = 0; i < 4; i++) {
            double angle = i * angleStep;
            double rotatedX = 0.5 * Math.cos(angle);
            double rotatedY = 0.5 * Math.sin(angle);
            expectedPixels[i] = projectToPixel(rotatedX, rotatedY, 32, 32, scale, expectedColor);
        }
        BufferedImage expected = buildExpectedImage(32, 32, background, expectedPixels);
        assertImageEquals(expected, actual);
    }

    /** Проверяет, что палитра применяется при рендеринге. */
    @Test
    void givenSingleColorPaletteWhenRenderThenImageMatchesPalette(@TempDir Path tempDir) throws Exception {
        Path output = tempDir.resolve("palette.png");
        RgbColor red = RgbColor.of(1.0, 0.0, 0.0);
        VariationDefinition variation =
                new VariationDefinition(VariationType.LINEAR, 1.0, red, 0.0, AffineParams.IDENTITY);

        FractalConfig config = baseConfig(output, 2.0, 1)
                .width(24)
                .height(24)
                .iterationCount(50L)
                .affineParams(new AffineParams(0.0, 0.0, 0.4, 0.0, 0.0, 0.0))
                .variations(List.of(variation))
                .palette(new Palette(List.of(red)))
                .build();

        new FractalRenderer().render(config);

        BufferedImage actual = ImageIO.read(output.toFile());
        int background = new RgbColor(0.0, 0.0, 0.0).toArgb(1.0);
        int expectedColor = red.toArgb(1.0);
        double scale = 1.0 * (24 / 3.0);
        Pixel expectedPixel = projectToPixel(0.4, 0.0, 24, 24, scale, expectedColor);
        BufferedImage expected = buildExpectedImage(24, 24, background, expectedPixel);
        assertImageEquals(expected, actual);
    }

    private Pixel projectToPixel(double x, double y, int width, int height, double scale, int argb) {
        double screenX = width / 2.0 + x * scale;
        double screenY = height / 2.0 - y * scale;
        return new Pixel((int) screenX, (int) screenY, argb);
    }
}
