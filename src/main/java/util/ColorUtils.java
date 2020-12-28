package util;

import java.awt.*;

public final class ColorUtils {

    private ColorUtils() {
    }

    public static Color interpolateRGB(Color color1, Color color2, float percentage) {
        var r = ((1 - percentage) * color1.getRed() + percentage * color2.getRed()) / 255;
        var g = ((1 - percentage) * color1.getGreen() + percentage * color2.getGreen()) / 255;
        var b = ((1 - percentage) * color1.getBlue() + percentage * color2.getBlue()) / 255;
        return new Color(r, g, b);
    }

    public static Color interpolateHSB(Color color1, Color color2, float percentage) {
        var hsb1 = Color.RGBtoHSB(color1.getRed(), color1.getGreen(), color1.getBlue(), null);
        var hsb2 = Color.RGBtoHSB(color2.getRed(), color2.getGreen(), color2.getBlue(), null);

        var h = (1 - percentage) * hsb1[0] + percentage * hsb2[0];
        var s = (1 - percentage) * hsb1[1] + percentage * hsb2[1];
        var b = (1 - percentage) * hsb1[2] + percentage * hsb2[2];

        return new Color(Color.HSBtoRGB(h, s, b));
    }
}
