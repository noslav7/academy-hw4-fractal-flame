package academy.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import academy.math.MutablePoint;
import academy.math.Point;
import org.junit.jupiter.api.Test;

/** Тесты аффинных параметров. */
class AffineParamsTest {

    /** Проверяет, что единичное преобразование сохраняет координаты. */
    @Test
    void givenIdentityWhenApplyThenCoordinatesStaySame() {
        Point point = new Point(1.25, -3.5);
        MutablePoint target = new MutablePoint(0.0, 0.0);

        MutablePoint result = AffineParams.IDENTITY.apply(point, target);

        assertSame(target, result);
        assertEquals(1.25, result.x(), 1e-9);
        assertEquals(-3.5, result.y(), 1e-9);
    }

    /** Проверяет корректность формулы аффинного преобразования. */
    @Test
    void givenCustomCoefficientsWhenApplyThenTransformsPoint() {
        AffineParams params = new AffineParams(2.0, 3.0, 4.0, 5.0, 6.0, 7.0);
        Point point = new Point(1.0, 2.0);
        MutablePoint target = new MutablePoint(0.0, 0.0);

        params.apply(point, target);

        assertEquals(12.0, target.x(), 1e-9);
        assertEquals(24.0, target.y(), 1e-9);
    }
}
