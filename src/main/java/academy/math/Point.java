package academy.math;

/**
 * Immutable point in 2D space. Fractal rendering heavily relies on small objects, so we expose a lightweight record for
 * clarity.
 */
public record Point(double x, double y) {}
