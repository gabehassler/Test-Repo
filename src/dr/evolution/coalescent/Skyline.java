package dr.evolution.coalescent;
import dr.evolution.tree.Tree;
import dr.evolution.util.Units;
import dr.math.Binomial;
public class Skyline implements Units {
	//
	// Public stuff
	//
	Skyline() {}
	public Skyline(Tree tree, double epsilon)
	{
		this(new TreeIntervals(tree), 1.0, epsilon);
	}
	public Skyline(Tree tree, double mutationRate, double epsilon)
	{
		this(new TreeIntervals(tree), mutationRate, epsilon);
	}
	public Skyline(IntervalList intervals, double mutationRate, double epsilon)
	{
		init(intervals, mutationRate, epsilon);
	}
	void init(IntervalList intervals, double mutationRate, double epsilon) {
		if (!intervals.isBinaryCoalescent()) {
			throw new IllegalArgumentException("All intervals must contain only a single coalescent");
		}
		mu = mutationRate;
		size = intervals.getIntervalCount();
		this.intervals = intervals;
		// population size in each coalescent interval
		populationSize = new double[size];
		// cumulative interval sizes
		cis = new double[size];
		maxTime = 0.0;
		for (int i = 0; i < size; i++) {
			cis[i] = maxTime;
			maxTime += intervals.getInterval(i) / mu;
		}
		if (epsilon == 0.0) {
			computeClassic();
		} else if (epsilon > 0.0) {
			computeGeneralized(epsilon);
		} else {
			// find optimal generalized skyline plot
			optimize();
		}
	}
	public String toString()
	{
		StringBuffer buffer = new StringBuffer("Skyline Plot ");
		if (eps == 0.0) {
			buffer.append("(classic): ");
		} else {
			buffer.append("(generalized): ");
			buffer.append("epsilon = " + eps + ", ");
		}
		buffer.append("log L = " + getLogLikelihood());
		if (params > size-2) {
			buffer.append("\tlog L(AICC) not available");
		} else {
			buffer.append("\tlog L(AICC) = " + getAICC());
		}
		return buffer.toString();
	}
	public void computeClassic()
	{
		int i = 0;
		int k = 0;
		do {
			double popSize = 0.0;
			boolean done = false;
			k = i;
			do {
				double w = intervals.getInterval(i) / mu;
				int n = intervals.getLineageCount(i);
				if (n < 0) n = 0;
				done = intervals.getIntervalType(i) == IntervalType.COALESCENT;
				popSize += w * Binomial.choose2(n);
				i++;
			} while (i < size && !done);
			for (int j = k; j < i; j++) {
				populationSize[j] = popSize;
			}
		} while (i < size);
		params = k;
		eps = 0.0;
	}
	public void computeGeneralized(double epsilon)
	{
		params = 0;
		double cw = 0; //cumulative w
		for (int i = 0; i < size; i++) {
			int n = intervals.getLineageCount(i);
			if (n < 0) n = 0;
			double w = intervals.getInterval(i) / mu;
			int start = i;
			int k = 1;
			while ((w < epsilon && i < size-1) || 
				intervals.getIntervalType(i) != IntervalType.COALESCENT) {
				i++;
				k++;
				w += intervals.getInterval(i) / mu;
				//System.out.println(ci.getInterval(i));
			} 
			//System.out.println("w=" + w + " k=" + k + "  i=" + i);
			// if remainder is smaller than epsilon
			// continue pooling until the end
			if (maxTime - cw - w < epsilon) {				
				for (int j = i+1; j < size; j++)
				{
					i++;
					k++;
					w += intervals.getInterval(i) / mu;
				}
			}
			double m = w * Binomial.choose2(n) / k;
			// assign the same pop.size to all sub intervals
			for (int j = start; j < start+k; j++) {
				populationSize[j] = m;
			}
			params++;
			cw += w;
		}
		eps = epsilon;
	}
	public void optimize()
	{
		// this is the naive way of doing this ...
		double besteps = getMaxTime();
		computeGeneralized(besteps);
		double bestaicc = getAICC();
		int GRID = 1000;
		double delta = besteps/GRID;
		double MINEPS = 1e-6;
		// Why MINEPS?
		// Because most "clock-like" trees are not properly
		// clock-like for a variety of reasons, i.e. the heights
		// of the tips are not exactly zero.
		eps = eps - delta;
		while(eps > MINEPS)
		{
			computeGeneralized(eps);
			double aicc = getAICC();
			if (aicc > bestaicc && params < size-1)
			{
				besteps = eps;
				bestaicc = aicc;
			}
			eps = eps - delta;
		}
		computeGeneralized(besteps);
	}
	public double getLogLikelihood()
	{
		double logL = 0.0;
		for (int i = 0; i < size; i++) {
			double w = intervals.getInterval(i);
			double m = populationSize[i];
			double n = intervals.getIntervalCount();
			double nc2 = n*(n-1.0)/2.0;
			if (intervals.getIntervalType(i) == IntervalType.COALESCENT) {
				logL += Math.log(nc2/m) - w*nc2/m;
			} else {
				logL -=  w*nc2/m;
			}
		}
		return logL;
	}
	public double getAICC()
	{
		double logL = getLogLikelihood();
		return AICC(logL, params, size);
	}
	public static double AICC(double l, int k, int n)
	{
		if (k > n-2) throw new IllegalArgumentException("k must be smaller than n-1");
		return  l - k - (double) (k*(k+1.0))/ (double) (n - k - 1.0) ;
	}
	public double findInterval(double time)
	{
		if (time < 0) throw new IllegalArgumentException("Negative values for time are not allowed");
		for (int i = 0; i < size-1; i++)
		{
			if (time >= cis[i] && time < cis[i+1]) return i;
		}
		return size-1;
	}
	public double getMaxTime()
	{
		return maxTime;
	}
	public double getMaxPopulationSize() {
		double max = 0.0;
		for (int i = 0; i < size; i++) {
			if (populationSize[i] > max) {
				max = populationSize[i];
			}
		}
		return max;
	}
	public IntervalList getIntervals() {
		return intervals;
	}
	public int getSize() {
		return size;
	}
	public int getParameterCount() {
		return params;
	}
	public double getEpsilon() {
		return eps;
	}
	public double getPopulationSize(int i) {
		return populationSize[i];
	}
	public final Type getUnits() {
		return intervals.getUnits();
	} 
	public final void setUnits(Type units) {
		throw new IllegalArgumentException("Can't set skyline's units");
	}
	// private
	private IntervalList intervals;
	private int size;
	private double maxTime;
	private double eps;
	private double mu;
	private int params;
	private double[] cis;
	private double[] populationSize; 
}
