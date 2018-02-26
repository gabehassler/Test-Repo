
package dr.geo.operators;

import dr.geo.GeoSpatialDistribution;
import dr.geo.MultiRegionGeoSpatialDistribution;
import dr.geo.Polygon2D;
import dr.inference.distribution.MultivariateDistributionLikelihood;
import dr.inference.model.Parameter;
import dr.inference.operators.MCMCOperator;
import dr.inference.operators.UniformOperator;
import dr.xml.*;

import java.util.ArrayList;
import java.util.List;

public class UniformGeoSpatialOperatorParser extends AbstractXMLObjectParser {
    public final static String UNIFORM_OPERATOR = "uniformGeoSpatialOperator";
    public static final String LOWER = "lower";
    public static final String UPPER = "upper";

    public String getParserName() {
        return UNIFORM_OPERATOR;
    }

    public Object parseXMLObject(XMLObject xo) throws XMLParseException {

        double weight = xo.getDoubleAttribute(MCMCOperator.WEIGHT);
        Parameter parameter = (Parameter) xo.getChild(Parameter.class);

        if( parameter.getDimension() == 0 ) {
             throw new XMLParseException("parameter with 0 dimension.");
        }

        MultivariateDistributionLikelihood likelihood = (MultivariateDistributionLikelihood)
                xo.getChild(MultivariateDistributionLikelihood.class);

        List<Polygon2D> polygonList = new ArrayList<Polygon2D>();

        if (likelihood.getDistribution() instanceof MultiRegionGeoSpatialDistribution) {
            for (GeoSpatialDistribution spatial : ((MultiRegionGeoSpatialDistribution) likelihood.getDistribution()).getRegions()) {
                polygonList.add(spatial.getRegion());
            }
        } else if (likelihood.getDistribution() instanceof GeoSpatialDistribution) {
            polygonList.add(
                    ((GeoSpatialDistribution) likelihood.getDistribution()).getRegion()
            );
        } else {
            throw new XMLParseException("Multivariate distribution must be either a GeoSpatialDistribution " +
                "or a MultiRegionGeoSpatialDistribution");
        }

        return new UniformGeoSpatialOperator(parameter, weight, polygonList);
    }

    //************************************************************************
    // AbstractXMLObjectParser implementation
    //************************************************************************

    public String getParserDescription() {
        return "An operator that picks new parameter values uniformly at random.";
    }

    public Class getReturnType() {
        return UniformOperator.class;
    }


    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }

    private final XMLSyntaxRule[] rules = {
            AttributeRule.newDoubleRule(MCMCOperator.WEIGHT),
//            AttributeRule.newDoubleRule(LOWER, true),
//            AttributeRule.newDoubleRule(UPPER, true),
            new ElementRule(Parameter.class),
            new ElementRule(MultivariateDistributionLikelihood.class),
    };
}
