package structures;

public class Point {
    public final double x;
    public final double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point plus(Point point) {
        return new Point(x + point.x, y + point.y);
    }

    public Point minus(Point point) {
        return new Point(x - point.x, y - point.y);
    }

    public Point scale(double scalar) {
        return new Point(x * scalar, y * scalar);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}