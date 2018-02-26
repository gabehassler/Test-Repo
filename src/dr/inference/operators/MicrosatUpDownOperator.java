
package dr.inference.operators;

import dr.math.MathUtils;

public class MicrosatUpDownOperator extends AbstractCoercableOperator {

    private Scalable.Default[] upParameter = null;
    private Scalable.Default[] downParameter = null;
    private double scaleFactor;

    public MicrosatUpDownOperator(Scalable.Default[] upParameter,
                                  Scalable.Default[] downParameter,
                                  double scale,
                                  double weight,
                                  CoercionMode mode) {

        super(mode);
        setWeight(weight);

        this.upParameter = upParameter;
        this.downParameter = downParameter;
        this.scaleFactor = scale;
    }

    public final double getScaleFactor() {
        return scaleFactor;
    }

    public final void setScaleFactor(double sf) {
        if( (sf > 0.0) && (sf < 1.0) ) {
            scaleFactor = sf;
        } else {
            throw new IllegalArgumentException("scale must be between 0 and 1");
        }
    }

    public final double doOperation() throws OperatorFailedException {


        final double scale = (scaleFactor + (MathUtils.nextDouble() * ((1.0 / scaleFactor) - scaleFactor)));
        int goingUp = 0, goingDown = 0;

        if( upParameter != null ) {
            for( Scalable.Default up : upParameter ) {
                goingUp += up.scaleAllAndNotify(scale, -1);
            }
        }

        if( downParameter != null ) {
            for(Scalable.Default dn : downParameter ) {
                goingDown += dn.scaleAllAndNotify(1.0 / scale, -1);
            }
        }

        return (goingUp - goingDown - 2) * Math.log(scale);
    }

    public final String getPerformanceSuggestion() {

        double prob = MCMCOperator.Utils.getAcceptanceProbability(this);
        double targetProb = getTargetAcceptanceProbability();
        double sf = OperatorUtils.optimizeScaleFactor(scaleFactor, prob, targetProb);
        dr.util.NumberFormatter formatter = new dr.util.NumberFormatter(5);
        if (prob < getMinimumGoodAcceptanceLevel()) {
            return "Try setting scaleFactor to about " + formatter.format(sf);
        } else if (prob > getMaximumGoodAcceptanceLevel()) {
            return "Try setting scaleFactor to about " + formatter.format(sf);
        } else return "";
    }

    public final String getOperatorName() {
        String name = "";
        if( upParameter != null ) {
            name = "up:";
            for( Scalable up : upParameter ) {
                name = name + up.getName() + " ";
            }
        }

        if( downParameter != null ) {
            name += "down:";
            for( Scalable dn : downParameter ) {
                name = name + dn.getName() + " ";
            }
        }
        return name;
    }

    public double getCoercableParameter() {
        return Math.log(1.0 / scaleFactor - 1.0) / Math.log(10);
    }

    public void setCoercableParameter(double value) {
        scaleFactor = 1.0 / (Math.pow(10.0, value) + 1.0);
    }

    public double getRawParameter() {
        return scaleFactor;
    }

    public double getTargetAcceptanceProbability() {
        return 0.234;
    }

    // Since this operator invariably modifies at least 2 parameters it
    // should allow lower acceptance probabilities
    // as it is known that optimal acceptance levels are inversely
    // proportional to the number of dimensions operated on
    // AD 16/3/2004
    public double getMinimumAcceptanceLevel() {
        return 0.05;
    }

    public double getMaximumAcceptanceLevel() {
        return 0.3;
    }

    public double getMinimumGoodAcceptanceLevel() {
        return 0.10;
    }

    public double getMaximumGoodAcceptanceLevel() {
        return 0.20;
    }

}