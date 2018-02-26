package dr.math;
public interface MinimiserMonitor {
	public void updateProgress(double progress);
	public void newMinimum(double value, double[] parameterValues);
}