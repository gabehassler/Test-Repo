package dr.evomodel.coalescent.structure;
import dr.evolution.colouring.ColourChangeMatrix;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
import dr.xml.*;
public class ConstantMigrationModel extends MigrationModel {
    //
    // Public stuff
    //
    public static String CONSTANT_MIGRATION_MODEL = "constantMigrationModel";
    public static String MIGRATION_RATES = "migrationRates";
    public ConstantMigrationModel(int demeCount, Parameter migrationParameter) {
        this(CONSTANT_MIGRATION_MODEL, demeCount, migrationParameter);
    }
    public ConstantMigrationModel(String name, int demeCount, Parameter migrationParameter) {
        super(name);
        this.demeCount = demeCount;
        this.migrationParameter = migrationParameter;
        addVariable(migrationParameter);
        migrationParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, migrationParameter.getDimension()));
    }
    // general functions
    public ColourChangeMatrix getMigrationMatrix() {
        if (colourChangeMatrix == null) {
            colourChangeMatrix = new ColourChangeMatrix(migrationParameter.getParameterValues(), demeCount);
        }
        return colourChangeMatrix;
    }
    public double[] getMigrationRates(double time) {
        return migrationParameter.getParameterValues();
    }
    protected final void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
        colourChangeMatrix = null;
    }
    protected void restoreState() {
        colourChangeMatrix = null;
    }
    public static XMLObjectParser PARSER = new AbstractXMLObjectParser() {
        public String getParserName() {
            return CONSTANT_MIGRATION_MODEL;
        }
        public Object parseXMLObject(XMLObject xo) throws XMLParseException {
            int demeCount = 2;
            XMLObject cxo = xo.getChild(MIGRATION_RATES);
            Parameter migrationParameter = (Parameter) cxo.getChild(Parameter.class);
            return new ConstantMigrationModel(demeCount, migrationParameter);
        }
        //************************************************************************
        // AbstractXMLObjectParser implementation
        //************************************************************************
        public String getParserDescription() {
            return "A migration model representing constant migration rates through time.";
        }
        public Class getReturnType() {
            return ConstantMigrationModel.class;
        }
        public XMLSyntaxRule[] getSyntaxRules() {
            return rules;
        }
        private final XMLSyntaxRule[] rules = {
                new ElementRule(MIGRATION_RATES,
                        new XMLSyntaxRule[]{new ElementRule(Parameter.class)})
        };
    };
    //
    // protected stuff
    //
    private int demeCount;
    private Parameter migrationParameter;
    private ColourChangeMatrix colourChangeMatrix = null;
}
