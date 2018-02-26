package dr.app.gui.table;
import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
public class TableEditorStopper extends FocusAdapter implements PropertyChangeListener
{
    public static void ensureEditingStopWhenTableLosesFocus(JTable table)
    {
        new TableEditorStopper(table);
    }
    private TableEditorStopper(JTable table)
    {
        this.table=table;
        table.addPropertyChangeListener("tableCellEditor", this);
    }
    public void propertyChange(PropertyChangeEvent evt)
    {
        if (focused!=null)
        {
            focused.removeFocusListener(this);
        }
        focused = table.getEditorComponent();
        if (focused!=null)
        {
            focused.addFocusListener(this);
        }
    }
    public void focusLost(FocusEvent e)
    {
        if (focused!=null)
        {
            focused.removeFocusListener(this);
            focused = e.getOppositeComponent();
            if (table==focused || table.isAncestorOf(focused))
            {
                focused.addFocusListener(this);
            }
            else
            {
                focused=null;
                TableCellEditor editor = table.getCellEditor();
                if (editor!=null)
                {
                    editor.stopCellEditing();
                }
            }
        }
    }
    private Component focused;
    private JTable table;
}
