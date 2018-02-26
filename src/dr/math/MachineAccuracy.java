package dr.math;
public class MachineAccuracy
{
	//
	// Public stuff
	//
	public static double EPSILON = 2.220446049250313E-16;
	public static double SQRT_EPSILON = 1.4901161193847656E-8;
	public static double SQRT_SQRT_EPSILON = 1.220703125E-4;
	public static double computeEpsilon()
	{
		double eps = 1.0;
		while( eps + 1.0 != 1.0 )
		{
			eps /= 2.0;
		}
		eps *= 2.0;
		return eps;
	}
	public static boolean same(double a, double b) {
		return Math.abs((a/b)-1.0) <= SQRT_EPSILON;
	}
}
