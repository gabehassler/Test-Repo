package dr.app.pathogen;
import jam.framework.MenuFactory;
import jam.framework.AbstractFrame;
import jam.framework.Application;
import jam.framework.MenuBarFactory;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
public class PathogenMacFileMenuFactory implements MenuFactory {
    public PathogenMacFileMenuFactory() {
    }
    public String getMenuName() {
        return "File";
    }
    public void populateMenu(JMenu menu, AbstractFrame frame) {
        Application application = Application.getApplication();
        JMenuItem item;
        item = new JMenuItem(application.getNewAction());
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, MenuBarFactory.MENU_MASK));
        menu.add(item);
        item = new JMenuItem(application.getOpenAction());
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, MenuBarFactory.MENU_MASK));
        menu.add(item);
        if (frame != null) {
            item = new JMenuItem(frame.getCloseWindowAction());
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, MenuBarFactory.MENU_MASK));
            menu.add(item);
            item = new JMenuItem(frame.getSaveAction());
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, MenuBarFactory.MENU_MASK));
            menu.add(item);
            item = new JMenuItem(frame.getSaveAsAction());
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, MenuBarFactory.MENU_MASK + ActionEvent.SHIFT_MASK));
            menu.add(item);
        } else {
            // No frame available so create a disabled menu for the default menu bar
            item = new JMenuItem("Close");
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, MenuBarFactory.MENU_MASK));
            item.setEnabled(false);
            menu.add(item);
            item = new JMenuItem("Save");
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, MenuBarFactory.MENU_MASK));
            item.setEnabled(false);
            menu.add(item);
            item = new JMenuItem("Save As...");
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, MenuBarFactory.MENU_MASK + ActionEvent.SHIFT_MASK));
            item.setEnabled(false);
            menu.add(item);
        }
        menu.addSeparator();
        if (frame instanceof PathogenFrame) {
            item = new JMenuItem(((PathogenFrame)frame).getExportTreeAction());
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, MenuBarFactory.MENU_MASK));
            menu.add(item);
//            item = new JMenuItem(((TemporalSamplerFrame)frame).getExportGraphicAction());
//            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, MenuBarFactory.MENU_MASK + KeyEvent.ALT_MASK));
//            menu.add(item);
            item = new JMenuItem(((PathogenFrame)frame).getExportDataAction());
            menu.add(item);
            item = new JMenuItem(((PathogenFrame)frame).getExportTimeTreeAction());
            menu.add(item);
        } else {
            // If the frame is not a BeautiFrame then create a dummy set of disabled menu options.
            // At present the only situation where this may happen is in Mac OS X when no windows
            // are open and the menubar is created by the hidden frame.
            item = new JMenuItem("Export Tree...");
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, MenuBarFactory.MENU_MASK));
            item.setEnabled(false);
            menu.add(item);
//            item = new JMenuItem("Export Graphic...");
//            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, MenuBarFactory.MENU_MASK + KeyEvent.ALT_MASK));
//            item.setEnabled(false);
//            menu.add(item);
            item = new JMenuItem("Export Data...");
            item.setEnabled(false);
            menu.add(item);
        }
        menu.addSeparator();
        if (frame != null) {
            item = new JMenuItem(frame.getPrintAction());
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, MenuBarFactory.MENU_MASK));
            menu.add(item);
            item = new JMenuItem(application.getPageSetupAction());
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, MenuBarFactory.MENU_MASK + ActionEvent.SHIFT_MASK));
            menu.add(item);
        } else {
            // No frame available so create a disabled menu for the default menu bar
            item = new JMenuItem("Print...");
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, MenuBarFactory.MENU_MASK));
            item.setEnabled(false);
            menu.add(item);
            item = new JMenuItem("Page Setup...");
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, MenuBarFactory.MENU_MASK + ActionEvent.SHIFT_MASK));
            item.setEnabled(false);
            menu.add(item);
        }
    }
    public int getPreferredAlignment() {
        return LEFT;
    }
}