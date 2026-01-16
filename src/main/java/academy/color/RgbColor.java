package academy.color;

import java.awt.Color;

/** Цвет RGB, хранящийся в диапазоне [0, 1] для каждого канала. */
public record RgbColor(double r, double g, double b) {

    /** Нормализует каналы в диапазон [0, 1]. */
    public RgbColor {
        double nr = clamp(r);
        double ng = clamp(g);
        double nb = clamp(b);
        r = nr;
        g = ng;
        b = nb;
    }

    /**
     * Создаёт цвет из компонент.
     *
     * @param r красный канал
     * @param g зелёный канал
     * @param b синий канал
     * @return новый цвет
     */
    public static RgbColor of(double r, double g, double b) {
        return new RgbColor(r, g, b);
    }

    /**
     * Переводит HSB в RGB.
     *
     * @param hue оттенок
     * @param saturation насыщенность
     * @param brightness яркость
     * @return цвет в RGB
     */
    public static RgbColor fromHsb(double hue, double saturation, double brightness) {
        int rgb = Color.HSBtoRGB((float) hue, (float) saturation, (float) brightness);
        double red = ((rgb >> 16) & 0xFF) / 255.0;
        double green = ((rgb >> 8) & 0xFF) / 255.0;
        double blue = (rgb & 0xFF) / 255.0;
        return new RgbColor(red, green, blue);
    }

    /**
     * Преобразует цвет в ARGB с заданной прозрачностью.
     *
     * @param alpha альфа-канал
     * @return 32-битный ARGB
     */
    public int toArgb(double alpha) {
        int a = toChannel(alpha);
        int red = toChannel(r);
        int green = toChannel(g);
        int blue = toChannel(b);
        return (a << 24) | (red << 16) | (green << 8) | blue;
    }

    /** Ограничивает значение диапазоном [0, 1]. */
    private static double clamp(double value) {
        if (Double.isNaN(value)) return 0.0;
        if (value < 0.0) return 0.0;
        if (value > 1.0) return 1.0;
        return value;
    }

    /** Переводит значение канала в диапазон 0..255. */
    private static int toChannel(double value) {
        return (int) Math.round(clamp(value) * 255.0);
    }
}
