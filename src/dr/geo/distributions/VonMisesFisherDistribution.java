package dr.geo.distributions;

import dr.geo.math.Space;
import dr.math.LogTricks;

public class VonMisesFisherDistribution extends HyperSphereDistribution {

    public VonMisesFisherDistribution(int dim, Space space, double[] mean, double kappa) {
        super(dim, space, mean, kappa);
    }

    public double logPdf(double[] x) {
        return logPdf(x, mean, kappa, space);
    }

    public String getType() {
        return "von Mises-Fisher";
    }

    protected int getAllowableDim() {
        return 3;
    }

    private static double logNormalizationConstant(double kappa) {
        // 'sinh' has some numerical instability for small arguments
        if (kappa < 1E-10) {
            return -Math.log(2) - LOG_2_PI;
        }
        return Math.log(kappa) - LOG_2_PI - LogTricks.logDiff(kappa, -kappa);
//        return Math.log(kappa) - LOG_2_PI - Math.log(Math.exp(+kappa) - Math.exp(-kappa));
    }

    public static double logPdf(double[] x, double[] mean, double kappa, Space space) {
        return logNormalizationConstant(kappa) + kappa * HyperSphereDistribution.innerProduct(x, mean, space);
    }

    public static void main(String[] arg) {
        // Test in cartesian coordinates
        double kappa1 = 1;
        double[] mean1 = { 1,0,0 };
        double[] x1 = { 0,1,0 };
        System.err.println("logP = "+logPdf(x1, mean1, kappa1, Space.CARTESIAN)+" ?= -2.692464\n");


        // Test in (lat,long) coordinates
        double kappa2 = 1;
        double[] mean2 = { 0,0 };
        double[] x2 = { 0,180 };
        System.err.println("logP = "+logPdf(x2, mean2, kappa2, Space.LAT_LONG )+" ?= -3.692464\n");

        // Test in (lat,long) coordinates
        double kappa3 = 2;
        double[] mean3 = { 90,0 };
        double[] x3 = { 0,180 };
        System.err.println("logP = "+logPdf(x3, mean3, kappa3, Space.LAT_LONG )+" ?= -3.126244");
    }
}


// R test code
//
//x = c(1,0,0)
//y = c(0,1,0)
//
//vmf = function(x, mean, kappa, p) {
//	order = p/2 - 1
//	norm = kappa^(order) / (2*pi)^(p/2) / besselI(kappa,order)
//	norm * exp(kappa * sum(x * mean))
//}
//
//log(vmf(x,y,1,3))
//
//pt1 = c(0,1,0)  # lat_long (0,0)
//pt2 = c(0,-1,0)  # lat_long (0,180)
//
//log(vmf(pt1,pt2,1,3))
//
//pt3 = c(0,0,1)  # lat_long (90,0)
//pt4 = c(0,-1,0)  # lat_long (0,180)
//
//log(vmf(pt3,pt4,2,3))