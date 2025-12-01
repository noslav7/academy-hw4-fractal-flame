package academy.render;

import academy.config.AffineParams;
import academy.config.FractalConfig;
import java.nio.file.Files;
import java.nio.file.Path;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FractalRendererTest {

    @Test
    void renderShouldProducePng(@TempDir Path tempDir) throws Exception {
        Path output = tempDir.resolve("flame.png");
        FractalConfig config =
                FractalConfig.builder()
                        .width(320)
                        .height(240)
                        .iterationCount(20_000L)
                        .threads(2)
                        .seed(42.0)
                        .outputPath(output)
                        .affineParams(new AffineParams(0.8, 0.0, 0.0, 0.0, 0.8, 0.0))
                        .burnInIterations(1000L)
                        .gamma(2.2)
                        .gammaCorrection(true)
                        .symmetryLevel(1)
                        .build();

        new FractalRenderer().render(config);

        Assertions.assertThat(Files.exists(output)).isTrue();
        Assertions.assertThat(Files.size(output)).isGreaterThan(0L);
    }
}

