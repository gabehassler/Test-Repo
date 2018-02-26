
package dr.inferencexml.model;

import dr.inference.model.MatrixParameter;
import dr.inference.model.MatrixVectorProductParameter;
import dr.inference.model.Parameter;
import dr.xml.*;


public class MatrixVectorProductParameterParser extends AbstractXMLObjectParser {

    public static final String PRODUCT_PARAMETER = "matrixVectorProductParameter";
    public static final String MATRIX = "matrix";
    public static final String VECTOR = "vector";

    public Object parseXMLObject(XMLObject xo) throws XMLParseException {

        MatrixParameter matrix = (MatrixParameter) xo.getChild(MATRIX).getChild(MatrixParameter.class);
        Parameter vector = (Parameter) xo.getChild(VECTOR).getChild(Parameter.class);

        if (matrix.getColumnDimension() != vector.getDimension()) {
            throw new XMLParseException("Wrong matrix-vector dimensions in " + xo.getId());
        }

        return new MatrixVectorProductParameter(matrix, vector);
    }

    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }

    private final XMLSyntaxRule[] rules = {
            new ElementRule(MATRIX, new XMLSyntaxRule[]{
                    new ElementRule(MatrixParameter.class),
            }),
            new ElementRule(VECTOR, new XMLSyntaxRule[]{
                    new ElementRule(Parameter.class),
            }),
    };

    public String getParserDescription() {
        return "A matrix-vector product of parameters.";
    }

    public Class getReturnType() {
        return MatrixVectorProductParameter.class;
    }

    public String getParserName() {
        return PRODUCT_PARAMETER;
    }
}
