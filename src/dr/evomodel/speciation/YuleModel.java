package dr.evomodel.speciation;
//public class YuleModel extends UltrametricSpeciationModel {
//
//
//
//
//    public YuleModel(Parameter birthRateParameter, Type units) {
//
//        super(YULE_MODEL, units);
//
//        this.birthRateParameter = birthRateParameter;
//        addVariable(birthRateParameter);
//        birthRateParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
//    }
//
//    public double getBirthRate() {
//        return birthRateParameter.getParameterValue(0);
//    }
//
//    public void setBirthRate(double birthRate) {
//
//        birthRateParameter.setParameterValue(0, birthRate);
//    }
//
//    //
//    // functions that define a speciation model
//    //
//    public double logTreeProbability(int taxonCount) {
//        return 0.0;
//    }
//
//    //
//    // functions that define a speciation model
//    //
//    public double logNodeProbability(Tree tree, NodeRef node) {
//
//        double nodeHeight = tree.getNodeHeight(node);
//        final double lambda = getBirthRate();
//
//        double logP = 0;
//
//
//        if (tree.isRoot(node)) {
//            logP += -lambda * nodeHeight;
//        } else {
//        }
//        logP += Math.log(lambda);
//        logP += -lambda * nodeHeight;
//
//        return logP;
//    }
//
//    public boolean includeExternalNodesInLikelihoodCalculation() {
//        return false;
//    }
//
//    // **************************************************************
//    // XMLElement IMPLEMENTATION
//    // **************************************************************
//
//    public org.w3c.dom.Element createElement(org.w3c.dom.Document d) {
//        throw new RuntimeException("createElement not implemented");
//    }
//
//
//    //Protected stuff
//    Parameter birthRateParameter;
//
//}
