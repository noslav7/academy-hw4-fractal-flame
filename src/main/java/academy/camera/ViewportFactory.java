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
    private static final double MAX_FIT_RADIUS = 50.0;
    private static final double MIN_SCALE_FACTOR = 0.1; // relative to default scale

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
        return new Viewport(
                config.width(),
                config.height(),
                camera.centerX(),
                camera.centerY(),
                baseScale,
                camera.rotationDegrees());
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
        double fallbackScale = camera.scale() * (config.width() / 3.0);
        if (scale < fallbackScale * MIN_SCALE_FACTOR) {
            LOGGER.warn("Auto-fit scale {} is too small, falling back to default framing", scale);
            return null;
        }
        double centerX = bounds.centerX();
        double centerY = bounds.centerY();
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
        MutablePoint globalAffinePoint = new MutablePoint(0.0, 0.0);
        MutablePoint localAffinePoint = new MutablePoint(0.0, 0.0);
        Point current = new Point(random.nextDouble(-1.0, 1.0), random.nextDouble(-1.0, 1.0));
        BoundingBox box = new BoundingBox();
        long burnIn = Math.min(config.burnInIterations(), samples / 2);
        for (long i = 0; i < samples; i++) {
            VariationDefinition variation = selector.pick(random.nextDouble());
            Point afterGlobal =
                    config.affineParams().apply(current, globalAffinePoint).toImmutable();
            Point afterLocal =
                    variation.localAffine().apply(afterGlobal, localAffinePoint).toImmutable();
            current = variation.type().apply(afterLocal, variation, random);
            if (i >= burnIn && isWithinBounds(current)) {
                box.include(current);
            }
        }
        if (box.isEmpty()) {
            LOGGER.warn("Auto-fit bounding box was empty after sampling");
            return null;
        }
        return box;
    }

    private static boolean isWithinBounds(Point point) {
        if (!Double.isFinite(point.x()) || !Double.isFinite(point.y())) {
            return false;
        }
        return Math.hypot(point.x(), point.y()) <= MAX_FIT_RADIUS;
    }
}
