
package dr.evomodelxml;

import dr.util.FileHelpers;
import dr.xml.*;
import dr.evolution.io.NexusImporter;
import dr.evolution.io.Importer;
import dr.evolution.tree.Tree;
import dr.evolution.util.TaxonList;
import dr.evomodel.tree.EmpiricalTreeDistributionModel;
import dr.evomodel.tree.TreeModel;
import dr.evomodel.operators.WilsonBalding;
import dr.evomodel.operators.EmpiricalTreeDistributionOperator;
import dr.inference.operators.MCMCOperator;

import java.io.*;

public class EmpiricalTreeDistributionOperatorParser extends AbstractXMLObjectParser {

    public String getParserName() {
        return EmpiricalTreeDistributionOperator.EMPIRICAL_TREE_DISTRIBUTION_OPERATOR;
    }

    public Object parseXMLObject(XMLObject xo) throws XMLParseException {

        final double weight = xo.getDoubleAttribute(MCMCOperator.WEIGHT);
        boolean metropolisHastings = false;

        if (xo.hasAttribute(EmpiricalTreeDistributionOperator.METROPOLIS_HASTINGS)) {
            metropolisHastings = xo.getBooleanAttribute(EmpiricalTreeDistributionOperator.METROPOLIS_HASTINGS);
        }

        final EmpiricalTreeDistributionModel treeModel = (EmpiricalTreeDistributionModel) xo.getChild(EmpiricalTreeDistributionModel.class);

        return new EmpiricalTreeDistributionOperator(treeModel, metropolisHastings, weight);
    }

    //************************************************************************
    // AbstractXMLObjectParser implementation
    //************************************************************************

    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }

    private final XMLSyntaxRule[] rules = {
            AttributeRule.newDoubleRule(MCMCOperator.WEIGHT),
            AttributeRule.newBooleanRule(EmpiricalTreeDistributionOperator.METROPOLIS_HASTINGS, true),
            new ElementRule(EmpiricalTreeDistributionModel.class)
    };

    public String getParserDescription() {
        return "Operator which switches between trees in an empirical distribution.";
    }

    public Class getReturnType() {
        return EmpiricalTreeDistributionOperator.class;
    }
}
