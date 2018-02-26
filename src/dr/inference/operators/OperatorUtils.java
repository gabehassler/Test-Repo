package dr.inference.operators;
public class OperatorUtils {
    public static double optimizeWindowSize(double delta, double currentLevel, double targetLevel) {
        return optimizeWindowSize(delta, Double.MAX_VALUE, currentLevel, targetLevel);
    }
    public static double optimizeWindowSize(double delta, double maxDelta, double currentLevel, double targetLevel) {
        if (delta <= 0.0) {
            throw new IllegalArgumentException("random walk window size cannot be negative: " + delta);
        }
        double ratio = currentLevel / targetLevel;
        if (ratio > 2.0) ratio = 2.0;
        if (ratio < 0.5) ratio = 0.5;
        double newDelta = delta * ratio;
        if (newDelta > maxDelta) newDelta = maxDelta;
        return newDelta;
    }
    public static double optimizeScaleFactor(double scaleFactor, double currentLevel, double targetLevel) {
        if (scaleFactor <= 0.0 || scaleFactor >= 1.0) {
            throw new IllegalArgumentException("scale factor was " + scaleFactor + "!");
        }
        double ratio = currentLevel / targetLevel;
        if (ratio > 2.0) ratio = 2.0;
        if (ratio < 0.5) ratio = 0.5;
        // new scale factor
        return Math.pow(scaleFactor, ratio);
	}
}
