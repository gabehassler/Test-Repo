package dr.evomodelxml.speciation;


import dr.evomodel.speciation.AlloppSpeciesBindings;
import dr.evomodel.speciation.AlloppSpeciesNetworkModel;
import dr.inference.model.Parameter;
import dr.inference.model.ParameterParser;
import dr.util.Attributable;
import dr.xml.*;





  <alloppSpeciesNetwork id="apspnetwork" oneHybridization="true">
    <alloppSpecies idref="alloppSpecies"/>
    <sppSplitPopulations value="0.018">
      <parameter id="apspnetwork.splitPopSize"/>
    </sppSplitPopulations>
    <preRootHeights>
      <parameter id="pre.root.heights"/>
    </preRootHeights>
  </alloppSpeciesNetwork>





public class AlloppSpeciesNetworkModelParser extends AbstractXMLObjectParser {
	public static final String ALLOPPSPECIESNETWORK = "alloppSpeciesNetwork";
    public static final String ONEHYBRIDIZATION = "oneHybridization";
    public static final String DIPLOIDROOT_ISROOT = "diploidRootIsRoot";
    public static final String TIP_POPULATIONS = "tipPopulations";
    public static final String ROOT_POPULATIONS = "rootPopulations";
    public static final String HYBRID_POPULATIONS = "hybridPopulations";




    public String getParserName() {
		return ALLOPPSPECIESNETWORK;
	}

	@Override
	public Object parseXMLObject(XMLObject xo) throws XMLParseException {
		AlloppSpeciesBindings apspb = (AlloppSpeciesBindings) xo.getChild(AlloppSpeciesBindings.class);
        boolean onehyb = xo.getBooleanAttribute(ONEHYBRIDIZATION);
        boolean diprootisroot = xo.getBooleanAttribute(DIPLOIDROOT_ISROOT);

        final XMLObject tippopxo = xo.getChild(TIP_POPULATIONS);
		final double tippopvalue = tippopxo.getAttribute(Attributable.VALUE, 1.0);
        final XMLObject rootpopxo = xo.getChild(ROOT_POPULATIONS);
        final double rootpopvalue = rootpopxo.getAttribute(Attributable.VALUE, 1.0);
        final XMLObject hybpopxo = xo.getChild(HYBRID_POPULATIONS);
        final double hybpopvalue = hybpopxo.getAttribute(Attributable.VALUE, 1.0);

		AlloppSpeciesNetworkModel asnm = new AlloppSpeciesNetworkModel(apspb,
                tippopvalue, rootpopvalue, hybpopvalue, onehyb, diprootisroot);
		// don't know dimensionality until network created, so replace parameters
        ParameterParser.replaceParameter(tippopxo, asnm.tippopvalues);
        final Parameter.DefaultBounds tippopbounds =
                new Parameter.DefaultBounds(Double.MAX_VALUE, 0, asnm.tippopvalues.getDimension());
        asnm.tippopvalues.addBounds(tippopbounds);

        ParameterParser.replaceParameter(rootpopxo, asnm.rootpopvalues);
        final Parameter.DefaultBounds rootpopbounds =
                new Parameter.DefaultBounds(Double.MAX_VALUE, 0, asnm.rootpopvalues.getDimension());
        asnm.rootpopvalues.addBounds(rootpopbounds);

        ParameterParser.replaceParameter(hybpopxo, asnm.logginghybpopvalues);
        final Parameter.DefaultBounds hybpopbounds =
                new Parameter.DefaultBounds(Double.MAX_VALUE, 0, asnm.logginghybpopvalues.getDimension());
        asnm.logginghybpopvalues.addBounds(hybpopbounds);
        // note hybpopvalues are different and only work for logging.
        return asnm;
	}

	
	private ElementRule tippopElementRule() {
		return new ElementRule(TIP_POPULATIONS, new XMLSyntaxRule[]{
	                        AttributeRule.newDoubleRule(Attributable.VALUE, true),
	                        new ElementRule(Parameter.class)});
		}

    private ElementRule rootpopElementRule() {
        return new ElementRule(ROOT_POPULATIONS, new XMLSyntaxRule[]{
                AttributeRule.newDoubleRule(Attributable.VALUE, true),
                new ElementRule(Parameter.class)});
    }
    private ElementRule hybpopElementRule() {
        return new ElementRule(HYBRID_POPULATIONS, new XMLSyntaxRule[]{
                AttributeRule.newDoubleRule(Attributable.VALUE, true),
                new ElementRule(Parameter.class)});
    }



    @Override
	public XMLSyntaxRule[] getSyntaxRules() {
        return new XMLSyntaxRule[]{
                AttributeRule.newBooleanRule(ONEHYBRIDIZATION, true),
                AttributeRule.newBooleanRule(DIPLOIDROOT_ISROOT, true),
                new ElementRule(AlloppSpeciesBindings.class),
                tippopElementRule(),
                rootpopElementRule(),
                hybpopElementRule()
        };
	}

	@Override
	public String getParserDescription() {
		return "Species network with population sizes along branches";
	}

	@Override
	public Class getReturnType() {
		return AlloppSpeciesNetworkModel.class;
	}

}
