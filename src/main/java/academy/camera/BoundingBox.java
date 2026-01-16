package academy.camera;

import academy.math.Point;

/**
 * Границы множества точек в двумерном пространстве.
 */
public final class BoundingBox {
    private double minX = Double.POSITIVE_INFINITY;
    private double maxX = Double.NEGATIVE_INFINITY;
    private double minY = Double.POSITIVE_INFINITY;
    private double maxY = Double.NEGATIVE_INFINITY;

    /**
     * Расширяет границы так, чтобы они включали заданную точку.
     *
     * @param point точка для учёта
     */
    public void include(Point point) {
        double x = point.x();
        double y = point.y();
        if (x < minX) minX = x;
        if (x > maxX) maxX = x;
        if (y < minY) minY = y;
        if (y > maxY) maxY = y;
    }

    /**
     * Проверяет, что в границы ещё не добавлено ни одной точки.
     *
     * @return {@code true}, если коробка пуста
     */
    public boolean isEmpty() {
        return minX == Double.POSITIVE_INFINITY;
    }

    /**
     * Возвращает ширину границ (с защитой от нулевого значения).
     *
     * @return ширина
     */
    public double width() {
        return isEmpty() ? 1.0 : Math.max(1e-6, maxX - minX);
    }

    /**
     * Возвращает высоту границ (с защитой от нулевого значения).
     *
     * @return высота
     */
    public double height() {
        return isEmpty() ? 1.0 : Math.max(1e-6, maxY - minY);
    }

    /**
     * Центр по X, либо 0 при пустых границах.
     *
     * @return координата X центра
     */
    public double centerX() {
        return isEmpty() ? 0.0 : (minX + maxX) / 2.0;
    }

    /**
     * Центр по Y, либо 0 при пустых границах.
     *
     * @return координата Y центра
     */
    public double centerY() {
        return isEmpty() ? 0.0 : (minY + maxY) / 2.0;
    }
}
