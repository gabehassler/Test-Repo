
package dr.util;

public class FrequencyDistribution {

	private double binSize;
	
	private double start = 0.0;
	
	private double smaller;

	private double larger;

	private int[] bins;

	public FrequencyDistribution(double start, int numBins, double binSize) {
		init(start, numBins, binSize);
	}

	public FrequencyDistribution(int numBins, double binSize) {
		init(0.0, numBins, binSize);
	}

	public FrequencyDistribution(double[] stats, int numBins, double binSize) {
		init(0.0, numBins, binSize);
        for (double stat : stats) {
            addValue(stat);
        }
    }

	public int getBinCount() {
		return bins.length;
	}

	public double getBinSize() {
		return binSize;
	}

	public double getLowerBound() {
		return start;
	}

	public int getFrequency(int bin) {
		return bins[bin];
	}

    public double getProb(int bin) {
        int total = 0;
        for (int b : bins) {
            total = total + b;
        }
        if (total == 0) {
            return 0.0;
        } else {
		    return (double) bins[bin] / (double) total;
        }
	}

	public void addValue(double value) {
	
		double diff = value - start;

		int index = (int)(diff / binSize);
		if (index < 0) {
			smaller += 1;
		} else if (index >= bins.length) {
			larger += 1;
		} else bins[index] += 1;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
        buffer.append("< ").append(start).append("\t").append(smaller).append("\n");
		double mean;
		for (int i = 0; i < bins.length; i++) {
			mean = start + (binSize * ((double)i + 0.5));
            buffer.append(mean).append("\t").append(bins[i]).append("\n");
		}
		double end = start + (binSize * bins.length);
        buffer.append(">= ").append(end).append("\t").append(larger).append("\n");
		return new String(buffer);
	}

	private void init(double start, int numBins, double binSize) {
		bins = new int[numBins];
		this.binSize = binSize; 
		smaller = 0;
		larger = 0;
		this.start = start;
	}
}
