package academy.camera;

import academy.config.FractalConfig;
import academy.math.MutablePoint;
import academy.math.Point;
import academy.variation.VariationDefinition;
import academy.variation.VariationSelector;
import java.util.SplittableRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ViewportFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ViewportFactory.class);
    private static final double MAX_FIT_RADIUS = 25.0;
    private static final double MAX_CENTER_DISTANCE = 50.0;

    private ViewportFactory() {}

    public static Viewport create(FractalConfig config) {
        CameraSettings camera = config.camera();
        if (camera.autoFit()) {
            Viewport fitted = tryAutoFit(config, camera);
            if (fitted != null) {
                return fitted;
            }
            LOGGER.warn("Auto-fit failed, falling back to default framing");
        }
        double baseScale = camera.scale() * (config.width() / 3.0);
        return new Viewport(config.width(), config.height(), camera.centerX(), camera.centerY(), baseScale, camera.rotationDegrees());
    }

    private static Viewport tryAutoFit(FractalConfig config, CameraSettings camera) {
        BoundingBox bounds = sampleBoundingBox(config, camera.fitSamples());
        if (bounds == null || bounds.isEmpty()) {
            return null;
        }
        double margin = camera.fitMargin();
        double spanX = Math.max(1e-6, bounds.width() * (1.0 + margin));
        double spanY = Math.max(1e-6, bounds.height() * (1.0 + margin));
        double scaleX = config.width() / spanX;
        double scaleY = config.height() / spanY;
        double scale = Math.min(scaleX, scaleY) * camera.scale();
        double minScale = config.width() / 200.0;
        if (scale < minScale) {
            LOGGER.warn("Auto-fit scale {} is too small (threshold {}), fallback triggered", scale, minScale);
            return null;
        }
        double centerX = clamp(bounds.centerX(), MAX_CENTER_DISTANCE);
        double centerY = clamp(bounds.centerY(), MAX_CENTER_DISTANCE);
        LOGGER.atInfo()
                .addKeyValue("centerX", centerX)
                .addKeyValue("centerY", centerY)
                .addKeyValue("scale", scale)
                .addKeyValue("width", config.width())
                .addKeyValue("height", config.height())
                .log("Viewport auto-fit parameters");
        return new Viewport(config.width(), config.height(), centerX, centerY, scale, camera.rotationDegrees());
    }

    private static BoundingBox sampleBoundingBox(FractalConfig config, long samples) {
        SplittableRandom random = new SplittableRandom(Double.doubleToLongBits(config.seed()));
        VariationSelector selector = new VariationSelector(config.variations());
        MutablePoint working = new MutablePoint(0.0, 0.0);
        Point current = new Point(random.nextDouble(-1.0, 1.0), random.nextDouble(-1.0, 1.0));
        BoundingBox box = new BoundingBox();
        long burnIn = Math.min(config.burnInIterations(), samples / 2);
        long skipped = 0;
        for (long i = 0; i < samples; i++) {
            VariationDefinition variation = selector.pick(random.nextDouble());
            Point affinePoint = config.affineParams().apply(current, working).toImmutable();
            current = variation.type().apply(affinePoint, variation, random);
            if (i > burnIn && isWithinBounds(current)) {
                box.include(current);
            } else {
                skipped++;
            }
        }
        if (box.isEmpty()) {
            LOGGER.warn("Auto-fit bounding box was empty after sampling");
            return null;
        }
        if (skipped > samples / 2) {
            LOGGER.warn(
                    "Auto-fit skipped {} samples as outliers (>{}); consider adjusting affine params",
                    skipped,
                    MAX_FIT_RADIUS);
        }
        return box;
    }

    private static boolean isWithinBounds(Point point) {
        if (!Double.isFinite(point.x()) || !Double.isFinite(point.y())) {
            return false;
        }
        return Math.hypot(point.x(), point.y()) <= MAX_FIT_RADIUS;
    }

    private static double clamp(double value, double maxAbs) {
        if (value > maxAbs) return maxAbs;
        if (value < -maxAbs) return -maxAbs;
        return value;
    }

}

