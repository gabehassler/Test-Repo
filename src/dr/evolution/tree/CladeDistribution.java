
package dr.evolution.tree;

import java.util.List;

public class CladeDistribution {
	
	private Clade clade;
	
	private int samples;
	
	private List<Double> heights;

	public CladeDistribution(Clade clade, int samples) {
		super();
		this.clade = clade;
		this.heights = heights;
		this.samples = samples;
	}

	public boolean addHeight(Double o) {
		samples++;
		return heights.add(o);
	}

	public Clade getClade() {
		return clade;
	}

	public void setClade(Clade clade) {
		this.clade = clade;
	}

	public int getSamples() {
		return samples;
	}

	public List<Double> getHeights() {
		return heights;
	}
	
	

}
