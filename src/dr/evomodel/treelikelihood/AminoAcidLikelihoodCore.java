package dr.evomodel.treelikelihood;
public class AminoAcidLikelihoodCore extends AbstractLikelihoodCore {
	public AminoAcidLikelihoodCore() {
		super(20);
	}
	protected void calculateStatesStatesPruning(int[] states1, double[] matrices1,
												int[] states2, double[] matrices2,
												double[] partials3)
	{
		int v = 0;
		int u = 0;
		for (int j = 0; j < matrixCount; j++) {
			for (int k = 0; k < patternCount; k++) {
				int w = u;
				int state1 = states1[k];
				int state2 = states2[k];
				if (state1 < 20 && state2 < 20) {
					partials3[v] = matrices1[w + state1] * matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1] * matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1] * matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1] * matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1] * matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1] * matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1] * matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1] * matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1] * matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1] * matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1] * matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1] * matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1] * matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1] * matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1] * matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1] * matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1] * matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1] * matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1] * matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1] * matrices2[w + state2];
					v++;	w += 20;
				} else if (state1 < 20) {
					// child 2 has a gap or unknown state so don't use it
					partials3[v] = matrices1[w + state1];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1];
					v++;	w += 20;
					partials3[v] = matrices1[w + state1];
					v++;	w += 20;
				} else if (state2 < 20) {
					// child 2 has a gap or unknown state so don't use it
					partials3[v] = matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices2[w + state2];
					v++;	w += 20;
					partials3[v] = matrices2[w + state2];
					v++;	w += 20;
				} else {
					// both children have a gap or unknown state so set partials to 1
					partials3[v] = 1.0;
					v++;
					partials3[v] = 1.0;
					v++;
					partials3[v] = 1.0;
					v++;
					partials3[v] = 1.0;
					v++;
					partials3[v] = 1.0;
					v++;
					partials3[v] = 1.0;
					v++;
					partials3[v] = 1.0;
					v++;
					partials3[v] = 1.0;
					v++;
					partials3[v] = 1.0;
					v++;
					partials3[v] = 1.0;
					v++;
					partials3[v] = 1.0;
					v++;
					partials3[v] = 1.0;
					v++;
					partials3[v] = 1.0;
					v++;
					partials3[v] = 1.0;
					v++;
					partials3[v] = 1.0;
					v++;
					partials3[v] = 1.0;
					v++;
					partials3[v] = 1.0;
					v++;
					partials3[v] = 1.0;
					v++;
					partials3[v] = 1.0;
					v++;
					partials3[v] = 1.0;
					v++;
				}
			}
			u += matrixSize;
		}
	}
	protected void calculateStatesPartialsPruning(	int[] states1, double[] matrices1,
													double[] partials2, double[] matrices2,
													double[] partials3)
	{
		int u = 0;
		int v = 0;
		int w = 0;
		int x, y;
		for (int l = 0; l < matrixCount; l++) {
			for (int k = 0; k < patternCount; k++) {
				int state1 = states1[k];
				if (state1 < 20) {
					double sum;
					x = w;
					for (int i = 0; i < 20; i++) {
						y = v;
						double value = matrices1[x + state1];
						sum =	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						partials3[u] = value * sum;
						u++;
					}
					v += 20;
				} else {
					// Child 1 has a gap or unknown state so don't use it
					double sum;
					x = w;
					for (int i = 0; i < 20; i++) {
						y = v;
						sum =	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						sum +=	matrices2[x] * partials2[y]; x++; y++;
						partials3[u] = sum;
						u++;
					}
					v += 20;
				}
			}
			w += matrixSize;
		}
	}
	protected void calculatePartialsPartialsPruning(double[] partials1, double[] matrices1,
													double[] partials2, double[] matrices2,
													double[] partials3)
	{
		double sum1, sum2;
		int u = 0;
		int v = 0;
		int w = 0;
		int x, y;
		for (int l = 0; l < matrixCount; l++) {
			for (int k = 0; k < patternCount; k++) {
				x = w;
				for (int i = 0; i < 20; i++) {
					y = v;
					sum1 =	matrices1[x] * partials1[y];
					sum2 =	matrices2[x] * partials2[y]; x++; y++;
					sum1 +=	matrices1[x] * partials1[y];
					sum2 +=	matrices2[x] * partials2[y]; x++; y++;
					sum1 +=	matrices1[x] * partials1[y];
					sum2 +=	matrices2[x] * partials2[y]; x++; y++;
					sum1 +=	matrices1[x] * partials1[y];
					sum2 +=	matrices2[x] * partials2[y]; x++; y++;
					sum1 +=	matrices1[x] * partials1[y];
					sum2 +=	matrices2[x] * partials2[y]; x++; y++;
					sum1 +=	matrices1[x] * partials1[y];
					sum2 +=	matrices2[x] * partials2[y]; x++; y++;
					sum1 +=	matrices1[x] * partials1[y];
					sum2 +=	matrices2[x] * partials2[y]; x++; y++;
					sum1 +=	matrices1[x] * partials1[y];
					sum2 +=	matrices2[x] * partials2[y]; x++; y++;
					sum1 +=	matrices1[x] * partials1[y];
					sum2 +=	matrices2[x] * partials2[y]; x++; y++;
					sum1 +=	matrices1[x] * partials1[y];
					sum2 +=	matrices2[x] * partials2[y]; x++; y++;
					sum1 +=	matrices1[x] * partials1[y];
					sum2 +=	matrices2[x] * partials2[y]; x++; y++;
					sum1 +=	matrices1[x] * partials1[y];
					sum2 +=	matrices2[x] * partials2[y]; x++; y++;
					sum1 +=	matrices1[x] * partials1[y];
					sum2 +=	matrices2[x] * partials2[y]; x++; y++;
					sum1 +=	matrices1[x] * partials1[y];
					sum2 +=	matrices2[x] * partials2[y]; x++; y++;
					sum1 +=	matrices1[x] * partials1[y];
					sum2 +=	matrices2[x] * partials2[y]; x++; y++;
					sum1 +=	matrices1[x] * partials1[y];
					sum2 +=	matrices2[x] * partials2[y]; x++; y++;
					sum1 +=	matrices1[x] * partials1[y];
					sum2 +=	matrices2[x] * partials2[y]; x++; y++;
					sum1 +=	matrices1[x] * partials1[y];
					sum2 +=	matrices2[x] * partials2[y]; x++; y++;
					sum1 +=	matrices1[x] * partials1[y];
					sum2 +=	matrices2[x] * partials2[y]; x++; y++;
					sum1 +=	matrices1[x] * partials1[y];
					sum2 +=	matrices2[x] * partials2[y]; x++; y++;
					partials3[u] = sum1 * sum2;
					u++;
				}
				v += 20;
			}
			w += matrixSize;
		}
	}
	protected void calculateStatesStatesPruning(int[] states1, double[] matrices1,
												int[] states2, double[] matrices2,
												double[] partials3, int[] matrixMap)
	{
		throw new RuntimeException("calculateStatesStatesPruning not implemented using matrixMap");
	}
	protected void calculateStatesPartialsPruning(	int[] states1, double[] matrices1,
													double[] partials2, double[] matrices2,
													double[] partials3, int[] matrixMap)
	{
		throw new RuntimeException("calculateStatesStatesPruning not implemented using matrixMap");
	}
	protected void calculatePartialsPartialsPruning(double[] partials1, double[] matrices1,
													double[] partials2, double[] matrices2,
													double[] partials3, int[] matrixMap)
	{
		throw new RuntimeException("calculateStatesStatesPruning not implemented using matrixMap");
	}
    public void calculateIntegratePartials(double[] inPartials, double[] proportions, double[] outPartials)
	{
		int u = 0;
		int v = 0;
		for (int k = 0; k < patternCount; k++) {
			outPartials[u] = inPartials[v] * proportions[0]; u++; v++;
			outPartials[u] = inPartials[v] * proportions[0]; u++; v++;
			outPartials[u] = inPartials[v] * proportions[0]; u++; v++;
			outPartials[u] = inPartials[v] * proportions[0]; u++; v++;
			outPartials[u] = inPartials[v] * proportions[0]; u++; v++;
			outPartials[u] = inPartials[v] * proportions[0]; u++; v++;
			outPartials[u] = inPartials[v] * proportions[0]; u++; v++;
			outPartials[u] = inPartials[v] * proportions[0]; u++; v++;
			outPartials[u] = inPartials[v] * proportions[0]; u++; v++;
			outPartials[u] = inPartials[v] * proportions[0]; u++; v++;
			outPartials[u] = inPartials[v] * proportions[0]; u++; v++;
			outPartials[u] = inPartials[v] * proportions[0]; u++; v++;
			outPartials[u] = inPartials[v] * proportions[0]; u++; v++;
			outPartials[u] = inPartials[v] * proportions[0]; u++; v++;
			outPartials[u] = inPartials[v] * proportions[0]; u++; v++;
			outPartials[u] = inPartials[v] * proportions[0]; u++; v++;
			outPartials[u] = inPartials[v] * proportions[0]; u++; v++;
			outPartials[u] = inPartials[v] * proportions[0]; u++; v++;
			outPartials[u] = inPartials[v] * proportions[0]; u++; v++;
			outPartials[u] = inPartials[v] * proportions[0]; u++; v++;
		}
		for (int j = 1; j < matrixCount; j++) {
			u = 0;
			for (int k = 0; k < patternCount; k++) {
				outPartials[u] += inPartials[v] * proportions[j]; u++; v++;
				outPartials[u] += inPartials[v] * proportions[j]; u++; v++;
				outPartials[u] += inPartials[v] * proportions[j]; u++; v++;
				outPartials[u] += inPartials[v] * proportions[j]; u++; v++;
				outPartials[u] += inPartials[v] * proportions[j]; u++; v++;
				outPartials[u] += inPartials[v] * proportions[j]; u++; v++;
				outPartials[u] += inPartials[v] * proportions[j]; u++; v++;
				outPartials[u] += inPartials[v] * proportions[j]; u++; v++;
				outPartials[u] += inPartials[v] * proportions[j]; u++; v++;
				outPartials[u] += inPartials[v] * proportions[j]; u++; v++;
				outPartials[u] += inPartials[v] * proportions[j]; u++; v++;
				outPartials[u] += inPartials[v] * proportions[j]; u++; v++;
				outPartials[u] += inPartials[v] * proportions[j]; u++; v++;
				outPartials[u] += inPartials[v] * proportions[j]; u++; v++;
				outPartials[u] += inPartials[v] * proportions[j]; u++; v++;
				outPartials[u] += inPartials[v] * proportions[j]; u++; v++;
				outPartials[u] += inPartials[v] * proportions[j]; u++; v++;
				outPartials[u] += inPartials[v] * proportions[j]; u++; v++;
				outPartials[u] += inPartials[v] * proportions[j]; u++; v++;
				outPartials[u] += inPartials[v] * proportions[j]; u++; v++;
			}
		}
	}
	public void calculateLogLikelihoods(double[] partials, double[] frequencies, double[] outLogLikelihoods)
	{
        int v = 0;
		for (int k = 0; k < patternCount; k++) {
			double sum = frequencies[0] * partials[v];	v++;
			sum += frequencies[1] * partials[v];	v++;
			sum += frequencies[2] * partials[v];	v++;
			sum += frequencies[3] * partials[v];	v++;
			sum += frequencies[4] * partials[v];	v++;
			sum += frequencies[5] * partials[v];	v++;
			sum += frequencies[6] * partials[v];	v++;
			sum += frequencies[7] * partials[v];	v++;
			sum += frequencies[8] * partials[v];	v++;
			sum += frequencies[9] * partials[v];	v++;
			sum += frequencies[10] * partials[v];	v++;
			sum += frequencies[11] * partials[v];	v++;
			sum += frequencies[12] * partials[v];	v++;
			sum += frequencies[13] * partials[v];	v++;
			sum += frequencies[14] * partials[v];	v++;
			sum += frequencies[15] * partials[v];	v++;
			sum += frequencies[16] * partials[v];	v++;
			sum += frequencies[17] * partials[v];	v++;
			sum += frequencies[18] * partials[v];	v++;
			sum += frequencies[19] * partials[v];	v++;
            outLogLikelihoods[k] = Math.log(sum)  + getLogScalingFactor(k);
		}
	}
}
