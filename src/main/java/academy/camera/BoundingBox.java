package academy.camera;

import academy.math.Point;

public final class BoundingBox {
    private double minX = Double.POSITIVE_INFINITY;
    private double maxX = Double.NEGATIVE_INFINITY;
    private double minY = Double.POSITIVE_INFINITY;
    private double maxY = Double.NEGATIVE_INFINITY;

    public void include(Point point) {
        double x = point.x();
        double y = point.y();
        if (x < minX) minX = x;
        if (x > maxX) maxX = x;
        if (y < minY) minY = y;
        if (y > maxY) maxY = y;
    }

    public boolean isEmpty() {
        return minX == Double.POSITIVE_INFINITY;
    }

    public double width() {
        return isEmpty() ? 1.0 : Math.max(1e-6, maxX - minX);
    }

    public double height() {
        return isEmpty() ? 1.0 : Math.max(1e-6, maxY - minY);
    }

    public double centerX() {
        return isEmpty() ? 0.0 : (minX + maxX) / 2.0;
    }

    public double centerY() {
        return isEmpty() ? 0.0 : (minY + maxY) / 2.0;
    }
}

