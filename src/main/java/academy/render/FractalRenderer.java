package academy.render;

import academy.camera.Viewport;
import academy.camera.ViewportFactory;
import academy.color.RgbColor;
import academy.config.FractalConfig;
import academy.math.MutablePoint;
import academy.math.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FractalRenderer {

    private static final Logger LOGGER = LoggerFactory.getLogger(FractalRenderer.class);

    public void render(FractalConfig config) {
        Instant start = Instant.now();
        LOGGER.atInfo()
                .addKeyValue("width", config.width())
                .addKeyValue("height", config.height())
                .addKeyValue("iterations", config.iterationCount())
                .addKeyValue("threads", config.threads())
                .log("Starting render");

        Viewport viewport = ViewportFactory.create(config);
        ExecutorService executor = Executors.newFixedThreadPool(config.threads());
        List<Future<Histogram>> futures = new ArrayList<>();
        long iterationsPerThread = config.iterationCount() / config.threads();
        long remainder = config.iterationCount() % config.threads();
        ProgressTracker tracker = new ProgressTracker(config.iterationCount());
        for (int i = 0; i < config.threads(); i++) {
            long iterations = iterationsPerThread + (i < remainder ? 1 : 0);
            int workerIndex = i;
            futures.add(
                    executor.submit(
                            new Worker(
                                    config,
                                    iterations,
                                    tracker,
                                    viewport,
                                    Double.doubleToLongBits(config.seed()) + workerIndex * 997)));
        }
        executor.shutdown();

        Histogram merged = new Histogram(viewport.width(), viewport.height());
        for (Future<Histogram> future : futures) {
            try {
                merged.merge(future.get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Render interrupted", e);
            } catch (ExecutionException e) {
                throw new IllegalStateException("Render failed", e.getCause());
            }
        }

        BufferedImage image = merged.toImage(config.gamma(), config.gammaCorrection(), config.brightness());
        writeImage(config.outputPath(), image);

        Duration duration = Duration.between(start, Instant.now());
        LOGGER.atInfo()
                .addKeyValue("durationSeconds", duration.toMillis() / 1000.0)
                .addKeyValue("output", config.outputPath())
                .log("Render completed");
    }

    private static void writeImage(Path target, BufferedImage image) {
        try {
            Path parent = target.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            ImageIO.write(image, "PNG", target.toFile());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write image: " + target, e);
        }
    }

    private static final class Worker implements Callable<Histogram> {
        private final FractalConfig config;
        private final long iterations;
        private final ProgressTracker tracker;
        private final SplittableRandom random;
        private final Viewport viewport;

        private Worker(
                FractalConfig config, long iterations, ProgressTracker tracker, Viewport viewport, long seed) {
            this.config = config;
            this.iterations = iterations;
            this.tracker = tracker;
            this.viewport = viewport;
            this.random = new SplittableRandom(seed);
        }

        @Override
        public Histogram call() {
            Histogram histogram = new Histogram(viewport.width(), viewport.height());
            MutablePoint globalAffinePoint = new MutablePoint(0.0, 0.0);
            MutablePoint localAffinePoint = new MutablePoint(0.0, 0.0);
            Point currentPoint = new Point(random.nextDouble(-1.0, 1.0), random.nextDouble(-1.0, 1.0));
            double colorIndex = random.nextDouble();
            VariationSelector selector = new VariationSelector(config.variations());
            long burnIn = config.burnInIterations();
            long processed = 0;
            long plotted = 0;
            for (long iteration = 0; iteration < iterations; iteration++) {
                VariationSelection selection = selector.pick(random.nextDouble());
                Point afterGlobal = config.affineParams().apply(currentPoint, globalAffinePoint).toImmutable();
                Point afterLocal =
                        selection.definition().localAffine().apply(afterGlobal, localAffinePoint).toImmutable();
                currentPoint =
                        selection.definition()
                                .type()
                                .apply(afterLocal, selection.definition(), random);
                colorIndex = (colorIndex + selection.definition().colorIndex()) * 0.5;
                RgbColor paletteColor = config.palette().sample(colorIndex);

                if (iteration > burnIn) {
                    plotWithSymmetry(histogram, currentPoint, paletteColor);
                    plotted++;
                }

                processed++;
                if (processed % 50_000 == 0) {
                    tracker.increment(50_000);
                }
            }
            tracker.increment(processed % 50_000);
            if (plotted == 0) {
                LOGGER.warn("Worker rendered 0 visible points (viewport may be misconfigured)");
            }
            return histogram;
        }

        private void plotWithSymmetry(Histogram histogram, Point point, RgbColor color) {
            int symmetry = config.symmetryLevel();
            if (symmetry <= 1) {
                plotSingle(histogram, point, color);
                return;
            }
            double angleStep = 2 * Math.PI / symmetry;
            for (int i = 0; i < symmetry; i++) {
                double angle = i * angleStep;
                double rotatedX = point.x() * Math.cos(angle) - point.y() * Math.sin(angle);
                double rotatedY = point.x() * Math.sin(angle) + point.y() * Math.cos(angle);
                plotSingle(histogram, new Point(rotatedX, rotatedY), color);
            }
        }

        private void plotSingle(Histogram histogram, Point point, RgbColor color) {
            Viewport.ScreenPoint screen = viewport.project(point);
            if (screen.isInside()) {
                histogram.addPoint(screen.ix(), screen.iy(), color);
            }
        }
    }
}

