
package dr.app.beagle.evomodel.operators;

import dr.app.beagle.evomodel.treelikelihood.AncestralStateBeagleTreeLikelihood;
import dr.app.beagle.evomodel.treelikelihood.BeagleTreeLikelihood;
import dr.inference.operators.GibbsOperator;
import dr.inference.operators.OperatorFailedException;
import dr.inference.operators.SimpleMCMCOperator;
import dr.math.MathUtils;
import jebl.math.Random;

public class PatternWeightIncrementOperator extends SimpleMCMCOperator implements GibbsOperator {

    public static final String PATTERN_WEIGHT_INCREMENT_OPERATOR = "patternWeightIncrementOperator";

    public PatternWeightIncrementOperator(BeagleTreeLikelihood treeLikelihood, double weight) {
        this.treeLikelihood = treeLikelihood;
        setWeight(weight);

        finalPatternWeights = treeLikelihood.getPatternWeights();

        double[] weights = new double[finalPatternWeights.length];
        treeLikelihood.setPatternWeights(finalPatternWeights);
//        treeLikelihood.setPatternWeights(weights);
    }

    public double doOperation() throws OperatorFailedException {

        if (allPatternsAdded) {
            return 0.0;
        }

        double[] weights = treeLikelihood.getPatternWeights();
        double[] w = new double[weights.length];

        double sum = 0.0;
        for (int i = 0; i < weights.length; i++) {
            w[i] = finalPatternWeights[i] - weights[i];
            sum += w[i];
        }

        // System.out.println("PatternWeightIncrementOperator - Sites remaining: " + sum);
        if (sum < 1.0) {
            allPatternsAdded = true;
            System.out.println("PatternWeightIncrementOperator - All sites added");
            return 0.0;
        }

        for (int i = 0; i < weights.length; i++) {
            w[i] /= sum;
            if (i > 0) {
                w[i] += w[i - 1];
            }
        }

        int r = Random.randomChoice(w);

        weights[r] ++;

        if (weights[r] > finalPatternWeights[r]) {
            throw new RuntimeException("Pattern weight exceeding final weight");
        }

        treeLikelihood.setPatternWeights(weights);

        treeLikelihood.makeDirty();

        return 0;
    }

    public void reject() {
        super.reject();
    }

    @Override
    public int getStepCount() {
        return 0;
    }

    public String getPerformanceSuggestion() {
        if (Utils.getAcceptanceProbability(this) < getMinimumAcceptanceLevel()) {
            return "";
        } else if (Utils.getAcceptanceProbability(this) > getMaximumAcceptanceLevel()) {
            return "";
        } else {
            return "";
        }
    }

    public String getOperatorName() {
        return PATTERN_WEIGHT_INCREMENT_OPERATOR;
    }

    private final BeagleTreeLikelihood treeLikelihood;
    private final double[] finalPatternWeights;

    private boolean allPatternsAdded = false;

}