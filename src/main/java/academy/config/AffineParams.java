package academy.config;

import academy.math.MutablePoint;
import academy.math.Point;

/**
 * Аффинные коэффициенты (a, b, c, d, e, f), применяемые перед нелинейной вариацией.
 *
 * @param a коэффициент по X
 * @param b коэффициент по X от Y
 * @param c сдвиг по X
 * @param d коэффициент по Y от X
 * @param e коэффициент по Y
 * @param f сдвиг по Y
 */
public record AffineParams(double a, double b, double c, double d, double e, double f) {

    /** Единичное преобразование. */
    public static final AffineParams IDENTITY = new AffineParams(1.0, 0.0, 0.0, 0.0, 1.0, 0.0);

    /**
     * Применяет аффинное преобразование к точке.
     *
     * @param point входная точка
     * @param target целевая изменяемая точка
     * @return {@code target} после преобразования
     */
    public MutablePoint apply(Point point, MutablePoint target) {
        double x = point.x();
        double y = point.y();
        target.setX(a * x + b * y + c);
        target.setY(d * x + e * y + f);
        return target;
    }
}
