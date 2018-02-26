
package dr.evolution.coalescent;

public class ExponentialSawtooth extends ExponentialGrowth {
	
	public ExponentialSawtooth(Type units) {
	
		super(units);
	}
	
	public final double getWavelength() { return wavelength; }
	
	public final void setWavelength(double t) { 
		if (t <= 0) throw new IllegalArgumentException();
		wavelength = t; 
	}
	
	public final double getOffset() { return offset; }
	
	public final void setOffset(double offset) {
		if (offset < 0 || offset >= 1.0) {
			throw new IllegalArgumentException();
		}
		this.offset = offset;
	}	
			
	// Implementation of abstract methods

	public double getDemographic(double t) {

		t += offset * wavelength;
		
		// rescale t so that 0 <= t < wavelength
		int cycle = (int)Math.floor(t / wavelength);
		t -= (cycle * wavelength);

		return super.getDemographic(t);
	}

	public double getIntensity(double t) {
	
		double absOffset = offset*wavelength;
		
		if (t < wavelength-absOffset) {
			// calculate intensity of first partial offset
			return super.getIntensity(t+absOffset) - super.getIntensity(absOffset);
		}
		
		// calculate intensity of first epoch:
		double intensity = super.getIntensity(wavelength) - super.getIntensity(absOffset);
		
		t -= (wavelength-absOffset);
		
		// calculate intensity of all full cycles
		int cycles = (int)Math.floor(t / wavelength);
		intensity += cycles * super.getIntensity(wavelength);
		t -= (cycles * wavelength);

		// calculate intensity of last partial cycle
		intensity += super.getIntensity(t);
		
		return intensity;
	}
	
	public double getInverseIntensity(double x) {
		
		throw new UnsupportedOperationException();
	}
	
	public int getNumArguments() {
		return 4;
	}
	
	public String getArgumentName(int n) {
		
		switch (n) {
			case 0: return "N0";
			case 1: return "r";
			case 2: return "wavelength";
			case 3: return "offset";
			default: throw new IllegalArgumentException();
		}
	}
	
	public double getArgument(int n) {
		switch (n) {
			case 0: return getN0();
			case 1: return getGrowthRate();
			case 2: return getWavelength();
			case 3: return getOffset();
			default: throw new IllegalArgumentException();
		}
	}
	
	public void setArgument(int n, double value) {
		switch (n) {
			case 0: setN0(value); break;
			case 1: setGrowthRate(value); break;
			case 2: setWavelength(value); break;
			case 3: setOffset(value); break;
			default: throw new IllegalArgumentException();
		}
	}

	public DemographicFunction getCopy() {
		ExponentialSawtooth df = new ExponentialSawtooth(getUnits());
		df.setN0(getN0());
		df.setGrowthRate(getGrowthRate());
		df.setWavelength(getWavelength());
		df.setOffset(getOffset());
		
		return df;
	}
	
	public static void main(String[] args) {
	
		double N0 = Double.parseDouble(args[0]);
		double growthRate = Double.parseDouble(args[1]);
		double wavelength = Double.parseDouble(args[2]);
		double offset = Double.parseDouble(args[3]);
		
	
		ExponentialSawtooth est = new ExponentialSawtooth(Type.SUBSTITUTIONS);
		est.setN0(N0);
		est.setGrowthRate(growthRate);
		est.setWavelength(wavelength);
		est.setOffset(offset);
	
		for (double time = 0; time < 20; time+=0.1) {
			System.out.println(time + "\t" + est.getDemographic(time) + "\t" + est.getIntensity(time));
		}
	}
	

	//
	// private stuff
	//

	private double wavelength;
	private double offset;
}
