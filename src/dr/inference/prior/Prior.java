
package dr.inference.prior;

import dr.inference.model.Model;

import java.io.Serializable;

public interface Prior extends Serializable {

    public static final class UniformPrior implements Prior {
        public double getLogPrior(Model m) {
            return 0.0;
        }

        public String getPriorName() {
            return "Uniform";
        }

        public String toString() {
            return "Uniform";
        }
    }

    public static final UniformPrior UNIFORM_PRIOR = new UniformPrior();


    public double getLogPrior(Model model);

    public String getPriorName();
}
