package util;

public class UserInputValidator {

    public static boolean assertInteger(String input) {
        return assertIntegerInRange(input, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public static boolean assertIntegerWithLowerBound(String input, int lowerBound) {
        return assertIntegerInRange(input, lowerBound, Integer.MAX_VALUE);
    }

    public static boolean assertIntegerWithUpperBound(String input, int upperBound) {
        return assertIntegerInRange(input, Integer.MIN_VALUE, upperBound);
    }

    public static boolean assertIntegerInRange(String input, int lowerBound, int upperBound) {
        try {
            int integer = Integer.parseInt(input);
            return integer >= lowerBound && integer <= upperBound;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    public static boolean assertDouble(String input) {
        return assertDoubleInRange(input, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    public static boolean assertDoubleWithLowerBound(String input, double lowerBound) {
        return assertDoubleInRange(input, lowerBound, Double.POSITIVE_INFINITY);
    }

    public static boolean assertDoubleWithUpperBound(String input, double upperBound) {
        return assertDoubleInRange(input, Double.NEGATIVE_INFINITY, upperBound);
    }

    public static boolean assertDoubleInRange(String input, double lowerBound, double upperBound) {
        try {
            double value = Double.parseDouble(input);
            return value >= lowerBound && value <= upperBound;
        } catch (NumberFormatException exception) {
            return false;
        }
    }
}
