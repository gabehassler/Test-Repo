package dr.app.beauti.alignmentviewer;

import java.awt.*;

public interface CellDecorator {
    Paint getCellForeground(int row, int column, int state);
    Paint getCellBackground(int row, int column, int state);
}
