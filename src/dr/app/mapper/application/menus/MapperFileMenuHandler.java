package dr.app.mapper.application.menus;

import javax.swing.*;

public interface MapperFileMenuHandler {

    Action getImportMeasurementsAction();

    Action getImportLocationsAction();

    Action getImportTreesAction();

    Action getExportDataAction();

	Action getExportPDFAction();

}