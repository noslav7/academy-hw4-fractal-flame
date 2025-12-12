package academy.math;

/** Simple mutable point used to avoid excessive allocations when applying affine transforms in tight loops. */
public final class MutablePoint {
    private double x;
    private double y;

    public MutablePoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Point toImmutable() {
        return new Point(x, y);
    }
}
