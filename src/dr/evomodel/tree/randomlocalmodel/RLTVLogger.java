package dr.evomodel.tree.randomlocalmodel;

import dr.evolution.tree.NodeRef;
import dr.evomodel.tree.TreeModel;
import dr.inference.loggers.LogFormatter;
import dr.inference.loggers.MCLogger;

public class RLTVLogger extends MCLogger {

    private TreeModel treeModel;
    private RandomLocalTreeVariable randomLocal;

    public RLTVLogger(LogFormatter formatter,
                      int logEvery, TreeModel treeModel,
                      RandomLocalTreeVariable randomLocal) {

        super(formatter, logEvery, false);
        this.treeModel = treeModel;
        this.randomLocal = randomLocal;
    }

    public void startLogging() {
        logLine("State\tRate changes");
    }

    public void log(long state) {

        if (logEvery <= 0 || ((state % logEvery) == 0)) {

            int nodeCount = treeModel.getNodeCount();

            StringBuilder builder = new StringBuilder();
            builder.append(state);
            for (int i = 0; i < nodeCount; i++) {

                NodeRef node = treeModel.getNode(i);

                if (randomLocal.isVariableSelected(treeModel, node)) {
                    builder.append("\t");
                    builder.append(node.getNumber());
                    builder.append("\t");
                    builder.append(randomLocal.getVariable(treeModel, node));
                }
            }

            logLine(builder.toString());
        }
    }

    public void stopLogging() {

        super.stopLogging();
    }

}
