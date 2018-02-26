package dr.evomodel.sitemodel;
import dr.inference.model.BooleanStatistic;
import dr.xml.*;
public class CategoryParameterStatistic extends BooleanStatistic {
    private static String MINIMUM_NUMBER = "minimumNumber";
    public CategoryParameterStatistic(String name,
                                      SampleStateAndCategoryModel siteModel,
                                      int minimumNumber) {
        super(name);
        this.minimumNumber = minimumNumber;
        this.siteModel = siteModel;
        this.categoryCount = siteModel.getCategoryCount();
    }
    public int getDimension() {
        return 1;
    }
    public boolean getBoolean(int dim) {
        for (int i = 0; i < categoryCount; i++) {
            if (siteModel.getSitesInCategoryCount(i) < minimumNumber)
                return false;
        }
        return true;
    }
    public static dr.xml.XMLObjectParser PARSER = new dr.xml.AbstractXMLObjectParser() {
        public String getParserName() {
            return "categoryParameterStatistic";
        }
        public Object parseXMLObject(XMLObject xo) throws XMLParseException {
            SampleStateAndCategoryModel siteModel = null;
            String name = xo.getAttribute("name", null);
            int minimum = xo.getAttribute(MINIMUM_NUMBER, 0);
            for (int i = 0; i < xo.getChildCount(); i++) {
                if (xo.getChild(i) instanceof SampleStateAndCategoryModel) {
                    siteModel = (SampleStateAndCategoryModel) xo.getChild(i);
                }
            }
            if (siteModel == null)
                throw new XMLParseException(getParserName() + " must contain a SampleStateAndCategoryModel.");
            if (minimum < 1) throw new XMLParseException(getParserName() + " minimum number must be greater than 0.");
            return new CategoryParameterStatistic(name, siteModel, minimum);
        }
        //************************************************************************
        // AbstractXMLObjectParser implementation
        //************************************************************************
        public String getParserDescription() {
            return "A statistic that returns true if the minimum number of sites in a category are present";
        }
        public Class getReturnType() {
            return CategoryParameterStatistic.class;
        }
        public XMLSyntaxRule[] getSyntaxRules() {
            return rules;
        }
        private XMLSyntaxRule[] rules = new XMLSyntaxRule[]{
                new StringAttributeRule(NAME, "A name for this statistic for the purposes of logging"),
                AttributeRule.newIntegerRule(MINIMUM_NUMBER),
                new ElementRule(SampleStateAndCategoryModel.class)
        };
    };
    private int minimumNumber;
    private int categoryCount;
    private SampleStateAndCategoryModel siteModel;
}
