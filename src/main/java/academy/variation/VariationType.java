package academy.variation;

import academy.math.Point;
import academy.util.StringValidators;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.SplittableRandom;

/**
 * Subset of flame variations described by Draves. Each variation transforms a cartesian point into a new coordinate.
 */
public enum VariationType {
    LINEAR("linear") {
        @Override
        public Point apply(Point point, VariationDefinition definition, SplittableRandom random) {
            return point;
        }
    },
    SWIRL("swirl") {
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
    HORSESHOE("horseshoe") {
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
    SPHERICAL("spherical") {
        @Override
        public Point apply(Point point, VariationDefinition definition, SplittableRandom random) {
            double r2 = radiusSquared(point);
            if (r2 == 0.0) return ORIGIN;
            double factor = 1.0 / r2;
            return new Point(point.x() * factor, point.y() * factor);
        }
    },
    SINUSOIDAL("sinusoidal") {
        @Override
        public Point apply(Point point, VariationDefinition definition, SplittableRandom random) {
            return new Point(Math.sin(point.x()), Math.sin(point.y()));
        }
    },
    BUBBLE("bubble") {
        @Override
        public Point apply(Point point, VariationDefinition definition, SplittableRandom random) {
            double r2 = radiusSquared(point);
            double factor = 4.0 / (r2 + 4.0);
            return new Point(point.x() * factor, point.y() * factor);
        }
    },
    PDJ("pdj") {
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
    FAN2("fan2") {
        @Override
        public Point apply(Point point, VariationDefinition definition, SplittableRandom random) {
            double fanX = definition.parameters().get("x", 1.0);
            double fanY = definition.parameters().get("y", 1.0);
            double radius = hypot(point);
            if (radius == 0.0) return ORIGIN;
            double theta = Math.atan2(point.y(), point.x());
            double t = theta + radius * fanY;
            double s = fanX * fanX * Math.PI; // flam3: p1 = π * x²
            if (s == 0.0) {
                s = Math.PI;
            }
            double period = 2.0 * s;
            double adjusted = t - Math.floor(t / period) * period;
            double newTheta = adjusted > s ? theta - s : theta + s;
            return new Point(radius * Math.cos(newTheta), radius * Math.sin(newTheta));
        }
    },
    JULIAN("julian") {
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
    DISC("disc") {
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
    SPIRAL("spiral") {
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
    HEART("heart") {
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
    HYPERBOLIC("hyperbolic") {
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
    FISHEYE("fisheye") {
        @Override
        public Point apply(Point point, VariationDefinition definition, SplittableRandom random) {
            double radius = hypot(point);
            double factor = 2.0 / (radius + 1.0);
            return new Point(point.y() * factor, point.x() * factor);
        }
    };

    public abstract Point apply(Point point, VariationDefinition definition, SplittableRandom random);

    private static final int SYMBOL_MIN_LENGTH = 1;
    private static final int SYMBOL_MAX_LENGTH = 32;
    private static final Map<String, VariationType> BY_SYMBOL = buildSymbolIndex();
    private final String[] symbols;

    VariationType(String... symbols) {
        this.symbols = symbols;
    }

    public static VariationType fromName(String value) {
        String normalized =
                StringValidators.requireLength(value, "variation symbol", SYMBOL_MIN_LENGTH, SYMBOL_MAX_LENGTH)
                        .toLowerCase(Locale.ROOT);
        VariationType type = BY_SYMBOL.get(normalized);
        if (type == null) {
            throw new IllegalArgumentException("Unknown variation: " + value);
        }
        return type;
    }

    private static Map<String, VariationType> buildSymbolIndex() {
        Map<String, VariationType> result = new HashMap<>();
        for (VariationType type : values()) {
            for (String symbol : type.symbols) {
                String normalized = symbol.toLowerCase(Locale.ROOT);
                if (result.putIfAbsent(normalized, type) != null) {
                    throw new IllegalStateException("Duplicate variation symbol: " + symbol);
                }
            }
        }
        return Map.copyOf(result);
    }

    private static double radiusSquared(Point point) {
        return point.x() * point.x() + point.y() * point.y();
    }

    private static double hypot(Point point) {
        return Math.hypot(point.x(), point.y());
    }

    private static final Point ORIGIN = new Point(0.0, 0.0);
}
