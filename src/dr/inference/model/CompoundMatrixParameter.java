package dr.inference.model;
import dr.xml.*;
import java.util.ArrayList;
import java.util.List;
public class CompoundMatrixParameter extends MatrixParameter {
    public CompoundMatrixParameter(String name, List<MatrixParameter> matrices) {
        super(name, compoundMatrices(matrices));
    }
    private static Parameter[] compoundMatrices(List<MatrixParameter> matrices) {
        int length = 0;
        for (MatrixParameter matrix : matrices) {
            length += matrix.getColumnDimension();
        }
        Parameter[] parameters = new Parameter[length];
        int index = 0;
        for (MatrixParameter matrix : matrices) {
            for (int i = 0; i < matrix.getColumnDimension(); ++i) {
                parameters[index] = matrix.getParameter(i);
                ++index;
            }
        }
        return parameters;
    }
    public final static String COMPOUND_MATRIX_PARAMETER = "compoundMatrixParameter";
    public static XMLObjectParser PARSER = new AbstractXMLObjectParser() {
        public String getParserName() {
            return COMPOUND_MATRIX_PARAMETER;
        }
        public Object parseXMLObject(XMLObject xo) throws XMLParseException {
            List<MatrixParameter> matrices = new ArrayList<MatrixParameter>();
            for (int i = 0; i < xo.getChildCount(); ++i) {
                matrices.add((MatrixParameter) xo.getChild(i));
            }
            final String name = xo.hasId() ? xo.getId() : null;
            return new CompoundMatrixParameter(name, matrices);
        }
        //************************************************************************
        // AbstractXMLObjectParser implementation
        //************************************************************************
        public String getParserDescription() {
            return "A compound matrix parameter constructed from its component parameters.";
        }
        public XMLSyntaxRule[] getSyntaxRules() {
            return rules;
        }
        private final XMLSyntaxRule[] rules = {
                new ElementRule(MatrixParameter.class, 1, Integer.MAX_VALUE),
        };
        public Class getReturnType() {
            return CompoundMatrixParameter.class;
        }
    };
}
