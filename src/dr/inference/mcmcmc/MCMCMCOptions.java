
package dr.inference.mcmcmc;



public class MCMCMCOptions {

    public MCMCMCOptions(final double[] temperatures, final int swapChainsEvery) {
        this.temperatures = temperatures;
        this.swapChainsEvery = swapChainsEvery;
    }


    public double[] getChainTemperatures() {
        return temperatures;
    }

    public int getSwapChainsEvery() {
        return swapChainsEvery;
    }

    private final double[] temperatures;
    private final int swapChainsEvery;
}
