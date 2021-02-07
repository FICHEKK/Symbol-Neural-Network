package ui;

import java.awt.*;

public final class Colors {

    public static final Color BLACK = Color.BLACK;
    public static final Color WHITE = Color.WHITE;
    public static final Color TINTED_WHITE = new Color(0xE7E9EF);
    public static final Color BLUE = new Color(0, 104, 250, 255);
    public static final Color DARK_BLUE = new Color(0, 54, 131, 255);
    public static final Color GOLD = Color.ORANGE;
    public static final Color ORANGE = new Color(255, 110, 0, 255);
    public static final Color MAGENTA = new Color(233, 0, 179, 255);
    public static final Color DARK_MAGENTA = new Color(41, 0, 55, 255);
    public static final Color RED = Color.RED;

    public static final Color BACKGROUND = BLUE;
    public static final Color VALID_TEXT = Color.WHITE;
    public static final Color INVALID_TEXT = Color.RED;

    private Colors() {
        throw new AssertionError("Colors class is not meant to be instantiated.");
    }
}
