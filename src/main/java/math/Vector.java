package math;

import java.util.Arrays;

public final class Vector {

    private final double[] values;

    private Vector(double[] values) {
        this.values = values;
    }

    public static Vector of(double... values) {
        return new Vector(values);
    }

    public static Vector zero(int size) {
        return new Vector(new double[size]);
    }

    public double get(int index) {
        return values[index];
    }

    public void set(int index, double value) {
        values[index] = value;
    }

    public int size() {
        return values.length;
    }

    public Vector plus(Vector other) {
        var values = toArray();

        for (int i = 0, size = size(); i < size; i++)
            values[i] += other.values[i];

        return Vector.of(values);
    }

    public double[] toArray() {
        return values.clone();
    }

    @Override
    public String toString() {
        return Arrays.toString(values);
    }
}
