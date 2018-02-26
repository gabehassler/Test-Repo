package dr.app.beagle.evomodel.treelikelihood;
import dr.app.beagle.evomodel.branchmodel.BranchModel;
import dr.app.beagle.evomodel.sitemodel.SiteRateModel;
import dr.evolution.alignment.PatternList;
import dr.evomodel.MSSD.AbstractObservationProcess;
import dr.evomodel.branchratemodel.BranchRateModel;
import dr.evomodel.tree.TreeModel;
import dr.evomodel.treelikelihood.LikelihoodPartialsProvider;
import dr.evomodel.treelikelihood.ScaleFactorsHelper;
import dr.evomodel.treelikelihood.TipStatesModel;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import java.util.Map;
import java.util.Set;
public class ALSBeagleTreeLikelihood extends BeagleTreeLikelihood implements LikelihoodPartialsProvider {
    protected AbstractObservationProcess observationProcess;
    public ALSBeagleTreeLikelihood(AbstractObservationProcess observationProcess, PatternList patternList, TreeModel treeModel,
                                   BranchModel branchModel,
                                   SiteRateModel siteRateModel,
                                   BranchRateModel branchRateModel,
                                   TipStatesModel tipStatesModel,
                                   boolean useAmbiguities,
                                   PartialsRescalingScheme scalingScheme,
                                   Map<Set<String>, Parameter> partialsRestrictions) {
        super(patternList, treeModel, branchModel, siteRateModel, branchRateModel, tipStatesModel, useAmbiguities, scalingScheme,
                partialsRestrictions);
//    }
//
//
//    public ALSBeagleTreeLikelihood(AbstractObservationProcess observationProcess, PatternList patternList, TreeModel treeModel,
//                SiteModel siteModel, BranchRateModel branchRateModel,
//        boolean useAmbiguities, boolean storePartials) {
//        super(patternList, treeModel, siteModel, branchRateModel, null, useAmbiguities, false, storePartials, false, false);
        this.observationProcess = observationProcess;
        addModel(observationProcess);
//        // TreeLikelihood does not initialize the partials for tips, we'll do it ourselves
//        int extNodeCount = treeModel.getExternalNodeCount();
//        for (int i = 0; i < extNodeCount; i++) {
//            String id = treeModel.getTaxonId(i);
//            int index = patternList.getTaxonIndex(id);
//            setPartials(likelihoodCore, patternList, categoryCount, index, i);
//        }
        double totalTime=0.0;
        double realTime = 0.0;
        for(int i=0; i<treeModel.getNodeCount();++i){
            NodeRef node = treeModel.getNode(i);
            double branchRate = branchRateModel.getBranchRate(treeModel,node);
            double branchTime = treeModel.getBranchLength(node);
            totalTime+=branchRate*branchTime;
            realTime += branchTime;
            System.err.println("Node "+node.toString()+ " time: "+branchTime+ " rate "+branchRate+" together "+branchTime*branchRate);
        }
        System.err.println("TotalTime: "+totalTime);
        System.err.println("RealTime: "+realTime);*/
        scaleFactorsHelper = new ScaleFactorsHelper(this, this,
                treeModel, stateCount, patternCount, categoryCount);
    }
    protected double calculateLogLikelihood() {
        // Calculate the partial likelihoods
        super.calculateLogLikelihood();
        // get the frequency model
        double[] freqs = substitutionModelDelegate.getRootStateFrequencies();
        // let the observationProcess handle the rest
        scaleFactorsHelper.resetScaleFactors();
        return observationProcess.nodePatternLikelihood(freqs, this, scaleFactorsHelper);
    }
    protected void handleModelChangedEvent(Model model, Object object, int index) {
        if (model == observationProcess) {
            likelihoodKnown = false;
        } else {
            super.handleModelChangedEvent(model, object, index);
        }
    }
    final private ScaleFactorsHelper scaleFactorsHelper;
}
