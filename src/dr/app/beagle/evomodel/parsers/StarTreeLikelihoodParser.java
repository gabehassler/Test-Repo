
package dr.app.beagle.evomodel.parsers;

import dr.app.beagle.evomodel.sitemodel.BranchSubstitutionModel;
import dr.app.beagle.evomodel.sitemodel.GammaSiteRateModel;
import dr.app.beagle.evomodel.treelikelihood.OldBeagleTreeLikelihood;
import dr.app.beagle.evomodel.treelikelihood.PartialsRescalingScheme;
import dr.app.beagle.evomodel.treelikelihood.StarTreeLikelihood;
import dr.evolution.alignment.PatternList;
import dr.evomodel.branchratemodel.BranchRateModel;
import dr.evomodel.tree.TreeModel;
import dr.inference.model.Parameter;
import dr.xml.XMLObject;
import dr.xml.XMLParseException;
import dr.xml.XMLSyntaxRule;

import java.util.Map;
import java.util.Set;


public class StarTreeLikelihoodParser extends OldTreeLikelihoodParser {

    public static final String STAR_TREE = "starTreeLikelihood";
//    public static final String RECONSTRUCTION_TAG = AncestralStateTreeLikelihood.STATES_KEY;
//    public static final String RECONSTRUCTION_TAG_NAME = "stateTagName";
//    public static final String MAP_RECONSTRUCTION = "useMAP";
//    public static final String MARGINAL_LIKELIHOOD = "useMarginalLikelihood";

    public String getParserName() {
        return STAR_TREE;
    }

    protected OldBeagleTreeLikelihood createTreeLikelihood(PatternList patternList, TreeModel treeModel,
                                                     BranchSubstitutionModel branchSubstitutionModel, GammaSiteRateModel siteRateModel,
                                                     BranchRateModel branchRateModel,
                                                     boolean useAmbiguities, PartialsRescalingScheme scalingScheme,
                                                     Map<Set<String>, Parameter> partialsRestrictions,
                                                     XMLObject xo) throws XMLParseException {
           return new StarTreeLikelihood(
                    patternList,
                    treeModel,
                   branchSubstitutionModel,
                    siteRateModel,
                    branchRateModel,
                    useAmbiguities,
                    scalingScheme,
                    partialsRestrictions
            );
    }

    public XMLSyntaxRule[] getSyntaxRules() {
        return OldTreeLikelihoodParser.rules;
    }
}
