
package dr.evomodel.MSSD;

import dr.evolution.alignment.PatternList;
import dr.evomodel.branchratemodel.BranchRateModel;
import dr.evomodel.sitemodel.SiteModel;
import dr.evomodel.tree.TreeModel;
import dr.evomodel.treelikelihood.LikelihoodScalingProvider;
import dr.evomodel.treelikelihood.ScaleFactorsHelper;
import dr.evomodel.treelikelihood.TreeLikelihood;
import dr.inference.model.Model;

public class ALSTreeLikelihood extends TreeLikelihood {

    protected AbstractObservationProcess observationProcess;

    public ALSTreeLikelihood(AbstractObservationProcess observationProcess, PatternList patternList, TreeModel treeModel,
                             SiteModel siteModel, BranchRateModel branchRateModel, boolean useAmbiguities,
                             boolean storePartials, boolean forceRescaling) {
        super(patternList, treeModel, siteModel, branchRateModel, null, useAmbiguities, false, storePartials, false, forceRescaling);

        this.observationProcess = observationProcess;
        addModel(observationProcess);

        // TreeLikelihood does not initialize the partials for tips, we'll do it ourselves
        int extNodeCount = treeModel.getExternalNodeCount();
        for (int i = 0; i < extNodeCount; i++) {
            String id = treeModel.getTaxonId(i);
            int index = patternList.getTaxonIndex(id);
            setPartials(likelihoodCore, patternList, categoryCount, index, i);
        }

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

        scaleFactorsHelper = new ScaleFactorsHelper((LikelihoodScalingProvider) likelihoodCore, this,
                treeModel, stateCount, patternCount, categoryCount);
    }

    protected double calculateLogLikelihood() {
        // Calculate the partial likelihoods
        super.calculateLogLikelihood();
        // get the frequency model
        double[] freqs = frequencyModel.getFrequencies();
        // let the observationProcess handle the rest
        scaleFactorsHelper.resetScaleFactors();
        return observationProcess.nodePatternLikelihood(freqs, likelihoodCore, scaleFactorsHelper);
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
