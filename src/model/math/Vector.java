package model.math;

public class Vector {
    private final double x;
    private final double y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector add(Vector v2) {
        return new Vector(x + v2.x, y + v2.y);
    }

    public Vector sub(Vector v2) {
        return new Vector(x - v2.x, y - v2.y);
    }

    public Vector multiply(Vector v2) {
        return new Vector(x * v2.x, y * v2.y);
    }

    public Vector multiply(double c) {
        return new Vector(x * c, y * c);
    }

    public double dot(Vector v2) {
        return (x * v2.x + y * v2.y);
    }

    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector normalised() {
        return new Vector(x / magnitude(), y / magnitude());
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
