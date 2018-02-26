package dr.evolution.continuous;
public interface Contrastable {
	public Contrastable getWeightedMean(double weight1, Contrastable cont1, double weight2, Contrastable cont2);
	public double getDifference(Contrastable B);
}
