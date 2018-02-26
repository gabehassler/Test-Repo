package dr.app.mapper.application.menus;

import jam.framework.MenuFactory;
import jam.framework.AbstractFrame;
import jam.framework.Application;
import jam.framework.MenuBarFactory;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class MapperDefaultFileMenuFactory implements MenuFactory {


    public MapperDefaultFileMenuFactory() {
    }

    public String getMenuName() {
        return "File";
    }

    public void populateMenu(JMenu menu, AbstractFrame frame) {

        JMenuItem item;

        Application application = Application.getApplication();
        menu.setMnemonic('F');

        item = new JMenuItem(application.getNewAction());
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, MenuBarFactory.MENU_MASK));
        menu.add(item);

        item = new JMenuItem(frame.getOpenAction());
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, MenuBarFactory.MENU_MASK));
        menu.add(item);

        item = new JMenuItem(frame.getSaveAction());
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, MenuBarFactory.MENU_MASK));
        menu.add(item);

        item = new JMenuItem(frame.getSaveAsAction());
        menu.add(item);

        menu.addSeparator();

        // On Windows and Linux platforms, each window has its own menu so items which are not needed
        // are simply missing. In contrast, on Mac, the menu is for the application so items should
        // be enabled/disabled as frames come to the front.
        if (frame instanceof MapperFileMenuHandler) {
            item = new JMenuItem(((MapperFileMenuHandler)frame).getImportMeasurementsAction());
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, MenuBarFactory.MENU_MASK));
            menu.add(item);

            item = new JMenuItem(((MapperFileMenuHandler)frame).getImportLocationsAction());
            menu.add(item);

            item = new JMenuItem(((MapperFileMenuHandler)frame).getImportTreesAction());
            menu.add(item);

            item = new JMenuItem(((MapperFileMenuHandler)frame).getExportDataAction());
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, MenuBarFactory.MENU_MASK));
            menu.add(item);

            item = new JMenuItem(((MapperFileMenuHandler)frame).getExportPDFAction());
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, MenuBarFactory.MENU_MASK + KeyEvent.ALT_MASK));
            menu.add(item);
        } else {
            // If the frame is not a TracerFileMenuHandler then leave out the import/export options.
        }

        menu.addSeparator();

        item = new JMenuItem(frame.getPrintAction());
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, MenuBarFactory.MENU_MASK));
        menu.add(item);

        item = new JMenuItem(application.getPageSetupAction());
        menu.add(item);

        menu.addSeparator();

        item = new JMenuItem(application.getExitAction());
        menu.add(item);

    }

    public int getPreferredAlignment() {
        return LEFT;
    }
}