package academy.camera;

import academy.math.Point;

/** Проекция из мировых координат в экранные с учётом центра, масштаба и поворота. */
public final class Viewport {
    private final int width;
    private final int height;
    private final double centerX;
    private final double centerY;
    private final double scale;
    private final double cos;
    private final double sin;

    /**
     * Создаёт проекцию для заданных параметров экрана и камеры.
     *
     * @param width ширина экрана
     * @param height высота экрана
     * @param centerX центр по X
     * @param centerY центр по Y
     * @param scale масштаб
     * @param rotationDegrees угол поворота в градусах
     */
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

    /**
     * Проецирует мировую точку в экранные координаты.
     *
     * @param point мировая точка
     * @return экранная точка с признаком попадания в кадр
     */
    public ScreenPoint project(Point point) {
        double translatedX = point.x() - centerX;
        double translatedY = point.y() - centerY;
        double rotatedX = translatedX * cos - translatedY * sin;
        double rotatedY = translatedX * sin + translatedY * cos;
        double screenX = width / 2.0 + rotatedX * scale;
        double screenY = height / 2.0 - rotatedY * scale;
        return new ScreenPoint(screenX, screenY, width, height);
    }

    /** @return ширина экрана. */
    public int width() {
        return width;
    }

    /** @return высота экрана. */
    public int height() {
        return height;
    }

    /**
     * Экранная точка с размером кадра для проверки попадания.
     *
     * @param x координата X
     * @param y координата Y
     * @param width ширина кадра
     * @param height высота кадра
     */
    public record ScreenPoint(double x, double y, int width, int height) {
        /** @return {@code true}, если точка находится внутри кадра. */
        public boolean isInside() {
            return x >= 0 && y >= 0 && x < width && y < height;
        }

        /** @return координата X, приведённая к целому. */
        public int ix() {
            return (int) x;
        }

        /** @return координата Y, приведённая к целому. */
        public int iy() {
            return (int) y;
        }
    }
}
