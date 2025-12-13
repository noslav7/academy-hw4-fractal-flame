package academy.variation;

import academy.math.Point;
import java.util.SplittableRandom;

/**
 * Subset of flame variations described by Draves. Each variation transforms a cartesian point into a new coordinate.
 */
public enum VariationType {
    LINEAR {
        @Override
        public Point apply(Point point, VariationDefinition definition, SplittableRandom random) {
            return point;
        }
    },
    SWIRL {
        @Override
        public Point apply(Point point, VariationDefinition definition, SplittableRandom random) {
            double r2 = radiusSquared(point);
            double sin = Math.sin(r2);
            double cos = Math.cos(r2);
            double x = point.x() * sin - point.y() * cos;
            double y = point.x() * cos + point.y() * sin;
            return new Point(x, y);
        }
    },
    HORSESHOE {
        @Override
        public Point apply(Point point, VariationDefinition definition, SplittableRandom random) {
            double r = hypot(point);
            if (r == 0.0) return ORIGIN;
            double factor = 1.0 / r;
            double x = (point.x() - point.y()) * (point.x() + point.y()) * factor;
            double y = 2.0 * point.x() * point.y() * factor;
            return new Point(x, y);
        }
    },
    SPHERICAL {
        @Override
        public Point apply(Point point, VariationDefinition definition, SplittableRandom random) {
            double r2 = radiusSquared(point);
            if (r2 == 0.0) return ORIGIN;
            double factor = 1.0 / r2;
            return new Point(point.x() * factor, point.y() * factor);
        }
    },
    SINUSOIDAL {
        @Override
        public Point apply(Point point, VariationDefinition definition, SplittableRandom random) {
            return new Point(Math.sin(point.x()), Math.sin(point.y()));
        }
    },
    BUBBLE {
        @Override
        public Point apply(Point point, VariationDefinition definition, SplittableRandom random) {
            double r2 = radiusSquared(point);
            double factor = 4.0 / (r2 + 4.0);
            return new Point(point.x() * factor, point.y() * factor);
        }
    },
    PDJ {
        @Override
        public Point apply(Point point, VariationDefinition definition, SplittableRandom random) {
            double a = definition.parameters().get("a", 1.0);
            double b = definition.parameters().get("b", 1.0);
            double c = definition.parameters().get("c", 1.0);
            double d = definition.parameters().get("d", 1.0);
            double x = Math.sin(a * point.y()) - Math.cos(b * point.x());
            double y = Math.sin(c * point.x()) - Math.cos(d * point.y());
            return new Point(x, y);
        }
    },
    FAN2 {
        @Override
        public Point apply(Point point, VariationDefinition definition, SplittableRandom random) {
            double fanX = definition.parameters().get("x", 1.0);
            double fanY = definition.parameters().get("y", 1.0);
            double radius = hypot(point);
            if (radius == 0.0) return ORIGIN;
            double theta = Math.atan2(point.y(), point.x());
            double t = theta + radius * fanY;
            double s = fanX * Math.PI;
            if (s == 0.0) {
                s = Math.PI;
            }
            double period = 2.0 * s;
            double adjusted = t - Math.floor(t / period) * period;
            double newTheta = adjusted > s ? theta - s : theta + s;
            return new Point(radius * Math.cos(newTheta), radius * Math.sin(newTheta));
        }
    },
    JULIAN {
        @Override
        public Point apply(Point point, VariationDefinition definition, SplittableRandom random) {
            double rawPower = Math.abs(definition.parameters().get("power", 2.0));
            double powerParam = rawPower < 1.0e-6 ? 1.0 : rawPower;
            int power = (int) Math.max(1, Math.round(powerParam));
            double dist = definition.parameters().get("dist", 1.0);
            double r = Math.hypot(point.x(), point.y());
            double theta = Math.atan2(point.y(), point.x());
            double magnitude = Math.pow(r, dist / powerParam);
            int k = random.nextInt(power);
            double angle = (theta + 2.0 * Math.PI * k) / powerParam;
            return new Point(magnitude * Math.cos(angle), magnitude * Math.sin(angle));
        }
    },
    DISC {
        @Override
        public Point apply(Point point, VariationDefinition definition, SplittableRandom random) {
            double radius = hypot(point);
            double theta = Math.atan2(point.y(), point.x());
            double factor = theta / Math.PI;
            double piRadius = Math.PI * radius;
            double x = factor * Math.sin(piRadius);
            double y = factor * Math.cos(piRadius);
            return new Point(x, y);
        }
    },
    SPIRAL {
        @Override
        public Point apply(Point point, VariationDefinition definition, SplittableRandom random) {
            double radius = hypot(point);
            if (radius == 0.0) return ORIGIN;
            double theta = Math.atan2(point.y(), point.x());
            double x = (Math.cos(theta) + Math.sin(radius)) / radius;
            double y = (Math.sin(theta) - Math.cos(radius)) / radius;
            return new Point(x, y);
        }
    },
    HEART {
        @Override
        public Point apply(Point point, VariationDefinition definition, SplittableRandom random) {
            double radius = hypot(point);
            double theta = Math.atan2(point.y(), point.x());
            double angle = theta * radius;
            double x = radius * Math.sin(angle);
            double y = -radius * Math.cos(angle);
            return new Point(x, y);
        }
    },
    HYPERBOLIC {
        @Override
        public Point apply(Point point, VariationDefinition definition, SplittableRandom random) {
            double radius = hypot(point);
            if (radius == 0.0) return ORIGIN;
            double theta = Math.atan2(point.y(), point.x());
            double x = Math.sin(theta) / radius;
            double y = radius * Math.cos(theta);
            return new Point(x, y);
        }
    },
    FISHEYE {
        @Override
        public Point apply(Point point, VariationDefinition definition, SplittableRandom random) {
            double radius = hypot(point);
            double factor = 2.0 / (radius + 1.0);
            return new Point(point.y() * factor, point.x() * factor);
        }
    };

    public abstract Point apply(Point point, VariationDefinition definition, SplittableRandom random);

    public static VariationType fromName(String value) {
        for (VariationType type : values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown variation: " + value);
    }

    private static double radiusSquared(Point point) {
        return point.x() * point.x() + point.y() * point.y();
    }

    private static double hypot(Point point) {
        return Math.hypot(point.x(), point.y());
    }

    private static final Point ORIGIN = new Point(0.0, 0.0);
}
