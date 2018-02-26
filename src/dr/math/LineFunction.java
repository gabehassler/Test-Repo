package dr.math;
public class LineFunction implements UnivariateFunction
{
	public LineFunction(MultivariateFunction func)
	{
		f = func;
		dim = f.getNumArguments();
		x = new double[dim];
	}
	public void update(double[] start, double[] dir)
	{
		s = start;
		d = dir;
		computeBounds();
	}
	public void getPoint(double lambda, double[] p)
	{
		for (int i = 0; i < dim; i++)
		{
			p[i] = s[i] + lambda*d[i];
		}
	}
	// implementation of UnivariateFunction
	public double evaluate(double lambda)
	{
		getPoint(lambda, x);
		return f.evaluate(x);
	}
	public double getLowerBound()
	{
		return lowerBound;
	}
	public double getUpperBound()
	{
		return upperBound;
	}
	public double findMinimum()
	{
		if (um == null)
		{
			um = new UnivariateMinimum();
		}
		return um.findMinimum(this);
	}
	public int getUpperBoundParameter()
	{
		return upperBoundParam;
	}
	public int getLowerBoundParameter()
	{
		return lowerBoundParam;
	}
	public boolean checkPoint(double[] p)
	{
		boolean modified = false;
		for (int i = 0; i < dim; i++)
		{
			if (p[i] < f.getLowerBound(i))
			{
				p[i] = f.getLowerBound(i);
				modified = true;
			}
			if (p[i] > f.getUpperBound(i))
			{
				p[i] = f.getUpperBound(i);
				modified = true;
			}
		}
		return modified;
	}
	public int checkVariables(double[] p, double[] grad, boolean[] active)
	{
		// this seems to be a reasonable small value
		double EPS = MachineAccuracy.SQRT_EPSILON;
		int numActive = 0;
		for (int i = 0; i < dim; i++)
		{
			active[i] = true;
			if (p[i] <= f.getLowerBound(i)+EPS)
			{
				// no search towards lower boundary
				if (grad[i] > 0)
				{
					active[i] = false;
				}
			}
			else if (p[i] >= f.getUpperBound(i)-EPS)
			{
				// no search towards upper boundary
				if (grad[i] < 0)
				{
					active[i] = false;
				}
			}
			else
			{
				numActive++;
			}
		}
		return numActive;
	}
	public int checkDirection(double[] p, double[] dir)
	{
		// this seems to be a reasonable small value
		double EPS = MachineAccuracy.SQRT_EPSILON;
		int numChanged = 0;
		for (int i = 0; i < dim; i++)
		{
			if (p[i] <= f.getLowerBound(i)+EPS)
			{
				// no search towards lower boundary
				if (dir[i] < 0)
				{
					dir[i] = 0;
					numChanged++;
				}
			}
			else if (p[i] >= f.getUpperBound(i)-EPS)
			{
				// no search towards upper boundary
				if (dir[i] > 0)
				{
					dir[i] = 0;
					numChanged++;
				}
			}
		}
		return numChanged;
	}
	//
	// Private stuff
	//
	private MultivariateFunction f;
	private int lowerBoundParam, upperBoundParam;
	private int dim;
	private double lowerBound, upperBound;
	private double[] s, d, x;
	private UnivariateMinimum um = null;
	private void computeBounds()
	{
		boolean firstVisit = true;
		for (int i = 0; i < dim; i++)
		{
			if (d[i] != 0)
			{
				double upper = (f.getUpperBound(i) - s[i])/d[i];
				double lower = (f.getLowerBound(i) - s[i])/d[i];
				if (lower > upper)
				{
					double tmp = upper;
					upper = lower;
					lower = tmp;
				}
				if (firstVisit)
				{
					lowerBound = lower;
					lowerBoundParam = i;
					upperBound = upper;
					upperBoundParam = i;
					firstVisit = false;
				}
				else
				{
					if (lower > lowerBound)
					{
						lowerBound = lower;
						lowerBoundParam = i;
					}
					if (upper < upperBound)
					{
						upperBound = upper;
						upperBoundParam = i;
					}
				}
			}
		}
	}
}
