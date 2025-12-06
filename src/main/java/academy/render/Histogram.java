package academy.render;

import academy.color.RgbColor;
import java.awt.image.BufferedImage;
import java.util.Arrays;

final class Histogram {

    private final int width;
    private final int height;
    private final double[] hits;
    private final double[] red;
    private final double[] green;
    private final double[] blue;

    Histogram(int width, int height) {
        this.width = width;
        this.height = height;
        int size = width * height;
        this.hits = new double[size];
        this.red = new double[size];
        this.green = new double[size];
        this.blue = new double[size];
    }

    void addPoint(int x, int y, RgbColor color) {
        if (x < 0 || y < 0 || x >= width || y >= height) return;
        int index = y * width + x;
        hits[index] += 1.0;
        red[index] += color.r();
        green[index] += color.g();
        blue[index] += color.b();
    }

    void merge(Histogram other) {
        for (int i = 0; i < hits.length; i++) {
            hits[i] += other.hits[i];
            red[i] += other.red[i];
            green[i] += other.green[i];
            blue[i] += other.blue[i];
        }
    }

    BufferedImage toImage(double gamma, boolean gammaCorrection, boolean logarithmicGamma, double exposure) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        var graphics = image.createGraphics();
        try {
            graphics.setColor(new java.awt.Color(0, 0, 0, 255));
            graphics.fillRect(0, 0, width, height);
        } finally {
            graphics.dispose();
        }
        double maxHit = Arrays.stream(hits).max().orElse(1.0);
        double logMaxHit = Math.log1p(maxHit);
        for (int i = 0; i < hits.length; i++) {
            double count = hits[i];
            if (count == 0.0) {
                continue;
            }
            double normalized =
                    logarithmicGamma
                            ? (logMaxHit > 0.0 ? Math.log1p(count) / logMaxHit : 0.0)
                            : count / maxHit;
            normalized = clamp01(normalized);
            double adjusted = (gammaCorrection ? Math.pow(normalized, 1.0 / gamma) : normalized) * exposure;
            double r = (red[i] / count) * adjusted;
            double g = (green[i] / count) * adjusted;
            double b = (blue[i] / count) * adjusted;
            int x = i % width;
            int y = i / width;
            image.setRGB(x, height - y - 1, new RgbColor(r, g, b).toArgb(1.0));
        }
        return image;
    }

    private static double clamp01(double value) {
        if (Double.isNaN(value)) return 0.0;
        if (value < 0.0) return 0.0;
        if (value > 1.0) return 1.0;
        return value;
    }
}

