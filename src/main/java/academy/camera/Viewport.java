package academy.camera;

import academy.math.Point;

public final class Viewport {
    private final int width;
    private final int height;
    private final double centerX;
    private final double centerY;
    private final double scale;
    private final double cos;
    private final double sin;

    public Viewport(int width, int height, double centerX, double centerY, double scale, double rotationDegrees) {
        this.width = width;
        this.height = height;
        this.centerX = centerX;
        this.centerY = centerY;
        this.scale = scale;
        double radians = Math.toRadians(rotationDegrees);
        this.cos = Math.cos(radians);
        this.sin = Math.sin(radians);
    }

    public ScreenPoint project(Point point) {
        double translatedX = point.x() - centerX;
        double translatedY = point.y() - centerY;
        double rotatedX = translatedX * cos - translatedY * sin;
        double rotatedY = translatedX * sin + translatedY * cos;
        double screenX = width / 2.0 + rotatedX * scale;
        double screenY = height / 2.0 - rotatedY * scale;
        return new ScreenPoint(screenX, screenY, width, height);
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public record ScreenPoint(double x, double y, int width, int height) {
        public boolean isInside() {
            return x >= 0 && y >= 0 && x < width && y < height;
        }

        public int ix() {
            return (int) x;
        }

        public int iy() {
            return (int) y;
        }
    }
}
