package academy.render;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import academy.config.AffineParams;
import academy.config.FractalConfig;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FractalRendererTest {

    @Test
    void givenValidConfigWhenRenderThenPngIsWritten(@TempDir Path tempDir) throws Exception {
        Path output = tempDir.resolve("flame.png");
        FractalConfig config =
                FractalConfig.builder()
                        .width(64)
                        .height(64)
                        .iterationCount(5_000L)
                        .threads(2)
                        .seed(42.0)
                        .outputPath(output)
                        .affineParams(new AffineParams(0.8, 0.0, 0.0, 0.0, 0.8, 0.0))
                        .burnInIterations(500L)
                        .gamma(2.2)
                        .gammaCorrection(true)
                        .symmetryLevel(1)
                        .build();

        new FractalRenderer().render(config);

        assertAll(
                () -> assertTrue(Files.exists(output), "Rendered image file should exist"),
                () -> assertTrue(Files.size(output) > 0L, "Rendered image file should not be empty"));
    }
}

