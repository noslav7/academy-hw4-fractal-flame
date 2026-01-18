package academy.camera;

import static org.junit.jupiter.api.Assertions.assertEquals;

import academy.color.Palette;
import academy.color.RgbColor;
import academy.config.AffineParams;
import academy.config.FractalConfig;
import academy.math.Point;
import academy.variation.VariationDefinition;
import academy.variation.VariationType;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.List;
import java.util.SplittableRandom;
import org.junit.jupiter.api.Test;

/** Тесты фабрики viewport. */
class ViewportFactoryTest {

    /** Проверяет дефолтное кадрирование при выключенном авто-подборе. */
    @Test
    void givenAutoFitDisabledWhenCreateThenUsesDefaultFraming() {
        CameraSettings camera = new CameraSettings(10.0, 20.0, 2.0, 0.0, false, 0.1, 10_000L);
        FractalConfig config =
                baseConfig(camera, 1.0).toBuilder().width(300).height(200).build();

        Viewport viewport = ViewportFactory.create(config);

        double baseScale = camera.scale() * (config.width() / 3.0);
        Point point = new Point(camera.centerX() + 1.0 / baseScale, camera.centerY());
        Viewport.ScreenPoint screen = viewport.project(point);
        assertEquals(config.width() / 2.0 + 1.0, screen.x(), 1e-6);
        assertEquals(config.height() / 2.0, screen.y(), 1e-6);
    }

    /** Проверяет, что авто-подбор центрирует на выборке. */
    @Test
    void givenAutoFitEnabledWhenCreateThenCentersOnSamplePoint() {
        double seed = 12.345;
        CameraSettings camera = new CameraSettings(5.0, -3.0, 1.0, 0.0, true, 0.1, 10_000L);
        FractalConfig config =
                baseConfig(camera, seed).toBuilder().width(320).height(240).build();

        SplittableRandom random = new SplittableRandom(Double.doubleToLongBits(seed));
        Point sample = new Point(random.nextDouble(-1.0, 1.0), random.nextDouble(-1.0, 1.0));

        Viewport viewport = ViewportFactory.create(config);
        Viewport.ScreenPoint screen = viewport.project(sample);

        assertEquals(config.width() / 2.0, screen.x(), 1e-6);
        assertEquals(config.height() / 2.0, screen.y(), 1e-6);
    }

    /** Проверяет fallback при NaN по оси X. */
    @Test
    void givenAutoFitWithNaNXWhenCreateThenFallsBackToDefaultFraming() {
        double seed = 7.0;
        CameraSettings camera = new CameraSettings(2.0, -1.0, 1.5, 0.0, true, 0.1, 10_000L);
        AffineParams nanAffine = new AffineParams(Double.NaN, 0.0, 0.0, 0.0, 1.0, 0.0);
        FractalConfig config = baseConfig(camera, seed, nanAffine).toBuilder()
                .width(300)
                .height(200)
                .build();

        Viewport viewport = ViewportFactory.create(config);

        double baseScale = camera.scale() * (config.width() / 3.0);
        Point point = new Point(camera.centerX() + 1.0 / baseScale, camera.centerY());
        Viewport.ScreenPoint screen = viewport.project(point);
        assertEquals(config.width() / 2.0 + 1.0, screen.x(), 1e-6);
        assertEquals(config.height() / 2.0, screen.y(), 1e-6);
    }

    /** Проверяет fallback при NaN по оси Y. */
    @Test
    void givenAutoFitWithNaNYWhenCreateThenFallsBackToDefaultFraming() {
        double seed = 8.0;
        CameraSettings camera = new CameraSettings(-1.0, 3.0, 1.2, 0.0, true, 0.1, 10_000L);
        AffineParams nanAffine = new AffineParams(1.0, 0.0, 0.0, 0.0, Double.NaN, 0.0);
        FractalConfig config = baseConfig(camera, seed, nanAffine).toBuilder()
                .width(300)
                .height(200)
                .build();

        Viewport viewport = ViewportFactory.create(config);

        double baseScale = camera.scale() * (config.width() / 3.0);
        Point point = new Point(camera.centerX() + 1.0 / baseScale, camera.centerY());
        Viewport.ScreenPoint screen = viewport.project(point);
        assertEquals(config.width() / 2.0 + 1.0, screen.x(), 1e-6);
        assertEquals(config.height() / 2.0, screen.y(), 1e-6);
    }

