package dr.app.beagle.evomodel.parsers;
import dr.app.beagle.evomodel.sitemodel.GammaSiteRateModel;
import dr.app.beagle.evomodel.treelikelihood.BeagleOperationReport;
import dr.evolution.alignment.Alignment;
import dr.evolution.alignment.PatternList;
import dr.evolution.alignment.SitePatterns;
import dr.evomodel.branchratemodel.BranchRateModel;
import dr.evomodel.tree.TreeModel;
import dr.xml.*;
import java.io.PrintWriter;
public class BeagleOperationParser extends AbstractXMLObjectParser {
    public static final String OPERATION_REPORT = "beagleOperationReport";
    public static final String BRANCH_FILE_NAME = "branchFileName";
    public static final String OPERATION_FILE_NAME = "operationFileName";
    public String getParserName() {
        return OPERATION_REPORT;
    }
    public Object parseXMLObject(XMLObject xo) throws XMLParseException {
        SitePatterns patternList = (SitePatterns) xo.getChild(PatternList.class);
        TreeModel treeModel = (TreeModel) xo.getChild(TreeModel.class);
        BranchRateModel rateModel = (BranchRateModel) xo.getChild(BranchRateModel.class);
        Alignment alignment = (Alignment) xo.getChild(Alignment.class);
        GammaSiteRateModel substitutionModel = (GammaSiteRateModel) xo.getChild(GammaSiteRateModel.class);
        PrintWriter branch = null, operation = null;
        if (xo.hasAttribute(BRANCH_FILE_NAME)) {
            branch = XMLParser.getFilePrintWriter(xo, OPERATION_REPORT, BRANCH_FILE_NAME);
        }
        if (xo.hasAttribute(OPERATION_FILE_NAME)) {
            operation = XMLParser.getFilePrintWriter(xo, OPERATION_REPORT, OPERATION_FILE_NAME);
        }
        return new BeagleOperationReport(treeModel, patternList, rateModel, substitutionModel, alignment, branch, operation);
    }
    //************************************************************************
    // AbstractXMLObjectParser implementation
    //************************************************************************
    public String getParserDescription() {
        return "This element represents the likelihood of a patternlist on a tree given the site model.";
    }
    public Class getReturnType() {
        return BeagleOperationReport.class;
    }
    public static final XMLSyntaxRule[] rules = {
            new ElementRule(PatternList.class, 2, 2),
            new ElementRule(TreeModel.class),
            new ElementRule(BranchRateModel.class),
            new ElementRule(GammaSiteRateModel.class),
//            new ElementRule(Alignment.class),
            AttributeRule.newStringRule(BRANCH_FILE_NAME, true),
            AttributeRule.newStringRule(OPERATION_FILE_NAME, true),
    };
    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }
}
