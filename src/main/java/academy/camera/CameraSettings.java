package academy.camera;

/** Describes how world coordinates are mapped to screen space. */
public record CameraSettings(
        double centerX,
        double centerY,
        double scale,
        double rotationDegrees,
        boolean autoFit,
        double fitMargin,
        long fitSamples) {

    public static final CameraSettings DEFAULT = new CameraSettings(0.0, 0.0, 1.0, 0.0, true, 0.1, 200_000L);

    public CameraSettings {
        if (scale <= 0.0) throw new IllegalArgumentException("scale must be > 0");
        if (fitMargin < 0.0) throw new IllegalArgumentException("fitMargin must be >= 0");
        if (fitSamples < 10_000L) {
            throw new IllegalArgumentException("fitSamples must be at least 10_000");
        }
    }

    public CameraSettings withScale(double newScale) {
        return new CameraSettings(centerX, centerY, newScale, rotationDegrees, autoFit, fitMargin, fitSamples);
    }

    public CameraSettings withCenter(double x, double y) {
        return new CameraSettings(x, y, scale, rotationDegrees, autoFit, fitMargin, fitSamples);
    }
}