    /** Проверяет fallback при точках вне предельного радиуса. */
    @Test
    void givenAutoFitWithOutOfBoundsPointsWhenCreateThenFallsBackToDefaultFraming() {
        double seed = 3.14;
        CameraSettings camera = new CameraSettings(0.0, 0.0, 1.0, 0.0, true, 0.1, 10_000L);
        AffineParams farAffine = new AffineParams(1.0, 0.0, 1000.0, 0.0, 1.0, 1000.0);
        FractalConfig config = baseConfig(camera, seed, farAffine).toBuilder()
                .width(300)
                .height(200)
                .build();

        Viewport viewport = ViewportFactory.create(config);

        double baseScale = camera.scale() * (config.width() / 3.0);
        Point point = new Point(camera.centerX() + 1.0 / baseScale, camera.centerY());
        Viewport.ScreenPoint screen = viewport.project(point);
        assertEquals(config.width() / 2.0 + 1.0, screen.x(), 1e-6);
        assertEquals(config.height() / 2.0, screen.y(), 1e-6);
    }

    /** Проверяет, что burn-in пропускает первые итерации выборки. */
    @Test
    void givenAutoFitWithBurnInWhenCreateThenCentersOnStablePoint() {
        double seed = 9.0;
        CameraSettings camera = new CameraSettings(0.0, 0.0, 1.0, 0.0, true, 0.1, 10_000L);
        AffineParams constantAffine = new AffineParams(0.0, 0.0, 10.0, 0.0, 0.0, -5.0);
        FractalConfig config = baseConfig(camera, seed, constantAffine).toBuilder()
                .burnInIterations(10L)
                .width(200)
                .height(100)
                .build();

        Viewport viewport = ViewportFactory.create(config);

        Point point = new Point(10.0, -5.0);
        Viewport.ScreenPoint screen = viewport.project(point);
        assertEquals(config.width() / 2.0, screen.x(), 1e-6);
        assertEquals(config.height() / 2.0, screen.y(), 1e-6);
    }

    /** Проверяет обработку невалидного Y в ограничителе. */
    @Test
    void givenNonFiniteYWhenCheckingBoundsThenReturnsFalse() throws Exception {
        Method method = ViewportFactory.class.getDeclaredMethod("isWithinBounds", Point.class);
        method.setAccessible(true);

        boolean result = (boolean) method.invoke(null, new Point(1.0, Double.NaN));

        assertEquals(false, result);
    }

    /** Проверяет fallback при слишком маленьком масштабе авто-подбора. */
    @Test
    void givenAutoFitWithTooSmallScaleWhenCreateThenFallsBackToDefaultFraming() {
        double seed = 6.28;
        CameraSettings camera = new CameraSettings(0.0, 0.0, 1.0, 0.0, true, 0.1, 10_000L);
        AffineParams wideAffine = new AffineParams(1.1, 0.0, 0.5, 0.0, 1.1, 0.5);
        FractalConfig config = baseConfig(camera, seed, wideAffine).toBuilder()
                .width(1)
                .height(1)
                .build();

        Viewport viewport = ViewportFactory.create(config);

        double baseScale = camera.scale() * (config.width() / 3.0);
        Point point = new Point(camera.centerX() + 1.0 / baseScale, camera.centerY());
        Viewport.ScreenPoint screen = viewport.project(point);
        assertEquals(config.width() / 2.0 + 1.0, screen.x(), 1e-6);
        assertEquals(config.height() / 2.0, screen.y(), 1e-6);
    }

    private static FractalConfig baseConfig(CameraSettings camera, double seed) {
        return baseConfig(camera, seed, AffineParams.IDENTITY);
    }

    private static FractalConfig baseConfig(CameraSettings camera, double seed, AffineParams affineParams) {
        VariationDefinition variation = new VariationDefinition(
                VariationType.LINEAR, 1.0, RgbColor.of(1.0, 1.0, 1.0), 0.0, AffineParams.IDENTITY);
        return FractalConfig.builder()
                .width(320)
                .height(240)
                .iterationCount(1L)
                .seed(seed)
                .outputPath(Path.of("test.png"))
                .threads(1)
                .affineParams(affineParams)
                .variations(List.of(variation))
                .burnInIterations(0L)
                .palette(new Palette(List.of(RgbColor.of(0.2, 0.3, 0.4))))
                .camera(camera)
                .brightness(1.0)
                .gamma(2.2)
                .gammaCorrection(false)
                .logGammaCorrection(true)
                .symmetryLevel(1)
                .build();
    }
}
