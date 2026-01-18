package academy.color;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

/** Тесты палитры. */
class PaletteTest {

    /** Проверяет, что null-список запрещён. */
    @Test
    void givenNullColorsWhenConstructedThenThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Palette(null));
    }

    /** Проверяет, что пустой список запрещён. */
    @Test
    void givenEmptyColorsWhenConstructedThenThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Palette(List.of()));
    }

    /** Проверяет выбор цвета при единственном значении. */
    @Test
    void givenSingleColorWhenSampleThenReturnsSameColor() {
        Palette palette = new Palette(List.of(RgbColor.of(0.1, 0.2, 0.3)));

        RgbColor sample = palette.sample(0.75);

        assertEquals(0.1, sample.r(), 1e-9);
        assertEquals(0.2, sample.g(), 1e-9);
        assertEquals(0.3, sample.b(), 1e-9);
    }

    /** Проверяет интерполяцию между двумя цветами. */
    @Test
    void givenTwoColorsWhenSampleMidpointThenInterpolates() {
        Palette palette = new Palette(List.of(RgbColor.of(0.0, 0.0, 0.0), RgbColor.of(1.0, 1.0, 1.0)));

        RgbColor sample = palette.sample(0.5);

        assertEquals(0.5, sample.r(), 1e-9);
        assertEquals(0.5, sample.g(), 1e-9);
        assertEquals(0.5, sample.b(), 1e-9);
    }

    /** Проверяет нормализацию индекса за пределами диапазона. */
    @Test
    void givenOutOfRangeIndexWhenSampleThenWrapsIntoRange() {
        Palette palette = new Palette(List.of(RgbColor.of(0.0, 0.0, 0.0), RgbColor.of(1.0, 1.0, 1.0)));

        RgbColor sample = palette.sample(-0.25);

        assertEquals(0.75, sample.r(), 1e-9);
        assertEquals(0.75, sample.g(), 1e-9);
        assertEquals(0.75, sample.b(), 1e-9);
    }
}
