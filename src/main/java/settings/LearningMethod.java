package settings;

public enum LearningMethod {
    STOCHASTIC("Stochastic"),
    MINI_BATCH("Mini-batch"),
    BATCH("Batch");

    private final String name;

    LearningMethod(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static LearningMethod from(String name) {
        for (var method : values()) {
            if (method.toString().equals(name)) {
                return method;
            }
        }

        throw new IllegalArgumentException("Could not convert '" + name + "' to a specific learning method.");
    }
}
