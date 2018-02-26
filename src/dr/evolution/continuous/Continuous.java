package dr.evolution.continuous;
public class Continuous implements Contrastable {
	public Continuous(double value) {
		this.value = value;
	}
	public double getDifference(Contrastable cont) {
		if (cont instanceof Continuous) {
			return value - ((Continuous)cont).value;
		} else throw new IllegalArgumentException("Expected a continuous parameter");
	}
	public Contrastable getWeightedMean(double weight1, Contrastable cont1, double weight2, Contrastable cont2) {
		double value = 0.0;
		value += ((Continuous)cont1).value * weight1;
		value += ((Continuous)cont2).value * weight2;
		value /= (weight1 + weight2);
		return new Continuous(value);
	}
	public double getValue() { return value; }
    public String toString() { return ""+getValue(); }
    private double value;
}
