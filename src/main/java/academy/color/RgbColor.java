package academy.color;

import java.awt.Color;

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

    public static RgbColor fromHsb(double hue, double saturation, double brightness) {
        int rgb = Color.HSBtoRGB((float) hue, (float) saturation, (float) brightness);
        double red = ((rgb >> 16) & 0xFF) / 255.0;
        double green = ((rgb >> 8) & 0xFF) / 255.0;
        double blue = (rgb & 0xFF) / 255.0;
        return new RgbColor(red, green, blue);
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
