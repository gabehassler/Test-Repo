
package dr.inference.mcmc;

class FrequencyDistribution2D {

	double binSize1, binSize2;
	double start1 = 0.0, start2 = 0.0;
	double[][] bins;

	public FrequencyDistribution2D(int numBins, double binSize1, int numBins2, double binSize2) {
		init(numBins, binSize1, numBins2, binSize2);
	}	

	private void init(int numBins, double binSize1, int numBins2, double binSize2) {
		bins = new double[numBins][numBins2];
		this.binSize1 = binSize1; 
		this.binSize2 = binSize2; 
	}

	public void addPoint(double value1, double value2) {
	
		double diff = value1 - start1;

		int index1 = (int)(diff / binSize1);
		diff = value2 - start2;
		int index2 = (int)(diff / binSize2);
		
		if ((index1 >= 0) && (index1 < bins.length) && (index2 >= 0) && (index2 < bins[0].length)) {
			bins[index1][index2] += 1;
		}
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		double mean;
		buffer.append("\t");
		for (int i = 0; i < bins.length; i++) {
			mean = start2 + (binSize2 * ((double)i + 0.5));
			buffer.append(mean + "\t");
		}
		buffer.append("\n");
		for (int i = 0; i < bins.length; i++) {
			mean = start1 + (binSize1 * ((double)i + 0.5));
			buffer.append(mean + "\t");
			for (int j = 0; j < bins[i].length; j++) {
				buffer.append(bins[i][j] + "\t");
			}
			buffer.append("\n");
		}
		return new String(buffer);
	}
}
