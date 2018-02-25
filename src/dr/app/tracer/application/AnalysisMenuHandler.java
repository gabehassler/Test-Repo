package dr.app.tracer.application;
import javax.swing.*;
public interface AnalysisMenuHandler {
Action getDemographicAction();
Action getBayesianSkylineAction();
Action getExtendedBayesianSkylineAction();
Action getSkyGridAction();
Action getGMRFSkyrideAction();
Action getLineagesThroughTimeAction();
Action getTraitThroughTimeAction();
Action getCreateTemporalAnalysisAction();
Action getAddDemographicAction();
Action getAddBayesianSkylineAction();
Action getAddExtendedBayesianSkylineAction();
Action getAddTimeDensityAction();
Action getBayesFactorsAction();
Action getConditionalPosteriorDistAction();
}
