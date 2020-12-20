package structures;

import java.util.Random;

public class Dataset {

    private static final Random RANDOM = new Random();

    public final double[][] X;
    public final double[][] Y;

    public Dataset(double[][] X, double[][] Y) {
        if (X.length != Y.length)
            throw new IllegalArgumentException("X.length != Y.length");

        this.X = X;
        this.Y = Y;
    }

    public Dataset shuffle() {
        var rows = X.length;

        for (int i = rows - 1; i > 0; i--) {
            int r = RANDOM.nextInt(i + 1);
            swapMatrixRows(X, i, r);
            swapMatrixRows(Y, i, r);
        }

        return this;
    }

    public Dataset expand(int additionalPermutationsPerSample) {
        if (additionalPermutationsPerSample < 0)
            throw new IllegalArgumentException("Additional permutations per sample cannot be a negative number.");

        var rows = X.length;
        var expandedX = new double[rows * (1 + additionalPermutationsPerSample)][];
        var expandedY = new double[rows * (1 + additionalPermutationsPerSample)][];

        for (int i = 0; i < rows; i++) {
            // Leave the original element in.
            var originalSampleIndex = i * (1 + additionalPermutationsPerSample);
            expandedX[originalSampleIndex] = X[i];
            expandedY[originalSampleIndex] = Y[i];

            for (int j = 1; j <= additionalPermutationsPerSample; j++) {
                var permutedSampleIndex = originalSampleIndex + j;
                expandedX[permutedSampleIndex] = getPermutedCopy(X[i]);
                expandedY[permutedSampleIndex] = Y[i];
            }
        }

        return new Dataset(expandedX, expandedY);
    }

    private static double[] getPermutedCopy(double[] array) {
        var permuted = array.clone();
        shuffleArray(permuted);
        return permuted;
    }

    private static void shuffleArray(double[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int r = RANDOM.nextInt(i + 1);
            swapArrayElements(array, i, r);
        }
    }

    private static void swapArrayElements(double[] array, int i, int j) {
        var temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    private static void swapMatrixRows(double[][] matrix, int i, int j) {
        var temp = matrix[i];
        matrix[i] = matrix[j];
        matrix[j] = temp;
    }
}
