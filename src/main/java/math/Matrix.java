package math;

public final class Matrix {

    private final int rows;
    private final int columns;
    private final double[] values;

    private Matrix(int rows, int columns, double... values) {
        if (rows < 1) throw new IllegalArgumentException("Matrix must have at least 1 row.");
        if (columns < 1) throw new IllegalArgumentException("Matrix must have at least 1 column.");

        this.rows = rows;
        this.columns = columns;
        this.values = values;
    }

    public static Matrix of(int rows, int columns, double... values) {
        return new Matrix(rows, columns, values);
    }

    public static Matrix zero(int rows, int columns) {
        return new Matrix(rows, columns, new double[rows * columns]);
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public double get(int row, int column) {
        return values[row * columns + column];
    }

    public void set(int row, int column, double value) {
        values[row * columns + column] = value;
    }

    public Matrix plus(Matrix other) {
        var values = this.values.clone();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                values[row * columns + col] += other.get(row, col);
            }
        }

        return Matrix.of(other.rows, other.columns, values);
    }

    public Vector times(Vector vector) {
        var result = Vector.zero(rows);

        for (int row = 0; row < rows; row++) {
            var value = 0.0;

            for (int i = 0; i < columns; i++) {
                value += values[row * columns + i] * vector.get(i);
            }

            result.set(row, value);
        }

        return result;
    }
}
