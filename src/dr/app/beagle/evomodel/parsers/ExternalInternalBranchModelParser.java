package dr.app.beagle.evomodel.parsers;
import dr.app.beagle.evomodel.branchmodel.BranchSpecificBranchModel;
import dr.app.beagle.evomodel.branchmodel.ExternalInternalBranchModel;
import dr.app.beagle.evomodel.substmodel.SubstitutionModel;
import dr.evolution.tree.Tree;
import dr.evolution.util.Taxa;
import dr.evolution.util.TaxonList;
import dr.evomodel.tree.TreeModel;
import dr.xml.*;
import java.util.logging.Logger;
public class ExternalInternalBranchModelParser extends AbstractXMLObjectParser {
    public static final String EXTERNAL_INTERNAL_BRANCH_MODEL = "externalInternalBranchModel";
    public static final String EXTERNAL_BRANCHES = "externalBranches";
    public String getParserName() {
        return EXTERNAL_INTERNAL_BRANCH_MODEL;
    }
    public Object parseXMLObject(XMLObject xo) throws XMLParseException {
        Logger.getLogger("dr.evomodel").info("Using external-internal branch model.");
        TreeModel tree = (TreeModel) xo.getChild(TreeModel.class);
        SubstitutionModel internalSubstitutionModel = (SubstitutionModel) xo.getChild(SubstitutionModel.class);
        SubstitutionModel externalSubstitutionModel = (SubstitutionModel) xo.getElementFirstChild(EXTERNAL_BRANCHES);
        return new ExternalInternalBranchModel(tree, externalSubstitutionModel, internalSubstitutionModel);
    }
    //************************************************************************
    // AbstractXMLObjectParser implementation
    //************************************************************************
    public String getParserDescription() {
        return
                "This element provides a branch model which allows different substitution models" +
                        "on internal and external branches of the tree.";
    }
    public Class getReturnType() {
        return BranchSpecificBranchModel.class;
    }
    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }
    private final XMLSyntaxRule[] rules = {
            new ElementRule(TreeModel.class, "The tree"),
            new ElementRule(SubstitutionModel.class, "The substitution model for internal branches"),
            new ElementRule(EXTERNAL_BRANCHES,
                    new XMLSyntaxRule[]{
                            new ElementRule(SubstitutionModel.class, "The external branch substitution model"),
                    }, false)
    };
}
