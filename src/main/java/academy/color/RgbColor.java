package academy.color;

import java.util.Objects;
import java.util.SplittableRandom;

/**
 * Simple RGB color stored as doubles in the range [0, 1]. Provides helpers for mixing and clamping to 8-bit channels.
 */
public record RgbColor(double r, double g, double b) {

    public RgbColor {
        double nr = clamp(r);
        double ng = clamp(g);
        double nb = clamp(b);
        r = nr;
        g = ng;
        b = nb;
    }

    public static RgbColor of(double r, double g, double b) {
        return new RgbColor(r, g, b);
    }

    public static RgbColor random(SplittableRandom random) {
        return new RgbColor(random.nextDouble(), random.nextDouble(), random.nextDouble());
    }

    public RgbColor mix(RgbColor target, double blend) {
        Objects.requireNonNull(target, "target");
        double ratio = clamp(blend);
        double inverse = 1.0 - ratio;
        return new RgbColor(
                r * inverse + target.r * ratio, g * inverse + target.g * ratio, b * inverse + target.b * ratio);
    }

    public int toArgb(double alpha) {
        int a = toChannel(alpha);
        int red = toChannel(r);
        int green = toChannel(g);
        int blue = toChannel(b);
        return (a << 24) | (red << 16) | (green << 8) | blue;
    }

    private static double clamp(double value) {
        if (Double.isNaN(value)) return 0.0;
        if (value < 0.0) return 0.0;
        if (value > 1.0) return 1.0;
        return value;
    }

    private static int toChannel(double value) {
        return (int) Math.round(clamp(value) * 255.0);
    }
}
