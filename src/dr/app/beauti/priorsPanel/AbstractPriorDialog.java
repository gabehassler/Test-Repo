package dr.app.beauti.priorsPanel;
import dr.app.beauti.options.Parameter;
public interface AbstractPriorDialog {
    int showDialog();
    boolean hasInvalidInput(boolean b);
    void getArguments(Parameter parameter);
}
