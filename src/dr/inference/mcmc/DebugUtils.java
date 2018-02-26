package dr.inference.mcmc;
import dr.evolution.io.Importer;
import dr.evolution.io.NewickImporter;
import dr.evolution.tree.Tree;
import dr.evomodel.tree.TreeModel;
import dr.inference.model.Likelihood;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import java.io.*;
public class DebugUtils {
    public static boolean writeStateToFile(File file, long state, double lnL) {
        OutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(file);
            PrintStream out = new PrintStream(fileOut);
            out.print("state\t");
            out.println(state);
            out.print("lnL\t");
            out.println(lnL);
            for (Parameter parameter : Parameter.FULL_PARAMETER_SET) {
                out.print(parameter.getParameterName());
                out.print("\t");
                out.print(parameter.getDimension());
                for (int dim = 0; dim < parameter.getDimension(); dim++) {
                    out.print("\t");
                    out.print(parameter.getParameterValue(dim));
                }
                out.println();
            }
            for (Model model : Model.FULL_MODEL_SET) {
                if (model instanceof TreeModel) {
                    out.print(model.getModelName());
                    out.print("\t");
                    out.println(((TreeModel) model).getNewick());
                }
            }
            out.close();
            fileOut.close();
        } catch (IOException ioe) {
            System.err.println("Unable to write file: " + ioe.getMessage());
            return false;
        }
        return true;
    }
    public static long readStateFromFile(File file, double[] lnL) {
        long state = -1;
        try {
            FileReader fileIn = new FileReader(file);
            BufferedReader in = new BufferedReader(fileIn);
            String line = in.readLine();
            String[] fields = line.split("\t");
            try {
                if (!fields[0].equals("state")) {
                    throw new RuntimeException("Unable to read state number from state file");
                }
                state = Long.parseLong(fields[1]);
            } catch (NumberFormatException nfe) {
                throw new RuntimeException("Unable to read state number from state file");
            }
            line = in.readLine();
            fields = line.split("\t");
            try {
                if (!fields[0].equals("lnL")) {
                    throw new RuntimeException("Unable to read lnL from state file");
                }
                if (lnL != null) {
                    lnL[0] = Double.parseDouble(fields[1]);
                }
            } catch (NumberFormatException nfe) {
                throw new RuntimeException("Unable to read lnL from state file");
            }
            for (Parameter parameter : Parameter.FULL_PARAMETER_SET) {
                line = in.readLine();
                fields = line.split("\t");
//                if (!fields[0].equals(parameter.getParameterName())) {
//                    System.err.println("Unable to match state parameter: " + fields[0] + ", expecting " + parameter.getParameterName());
//                }
                int dimension = Integer.parseInt(fields[1]);
                if (dimension != parameter.getDimension()) {
                    System.err.println("Unable to match state parameter dimension: " + dimension + ", expecting " + parameter.getDimension());
                }
                for (int dim = 0; dim < parameter.getDimension(); dim++) {
                    parameter.setParameterValueQuietly(dim, Double.parseDouble(fields[dim + 2]));
                }
            }
            // load the tree models last as we get the node heights from the tree (not the parameters which
            // which may not be associated with the right node
            for (Model model : Model.FULL_MODEL_SET) {
                if (model instanceof TreeModel) {
                    line = in.readLine();
                    fields = line.split("\t");
                    if (!fields[0].equals(model.getModelName())) {
                        throw new RuntimeException("Unable to match state parameter: " + fields[0] + ", expecting " + model.getModelName());
                    }
                    NewickImporter importer = new NewickImporter(fields[1]);
                    Tree tree = importer.importNextTree();
                    ((TreeModel) model).beginTreeEdit();
                    ((TreeModel) model).adoptTreeStructure(tree);
                    ((TreeModel) model).endTreeEdit();
                }
            }
            in.close();
            fileIn.close();
            for (Likelihood likelihood : Likelihood.FULL_LIKELIHOOD_SET) {
                likelihood.makeDirty();
            }
        } catch (IOException ioe) {
            throw new RuntimeException("Unable to read file: " + ioe.getMessage());
        } catch (Importer.ImportException ie) {
            throw new RuntimeException("Unable to import tree: " + ie.getMessage());
        }
        return state;
    }
}
