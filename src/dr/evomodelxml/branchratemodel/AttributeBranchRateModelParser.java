
package dr.evomodelxml.branchratemodel;

import dr.evomodel.branchratemodel.ArbitraryBranchRates;
import dr.evomodel.branchratemodel.AttributeBranchRateModel;
import dr.evomodel.tree.TreeModel;
import dr.inference.model.Parameter;
import dr.xml.*;

import java.util.logging.Logger;

public class AttributeBranchRateModelParser extends AbstractXMLObjectParser {

    public static final String RATE_ATTRIBUTE_NAME = "rateAttribute";

    public String getParserName() {
        return AttributeBranchRateModel.ATTRIBUTE_BRANCH_RATE_MODEL;
    }

    public Object parseXMLObject(XMLObject xo) throws XMLParseException {

        TreeModel tree = (TreeModel) xo.getChild(TreeModel.class);


        final String rateAttributeName = (xo.hasAttribute(RATE_ATTRIBUTE_NAME) ?
                xo.getStringAttribute(RATE_ATTRIBUTE_NAME) : null);

        return new AttributeBranchRateModel(tree, rateAttributeName);
    }

    //************************************************************************
    // AbstractXMLObjectParser implementation
    //************************************************************************

    public String getParserDescription() {
        return "This element returns a branch rate model." +
                "The branch rates are specified by an attribute embedded in the nodes of the tree.";
    }

    public Class getReturnType() {
        return AttributeBranchRateModel.class;
    }

    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }

    private final XMLSyntaxRule[] rules = {
            new ElementRule(TreeModel.class),
            new StringAttributeRule(RATE_ATTRIBUTE_NAME,
                    "Optional name of a rate attribute to be read with the trees")
    };


}
