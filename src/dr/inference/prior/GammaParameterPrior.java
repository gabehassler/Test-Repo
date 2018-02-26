//package dr.inference.prior;
//
//import dr.inference.model.Parameter;
//import dr.math.distributions.GammaDistribution;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//public class GammaParameterPrior extends AbstractParameterPrior {
//
//    /**
//     * the gamma distribution
//     */
//    GammaDistribution gammaDistribution;
//
//    public GammaParameterPrior(Parameter parameter, double gammaMean, double gammaShape) {
//        this(parameter, -1, gammaMean, gammaShape);
//    }
//
//    public GammaParameterPrior(Parameter parameter, int dimension, double gammaMean, double gammaShape) {
//        gammaDistribution = new GammaDistribution(gammaShape, gammaMean / gammaShape);
//        setParameter(parameter);
//        setDimension(dimension);
//    }
//
//    public double getLogPriorComponent(double value) {
//        return gammaDistribution.logPdf(value);
//       // return Math.log(gammaDistribution.pdf(value));
//    }
//
//    public Element createElement(Document d) {
//        Element e = d.createElement("gammaPrior");
//        e.setAttribute("mean", gammaDistribution.mean() + "");
//        e.setAttribute("shape", gammaDistribution.getShape() + "");
//        return e;
//    }
//
//    public final double getMean() {
//        return gammaDistribution.mean();
//    }
//
//    public final double getShape() {
//        return gammaDistribution.getShape();
//    }
//
//
//    public String toString() {
//        return "Gamma(" + formatter.format(getMean()).trim() + ", " + formatter.format(getShape()).trim() + ")";
//    }
//
//    public String toHTML() {
//        return "<font color=\"#FF00FF\">" + toString() + "</font>";
//    }
//}
