package academy.config;

import academy.math.MutablePoint;
import academy.math.Point;

/**
 * Represents the affine transformation coefficients (a, b, c, d, e, f) that are applied before any non-linear
 * variation.
 */
public record AffineParams(double a, double b, double c, double d, double e, double f) {

    public static final AffineParams IDENTITY = new AffineParams(1.0, 0.0, 0.0, 0.0, 1.0, 0.0);

    public MutablePoint apply(Point point, MutablePoint target) {
        double x = point.x();
        double y = point.y();
        target.setX(a * x + b * y + c);
        target.setY(d * x + e * y + f);
        return target;
    }
}
