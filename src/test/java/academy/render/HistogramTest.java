package academy.render;

import static org.junit.jupiter.api.Assertions.assertTrue;

import academy.color.RgbColor;
import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;

/**
 * Тесты преобразования гистограммы в изображение.
 */
class HistogramTest {

    /**
     * Проверяет, что логарифмическая гамма уменьшает контраст.
     */
    @Test
    void logarithmicGammaCompressesDynamicRange() {
        Histogram histogram = new Histogram(2, 1);
        // Left pixel gets four hits, right pixel one hit to create a noticeable contrast.
        histogram.addPoint(0, 0, RgbColor.of(1.0, 1.0, 1.0));
        histogram.addPoint(0, 0, RgbColor.of(1.0, 1.0, 1.0));
        histogram.addPoint(0, 0, RgbColor.of(1.0, 1.0, 1.0));
        histogram.addPoint(0, 0, RgbColor.of(1.0, 1.0, 1.0));
        histogram.addPoint(1, 0, RgbColor.of(1.0, 1.0, 1.0));

        BufferedImage linear = histogram.toImage(2.2, false, false, 1.0);
        BufferedImage logarithmic = histogram.toImage(2.2, false, true, 1.0);

        int linearLeft = channel(linear, 0, 0);
        int linearRight = channel(linear, 1, 0);
        int logLeft = channel(logarithmic, 0, 0);
        int logRight = channel(logarithmic, 1, 0);

        assertTrue(
                linearLeft - linearRight > logLeft - logRight,
                "Logarithmic correction should reduce contrast compared to linear scaling");
    }

    /**
     * Проверяет корректность логарифмической гаммы на единственном попадании.
     */
    @Test
    void logarithmicGammaHandlesSingleHit() {
        Histogram histogram = new Histogram(1, 1);
        histogram.addPoint(0, 0, RgbColor.of(1.0, 0.0, 0.0));

        BufferedImage result = histogram.toImage(2.2, false, true, 1.0);

        assertTrue(channel(result, 0, 0) > 0, "Single hit should remain visible after log scaling");
    }

    /**
     * Возвращает красный канал пикселя.
     */
    private static int channel(BufferedImage image, int x, int y) {
        return (image.getRGB(x, y) >> 16) & 0xFF;
    }
}
