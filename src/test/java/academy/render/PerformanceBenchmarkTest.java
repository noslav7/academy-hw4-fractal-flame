package academy.render;

import static org.junit.jupiter.api.Assertions.assertTrue;

import academy.color.Palette;
import academy.color.RgbColor;
import academy.config.AffineParams;
import academy.config.FractalConfig;
import academy.variation.VariationDefinition;
import academy.variation.VariationType;
import java.nio.file.Path;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Небольшой бенчмарк для оценки многопоточности.
 */
class PerformanceBenchmarkTest {

    /**
     * Проверяет рендер при разных количествах потоков.
     */
    @Test
    void givenThreadCountsWhenRenderThenRecordDurations(@TempDir Path tempDir) throws Exception {
        VariationDefinition variation = new VariationDefinition(
                VariationType.LINEAR, 1.0, RgbColor.of(1.0, 0.5, 0.0), 0.0, AffineParams.IDENTITY);
        FractalRenderer renderer = new FractalRenderer();
        Map<Integer, Long> durationsMillis = new LinkedHashMap<>();

        for (int threads : List.of(1, 2, 4, 8)) {
            Path output = tempDir.resolve("bench-" + threads + ".png");
            FractalConfig config = FractalConfig.builder()
                    .width(64)
                    .height(64)
                    .iterationCount(5_000L)
                    .threads(threads)
                    .seed(123.0)
                    .outputPath(output)
                    .affineParams(AffineParams.IDENTITY)
                    .variations(List.of(variation))
                    .burnInIterations(0L)
                    .palette(new Palette(List.of(RgbColor.of(1.0, 0.5, 0.0))))
                    .gammaCorrection(false)
                    .logGammaCorrection(false)
                    .symmetryLevel(1)
                    .build();

            long start = System.nanoTime();
            renderer.render(config);
            long duration = Duration.ofNanos(System.nanoTime() - start).toMillis();
            durationsMillis.put(threads, duration);

            assertTrue(output.toFile().exists(), "Benchmark output should exist for threads=" + threads);
        }

        System.out.println("Multithreading benchmark durations (ms): " + durationsMillis);
        // Soft sanity check: higher parallelism should not be dramatically slower than single-thread.
        assertTrue(
                durationsMillis.get(8) <= durationsMillis.get(1) * 5,
                "8-thread render unexpectedly slower than single-thread baseline");
    }
}
