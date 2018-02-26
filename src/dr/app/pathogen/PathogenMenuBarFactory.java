
package dr.app.pathogen;

import jam.mac.MacEditMenuFactory;
import jam.mac.MacHelpMenuFactory;
import jam.mac.MacWindowMenuFactory;
import jam.framework.DefaultMenuBarFactory;
import jam.framework.DefaultEditMenuFactory;
import jam.framework.DefaultHelpMenuFactory;

import dr.app.util.OSType;


public class PathogenMenuBarFactory extends DefaultMenuBarFactory {

	public PathogenMenuBarFactory() {
		if (OSType.isMac()) {
			registerMenuFactory(new PathogenMacFileMenuFactory());
			registerMenuFactory(new MacEditMenuFactory());
			registerMenuFactory(new MacWindowMenuFactory());
			registerMenuFactory(new MacHelpMenuFactory());
		} else {
			registerMenuFactory(new PathogenDefaultFileMenuFactory());
			registerMenuFactory(new DefaultEditMenuFactory());
			registerMenuFactory(new DefaultHelpMenuFactory());
		}
	}

}