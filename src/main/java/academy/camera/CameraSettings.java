package academy.camera;

/**
 * Настройки камеры, определяющие, как мировые координаты переводятся в экранные.
 *
 * @param centerX центр по X
 * @param centerY центр по Y
 * @param scale масштаб
 * @param rotationDegrees поворот в градусах
 * @param autoFit включить авто-подбор кадрирования
 * @param fitMargin запас по краям при авто-подборе
 * @param fitSamples число выборок для авто-подбора
 */
public record CameraSettings(
        double centerX,
        double centerY,
        double scale,
        double rotationDegrees,
        boolean autoFit,
        double fitMargin,
        long fitSamples) {

    /** Набор значений по умолчанию. */
    public static final CameraSettings DEFAULT = new CameraSettings(0.0, 0.0, 1.0, 0.0, true, 0.1, 200_000L);

    /**
     * Проверяет корректность параметров камеры.
     */
    public CameraSettings {
        if (scale <= 0.0) throw new IllegalArgumentException("scale must be > 0");
        if (fitMargin < 0.0) throw new IllegalArgumentException("fitMargin must be >= 0");
        if (fitSamples < 10_000L) {
            throw new IllegalArgumentException("fitSamples must be at least 10_000");
        }
    }

}
