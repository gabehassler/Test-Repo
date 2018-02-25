package dr.evomodelxml.speciation;
import dr.evomodel.speciation.AlloppSpeciesBindings;
import dr.evomodel.speciation.MulSpeciesBindings;
import dr.evomodel.tree.TreeModel;
import dr.xml.*;
import java.util.ArrayList;
import java.util.List;
<mulSpecies id="mulSpecies">
<sp id="Alpha" ploidylevel = 2>
<individual id = "1">  
<taxon idref="1_Alpha" /> 
</individual>
<individual id = "2">  
<taxon idref="2_Alpha" /> 
</individual>
<individual id = "3">  
<taxon idref="3_Alpha" /> 
</individual>
</sp>
<sp id="Beta" ploidylevel = 4>
<individual id = "4">  
<taxon idref="4_Beta_A" /> 
<taxon idref="4_Beta_B" />
</individual>
<individual id = "5">  
<taxon idref="5_Beta_A" /> 
<taxon idref="5_Beta_B" />
</individual>
</sp>
...
(more species, then genetrees)
</mulSpecies>
// I have adapted code from SpeciesBindingsParser.
// I changed 'ploidy' to 'popfactor' to reduce confusion with the ploidy level
// of a species which is independent of which gene is considered.
// Use of popfactors is untested. Maybe chloroplast data would use it.
public class MulSpeciesBindingsParser extends AbstractXMLObjectParser {
public static final String MUL_SPECIES = "mulSpecies";
public static final String GENE_TREES = "geneTrees";
public static final String GTREE = "gtree";
public static final String POPFACTOR = "popfactor";
public String getParserName() {
return MUL_SPECIES;
}
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
List<AlloppSpeciesBindings.ApSpInfo> apsp = new ArrayList<AlloppSpeciesBindings.ApSpInfo>();
for (int k = 0; k < xo.getChildCount(); ++k) {
final Object child = xo.getChild(k);
if (child instanceof AlloppSpeciesBindings.ApSpInfo) {
apsp.add((AlloppSpeciesBindings.ApSpInfo) child);
}
}
final XMLObject xogt = xo.getChild(GENE_TREES);
final int nTrees = xogt.getChildCount();
final TreeModel[] trees = new TreeModel[nTrees];
double[] popFactors = new double[nTrees];
for (int nt = 0; nt < trees.length; ++nt) {
Object child = xogt.getChild(nt);
if (!(child instanceof TreeModel)) {
assert child instanceof XMLObject;
popFactors[nt] = ((XMLObject) child).getDoubleAttribute(POPFACTOR);
child = ((XMLObject) child).getChild(TreeModel.class);
} else {
popFactors[nt] = -1;
}
trees[nt] = (TreeModel) child;
}
try {
return new MulSpeciesBindings(apsp.toArray(new AlloppSpeciesBindings.ApSpInfo[apsp.size()]),
trees, popFactors);
} catch (Error e) {
throw new XMLParseException(e.getMessage());
}
}
// I have adapted code from SpeciesBindingsParser 
// I changed 'Ploidy' to 'PopFactors' to reduce confusion -
// the only use I can think of for popfactors in an AlloppNetwork is chloroplast data 
ElementRule treeWithPopFactors = new ElementRule(GTREE,
new XMLSyntaxRule[]{AttributeRule.newDoubleRule(POPFACTOR),
new ElementRule(TreeModel.class)}, 0, Integer.MAX_VALUE);
@Override
public XMLSyntaxRule[] getSyntaxRules() {
return new XMLSyntaxRule[]{
new ElementRule(AlloppSpeciesBindings.ApSpInfo.class, 2, Integer.MAX_VALUE),
new ElementRule(GENE_TREES,
new XMLSyntaxRule[]{
new ElementRule(TreeModel.class, 0, Integer.MAX_VALUE),
treeWithPopFactors
}),
};
}	
@Override
public String getParserDescription() {
return "Binds taxa to gene trees with information about possibly allopolyploid species.";
}
@Override
public Class getReturnType() {
return MulSpeciesBindings.class;
}
}