

package dr.app.coalgen;

import jam.framework.Application;
import jam.framework.DocumentFrame;
import jam.framework.SingleDocMenuBarFactory;

import javax.swing.*;

public class CoalGenMenuFactory extends SingleDocMenuBarFactory {

    public void populateMenuBar(JMenuBar menuBar, DocumentFrame documentFrame,
                                Application application) {

        super.populateMenuBar(menuBar, documentFrame);

        documentFrame.getSaveAction().setEnabled(false);
        documentFrame.getSaveAsAction().setEnabled(false);

        documentFrame.getCutAction().setEnabled(false);
        documentFrame.getCopyAction().setEnabled(true);
        documentFrame.getPasteAction().setEnabled(false);
        documentFrame.getDeleteAction().setEnabled(false);
        documentFrame.getSelectAllAction().setEnabled(false);
        documentFrame.getFindAction().setEnabled(false);

        documentFrame.getZoomWindowAction().setEnabled(false);
    }
}