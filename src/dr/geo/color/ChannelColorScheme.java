package dr.geo.color;
import java.awt.*;
import java.util.ArrayList;
public interface ChannelColorScheme {
    Color getColor(java.util.List<Double> input, java.util.List<Double> min, java.util.List<Double> max);
    static class MultipleChannelColorScheme implements ChannelColorScheme {
        private final ColorScheme[] schemes;
        MultipleChannelColorScheme(ColorScheme[] schemes) {
            this.schemes = schemes;
        }
        public Color getColor(java.util.List<Double> input, java.util.List<Double> min, java.util.List<Double> max) {
            java.util.List<Color> colors = new ArrayList<Color>();
            final int channels = schemes.length; // assumes the same length as input, min, max
            for (int i = 0; i < channels; ++i) {
                colors.add(schemes[i].getColor(input.get(i), min.get(i), max.get(i)));
            }
            return blend(colors);
        }
        private static Color blend(java.util.List<Color> colors) {
            double totalAlpha = 0.0;
            for (Color color : colors) {
                totalAlpha += color.getAlpha();
            }
            double r = 0.0;
            double g = 0.0;
            double b = 0.0;
            double a = 0.0;
            for (Color color : colors) {
                double weight = color.getAlpha();
                r += weight * color.getRed();
                g += weight * color.getGreen();
                b += weight * color.getBlue();
                a = Math.max(a, weight);
            }
            r /= totalAlpha;
            g /= totalAlpha;
            b /= totalAlpha;
            return new Color((int) r, (int) g, (int) b, (int) a);
        }
    }
    public static final ChannelColorScheme CHANNEL_RED = new MultipleChannelColorScheme(
            new ColorScheme[]{ColorScheme.WHITE_RED}
    );
    public static final ChannelColorScheme CHANNEL_RED_BLUE = new MultipleChannelColorScheme(
            new ColorScheme[]{ColorScheme.WHITE_RED, ColorScheme.WHITE_BLUE}
    );
}