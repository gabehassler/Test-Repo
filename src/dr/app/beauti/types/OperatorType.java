
package dr.app.beauti.types;

import dr.evomodel.operators.BitFlipInSubstitutionModelOperator;
import dr.evomodelxml.operators.TreeNodeSlideParser;
import dr.inference.operators.RateBitExchangeOperator;
import dr.inferencexml.operators.ScaleOperatorParser;

public enum OperatorType {

    SCALE("scale"),
    RANDOM_WALK("randomWalk"),
    RANDOM_WALK_ABSORBING("randomWalkAbsorbing"),
    RANDOM_WALK_REFLECTING("randomWalkReflecting"),
    RANDOM_WALK_INT("randomWalkIntegerOperator"),
    INTEGER_RANDOM_WALK("integerRandomWalk"),
    UP_DOWN("upDown"),
    UP_DOWN_ALL_RATES_HEIGHTS("upDownAllRatesHeights"),
    MICROSAT_UP_DOWN("microsatUpDown"),
    SCALE_ALL(ScaleOperatorParser.SCALE_ALL),
    SCALE_INDEPENDENTLY("scaleIndependently"),
    CENTERED_SCALE("centeredScale"),
    DELTA_EXCHANGE("deltaExchange"),
    INTEGER_DELTA_EXCHANGE("integerDeltaExchange"),
    SWAP("swap"),
    BITFLIP("bitFlip"),
    BITFIP_IN_SUBST(BitFlipInSubstitutionModelOperator.BIT_FLIP_OPERATOR),// bitFlipInSubstitutionModelOperator
    RATE_BIT_EXCHANGE(RateBitExchangeOperator.OPERATOR_NAME), // rateBitExchangeOperator
    TREE_BIT_MOVE("treeBitMove"),
    SAMPLE_NONACTIVE("sampleNoneActiveOperator"),
    SCALE_WITH_INDICATORS("scaleWithIndicators"),
    UNIFORM("uniform"),
    INTEGER_UNIFORM("integerUniform"),
    SUBTREE_SLIDE("subtreeSlide"),
    NARROW_EXCHANGE("narrowExchange"),
    WIDE_EXCHANGE("wideExchange"),
    GMRF_GIBBS_OPERATOR("gmrfGibbsOperator"),
    SKY_GRID_GIBBS_OPERATOR("gmrfGibbsOperator"),
//    PRECISION_GMRF_OPERATOR("precisionGMRFOperator"),
    WILSON_BALDING("wilsonBalding"),
    NODE_REHIGHT(TreeNodeSlideParser.TREE_NODE_REHEIGHT); // nodeReHeight

    OperatorType(String displayName) {
        this.displayName = displayName;
    }

    public String toString() {
        return displayName;
    }

    private final String displayName;
}
