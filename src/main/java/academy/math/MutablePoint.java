package academy.math;

/**
 * Изменяемая точка для снижения числа аллокаций в горячих циклах.
 */
public final class MutablePoint {
    private double x;
    private double y;

    /**
     * Создаёт изменяемую точку.
     *
     * @param x координата X
     * @param y координата Y
     */
    public MutablePoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /** @return координата X. */
    public double x() {
        return x;
    }

    /** @return координата Y. */
    public double y() {
        return y;
    }

    /** @param x новое значение X. */
    public void setX(double x) {
        this.x = x;
    }

    /** @param y новое значение Y. */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Создаёт неизменяемую копию точки.
     *
     * @return immutable точка
     */
    public Point toImmutable() {
        return new Point(x, y);
    }
}
