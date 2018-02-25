//package dr.inference.prior;
//
//import dr.inference.model.Parameter;
//import dr.math.distributions.NormalDistribution;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//public class NormalParameterPrior extends AbstractParameterPrior {
//
//    /**
//     * the normal distribution
//     */
//    NormalDistribution normalDistribution;
//
//    public NormalParameterPrior(Parameter parameter, double mean, double stdev) {
//        this(parameter, -1, mean, stdev);
//    }
//
//    public NormalParameterPrior(Parameter parameter, int dimension, double mean, double stdev) {
//        normalDistribution = new NormalDistribution(mean, stdev);
//        setParameter(parameter);
//        setDimension(dimension);
//    }
//
//    public double getLogPriorComponent(double value) {
//        return Math.log(normalDistribution.pdf(value));
//    }
//
//    public Element createElement(Document d) {
//        Element e = d.createElement("normalPrior");
//        e.setAttribute("mean", normalDistribution.mean() + "");
//        e.setAttribute("stdev", normalDistribution.getSD() + "");
//        return e;
//    }
//
//    public final double getMean() {
//        return normalDistribution.mean();
//    }
//
//    public final double getStdev() {
//        return normalDistribution.getSD();
//    }
//
//    public String toString() {
//        return "Normal(" + formatter.format(getMean()).trim() + ", " + formatter.format(getStdev()).trim() + ")";
//    }
//
//    public String toHTML() {
//        return "<font color=\"#FF00FF\">" + toString() + "</font>";
//    }
//}
