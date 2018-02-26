
package dr.app.beagle.evomodel.parsers;

import dr.app.beagle.evomodel.branchmodel.BranchSpecificBranchModel;
import dr.app.beagle.evomodel.branchmodel.EpochBranchModel;
import dr.app.beagle.evomodel.substmodel.SubstitutionModel;
import dr.evolution.tree.Tree;
import dr.evolution.util.Taxa;
import dr.evolution.util.TaxonList;
import dr.evomodel.branchratemodel.RateEpochBranchRateModel;
import dr.evomodel.tree.TreeModel;
import dr.inference.model.CompoundParameter;
import dr.inference.model.Parameter;
import dr.xml.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class BranchSpecificBranchModelParser extends AbstractXMLObjectParser {

    public static final String BRANCH_SPECIFIC_SUBSTITUTION_MODEL = "branchSpecificSubstitutionModel";
    public static final String CLADE = "clade";
    public static final String EXTERNAL_BRANCHES = "externalBranches";
    public static final String BACKBONE = "backbone";
    public static final String STEM_WEIGHT = "stemWeight";

    public String getParserName() {
        return BRANCH_SPECIFIC_SUBSTITUTION_MODEL;
    }

    public Object parseXMLObject(XMLObject xo) throws XMLParseException {

        Logger.getLogger("dr.evomodel").info("Using clade-specific branch model.");

        TreeModel tree = (TreeModel) xo.getChild(TreeModel.class);
        SubstitutionModel substitutionModel = (SubstitutionModel) xo.getChild(SubstitutionModel.class);
        BranchSpecificBranchModel branchModel = new BranchSpecificBranchModel(tree, substitutionModel);

        for (int i = 0; i < xo.getChildCount(); i++) {
            if (xo.getChild(i) instanceof XMLObject) {

                XMLObject xoc = (XMLObject) xo.getChild(i);
                if (xoc.getName().equals(CLADE)) {

                    double stemWeight = xoc.getAttribute(STEM_WEIGHT, 0.0);

                    substitutionModel = (SubstitutionModel) xoc.getChild(SubstitutionModel.class);
                    TaxonList taxonList = (TaxonList) xoc.getChild(TaxonList.class);

                    if (taxonList.getTaxonCount() == 1) {
                        throw new XMLParseException("A clade must be defined by at least two taxa");
                    }

                    try {
                        branchModel.addClade(taxonList, substitutionModel, stemWeight);

                    } catch (Tree.MissingTaxonException mte) {
                        throw new XMLParseException("Taxon, " + mte + ", in " + getParserName() + " was not found in the tree.");
                    }
                } else if (xoc.getName().equals(EXTERNAL_BRANCHES)) {

                    substitutionModel = (SubstitutionModel) xoc.getChild(SubstitutionModel.class);
                    TaxonList taxonList = (TaxonList) xoc.getChild(TaxonList.class);


                    try {
                        branchModel.addExternalBranches(taxonList, substitutionModel);

                    } catch (Tree.MissingTaxonException mte) {
                        throw new XMLParseException("Taxon, " + mte + ", in " + getParserName() + " was not found in the tree.");
                    }
                } else if (xoc.getName().equals(BACKBONE)) {

                    substitutionModel = (SubstitutionModel) xoc.getChild(SubstitutionModel.class);
                    TaxonList taxonList = (TaxonList) xoc.getChild(TaxonList.class);

                    try {
                        branchModel.addBackbone(taxonList, substitutionModel);

                    } catch (Tree.MissingTaxonException mte) {
                        throw new XMLParseException("Taxon, " + mte + ", in " + getParserName() + " was not found in the tree.");
                    }
                }

            }
        }

        return branchModel;
    }

    //************************************************************************
    // AbstractXMLObjectParser implementation
    //************************************************************************

    public String getParserDescription() {
        return
                "This element provides a branch model which allows different substitution models" +
                        "on different parts of the tree.";
    }

    public Class getReturnType() {
        return BranchSpecificBranchModel.class;
    }

    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }

    private final XMLSyntaxRule[] rules = {
            new ElementRule(TreeModel.class, "The tree"),
            new ElementRule(SubstitutionModel.class, "The substitution model for branches not explicitly included"),
            new ElementRule(EXTERNAL_BRANCHES,
                    new XMLSyntaxRule[]{
                            new ElementRule(Taxa.class, "A substitution model will be applied to the external branches for these taxa"),
                            new ElementRule(SubstitutionModel.class, "The substitution model"),
                    }, 0, Integer.MAX_VALUE),
            new ElementRule(CLADE,
                    new XMLSyntaxRule[]{
                            AttributeRule.newDoubleRule(STEM_WEIGHT, true, "What proportion of the stem branch to include [0 <= w <= 1] (default 0)."),
                            new ElementRule(Taxa.class, "A set of taxa which defines a clade to apply a different site model to"),
                            new ElementRule(SubstitutionModel.class, "The substitution model"),
                    }, 0, Integer.MAX_VALUE),
            new ElementRule(BACKBONE,
                    new XMLSyntaxRule[]{
                            new ElementRule(Taxa.class, "A substitution model will be applied only to " +
                                    "the 'backbone' branches defined by these taxa."),
                            new ElementRule(SubstitutionModel.class, "The substitution model"),
                    }, 0, Integer.MAX_VALUE),
    };

}
