
package dr.app.beauti;

import jam.framework.Exportable;

import javax.swing.*;

import dr.app.beauti.options.BeautiOptions;

public abstract class BeautiPanel extends JPanel implements Exportable {

    public abstract void setOptions(BeautiOptions options);

    public abstract void getOptions(BeautiOptions options);
}
