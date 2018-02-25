package dr.app.beagle.evomodel.parsers;
import dr.app.beagle.evomodel.sitemodel.BranchSubstitutionModel;
import dr.app.beagle.evomodel.sitemodel.ExternalInternalBranchSubstitutionModel;
import dr.app.beagle.evomodel.substmodel.FrequencyModel;
import dr.app.beagle.evomodel.substmodel.SubstitutionModel;
import dr.xml.*;
import java.util.ArrayList;
import java.util.List;
@Deprecated
public class ExternalInternalBranchSubstitutionModelParser extends AbstractXMLObjectParser {
public static final String PARSER_NAME = "tipBranchSubstitutionModel";
public static final String INTERNAL = "internal";
public static final String EXTERNAL = "external";
@Override
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
List<SubstitutionModel> modelList = new ArrayList<SubstitutionModel>();
modelList.add((SubstitutionModel) xo.getElementFirstChild(INTERNAL));
modelList.add((SubstitutionModel) xo.getElementFirstChild(EXTERNAL));
List<FrequencyModel> freqList = new ArrayList<FrequencyModel>();
freqList.add((FrequencyModel) xo.getChild(FrequencyModel.class));
return new ExternalInternalBranchSubstitutionModel(modelList, freqList);
}
@Override
public XMLSyntaxRule[] getSyntaxRules() {
return new XMLSyntaxRule[] {
new ElementRule(INTERNAL, SubstitutionModel.class),
new ElementRule(EXTERNAL, SubstitutionModel.class),               
new ElementRule(FrequencyModel.class),
};
}
@Override
public String getParserDescription() {
return "A branch site model that uses different site models on the external and internal branches";
}
@Override
public Class getReturnType() {
return BranchSubstitutionModel.class;
}
public String getParserName() {
return PARSER_NAME;
}
}
