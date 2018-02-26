
package dr.evomodel.treelikelihood;


public class GeneralLikelihoodCore extends AbstractLikelihoodCore {

	public GeneralLikelihoodCore(int stateCount) {
		super(stateCount);
	}

	protected void calculateStatesStatesPruning(int[] states1, double[] matrices1,
												int[] states2, double[] matrices2,
												double[] partials3)
	{
		int v = 0;

		for (int l = 0; l < matrixCount; l++) {

			for (int k = 0; k < patternCount; k++) {

				int state1 = states1[k];
				int state2 = states2[k];

				int w = l * matrixSize;

                if (state1 < stateCount && state2 < stateCount) {

					for (int i = 0; i < stateCount; i++) {

						partials3[v] = matrices1[w + state1] * matrices2[w + state2];

						v++;
						w += stateCount;
					}

				} else if (state1 < stateCount) {
					// child 2 has a gap or unknown state so treat it as unknown

					for (int i = 0; i < stateCount; i++) {

						partials3[v] = matrices1[w + state1];

						v++;
						w += stateCount;
					}
				} else if (state2 < stateCount) {
					// child 2 has a gap or unknown state so treat it as unknown

					for (int i = 0; i < stateCount; i++) {

						partials3[v] = matrices2[w + state2];

						v++;
						w += stateCount;
					}
				} else {
					// both children have a gap or unknown state so set partials to 1

					for (int j = 0; j < stateCount; j++) {
						partials3[v] = 1.0;
						v++;
					}
				}
			}
		}
	}

	protected void calculateStatesPartialsPruning(	int[] states1, double[] matrices1,
													double[] partials2, double[] matrices2,
													double[] partials3)
	{

		double sum, tmp;

		int u = 0;
		int v = 0;

		for (int l = 0; l < matrixCount; l++) {
			for (int k = 0; k < patternCount; k++) {

				int state1 = states1[k];

                int w = l * matrixSize;

				if (state1 < stateCount) {


					for (int i = 0; i < stateCount; i++) {

						tmp = matrices1[w + state1];

						sum = 0.0;
						for (int j = 0; j < stateCount; j++) {
							sum += matrices2[w] * partials2[v + j];
							w++;
						}

						partials3[u] = tmp * sum;
						u++;
					}

					v += stateCount;
				} else {
					// Child 1 has a gap or unknown state so don't use it

					for (int i = 0; i < stateCount; i++) {

						sum = 0.0;
						for (int j = 0; j < stateCount; j++) {
							sum += matrices2[w] * partials2[v + j];
							w++;
						}

						partials3[u] = sum;
						u++;
					}

					v += stateCount;
				}
			}
		}
	}

	protected void calculatePartialsPartialsPruning(double[] partials1, double[] matrices1,
													double[] partials2, double[] matrices2,
													double[] partials3)
	{
		double sum1, sum2;

		int u = 0;
		int v = 0;

		for (int l = 0; l < matrixCount; l++) {

			for (int k = 0; k < patternCount; k++) {

                int w = l * matrixSize;

				for (int i = 0; i < stateCount; i++) {

					sum1 = sum2 = 0.0;

					for (int j = 0; j < stateCount; j++) {
						sum1 += matrices1[w] * partials1[v + j];
						sum2 += matrices2[w] * partials2[v + j];

						w++;
					}

					partials3[u] = sum1 * sum2;
					u++;
				}
				v += stateCount;
			}
		}
	}

	protected void calculateStatesStatesPruning(int[] states1, double[] matrices1,
												int[] states2, double[] matrices2,
												double[] partials3, int[] matrixMap)
	{
		int v = 0;

		for (int k = 0; k < patternCount; k++) {

			int state1 = states1[k];
			int state2 = states2[k];

			int w = matrixMap[k] * matrixSize;

			if (state1 < stateCount && state2 < stateCount) {

				for (int i = 0; i < stateCount; i++) {

					partials3[v] = matrices1[w + state1] * matrices2[w + state2];

					v++;
					w += stateCount;
				}

			} else if (state1 < stateCount) {
				// child 2 has a gap or unknown state so treat it as unknown

				for (int i = 0; i < stateCount; i++) {

					partials3[v] = matrices1[w + state1];

					v++;
					w += stateCount;
				}
			} else if (state2 < stateCount) {
				// child 2 has a gap or unknown state so treat it as unknown

				for (int i = 0; i < stateCount; i++) {

					partials3[v] = matrices2[w + state2];

					v++;
					w += stateCount;
				}
			} else {
				// both children have a gap or unknown state so set partials to 1

				for (int j = 0; j < stateCount; j++) {
					partials3[v] = 1.0;
					v++;
				}
			}
		}
	}

	protected void calculateStatesPartialsPruning(	int[] states1, double[] matrices1,
													double[] partials2, double[] matrices2,
													double[] partials3, int[] matrixMap)
	{

		double sum, tmp;

		int u = 0;
		int v = 0;

		for (int k = 0; k < patternCount; k++) {

			int state1 = states1[k];

			int w = matrixMap[k] * matrixSize;

			if (state1 < stateCount) {

				for (int i = 0; i < stateCount; i++) {

					tmp = matrices1[w + state1];

					sum = 0.0;
					for (int j = 0; j < stateCount; j++) {
						sum += matrices2[w] * partials2[v + j];
						w++;
					}

					partials3[u] = tmp * sum;
					u++;
				}

				v += stateCount;
			} else {
				// Child 1 has a gap or unknown state so don't use it

				for (int i = 0; i < stateCount; i++) {

					sum = 0.0;
					for (int j = 0; j < stateCount; j++) {
						sum += matrices2[w] * partials2[v + j];
						w++;
					}

					partials3[u] = sum;
					u++;
				}

				v += stateCount;
			}
		}
	}

	protected void calculatePartialsPartialsPruning(double[] partials1, double[] matrices1,
													double[] partials2, double[] matrices2,
													double[] partials3, int[] matrixMap)
	{
		double sum1, sum2;

		int u = 0;
		int v = 0;

		for (int k = 0; k < patternCount; k++) {

			int w = matrixMap[k] * matrixSize;

			for (int i = 0; i < stateCount; i++) {

				sum1 = sum2 = 0.0;

				for (int j = 0; j < stateCount; j++) {
					sum1 += matrices1[w] * partials1[v + j];
					sum2 += matrices2[w] * partials2[v + j];

					w++;
				}

				partials3[u] = sum1 * sum2;
				u++;
			}
			v += stateCount;
		}
	}

	protected void calculateIntegratePartials(double[] inPartials, double[] proportions, double[] outPartials)
	{

		int u = 0;
		int v = 0;
		for (int k = 0; k < patternCount; k++) {

			for (int i = 0; i < stateCount; i++) {

				outPartials[u] = inPartials[v] * proportions[0];
				u++;
				v++;
			}
		}


		for (int l = 1; l < matrixCount; l++) {
			u = 0;

			for (int k = 0; k < patternCount; k++) {

				for (int i = 0; i < stateCount; i++) {

					outPartials[u] += inPartials[v] * proportions[l];
					u++;
					v++;
				}
			}
		}
	}

	public void calculateLogLikelihoods(double[] partials, double[] frequencies, double[] outLogLikelihoods)
	{
        int v = 0;
		for (int k = 0; k < patternCount; k++) {

            double sum = 0.0;
			for (int i = 0; i < stateCount; i++) {

				sum += frequencies[i] * partials[v];
				v++;
			}
            outLogLikelihoods[k] = Math.log(sum) + getLogScalingFactor(k);
		}
	}
}
