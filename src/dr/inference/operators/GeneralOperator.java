
package dr.inference.operators;

import dr.inference.model.Likelihood;
import dr.inference.prior.Prior;

public interface GeneralOperator {

    double operate(Prior prior, Likelihood likelihood) throws OperatorFailedException;
}
