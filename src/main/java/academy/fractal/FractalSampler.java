package academy.fractal;

import academy.config.FractalConfig;
import academy.math.MutablePoint;
import academy.math.Point;
import academy.variation.VariationDefinition;
import academy.variation.VariationSelector;
import java.util.Objects;
import java.util.SplittableRandom;

/**
 * Выполняет один шаг итерации фрактального пламени.
 */
public final class FractalSampler {
    private final FractalConfig config;
    private final VariationSelector selector;

    /**
     * Создаёт самплер для заданной конфигурации.
     *
     * @param config конфигурация фрактала
     */
    public FractalSampler(FractalConfig config) {
        this.config = Objects.requireNonNull(config, "config");
        this.selector = new VariationSelector(config.variations());
    }

    /**
     * Выполняет один шаг: выбирает вариацию и применяет преобразования.
     *
     * @param current текущая точка
     * @param random генератор случайных чисел
     * @param globalAffinePoint буфер для глобального аффинного преобразования
     * @param localAffinePoint буфер для локального аффинного преобразования
     * @return результат шага (новая точка и выбранная вариация)
     */
    public StepResult step(
            Point current, SplittableRandom random, MutablePoint globalAffinePoint, MutablePoint localAffinePoint) {
        Objects.requireNonNull(current, "current");
        Objects.requireNonNull(random, "random");
        Objects.requireNonNull(globalAffinePoint, "globalAffinePoint");
        Objects.requireNonNull(localAffinePoint, "localAffinePoint");
        VariationDefinition variation = selector.pick(random.nextDouble());
        Point afterGlobal = config.affineParams().apply(current, globalAffinePoint).toImmutable();
        Point afterLocal = variation.localAffine().apply(afterGlobal, localAffinePoint).toImmutable();
        Point nextPoint = variation.type().apply(afterLocal, variation, random);
        return new StepResult(nextPoint, variation);
    }

    /**
     * Результат шага: новая точка и выбранная вариация.
     *
     * @param point новая точка
     * @param variation выбранная вариация
     */
    public record StepResult(Point point, VariationDefinition variation) {}
}
