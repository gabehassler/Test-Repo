package dr.app.gui.table;
import dr.app.gui.components.RealNumberField;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class DateCellEditor extends DefaultCellEditor {
    private static final long serialVersionUID = 5067833373685886590L;
    private RealNumberField editor;
    public DateCellEditor() {
        this(false);
    }
    public DateCellEditor(boolean allowEmpty) {
        super(new RealNumberField(0.0, Double.MAX_VALUE));
        editor = (RealNumberField) getComponent();
        editor.setAllowEmpty(allowEmpty);
        setClickCountToStart(2); //This is usually 1 or 2.
        // Must do this so that editing stops when appropriate.
        editor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }
    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }
    public Object getCellEditorValue() {
        return editor.getValue();
    }
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {
        FontMetrics metrics = table.getFontMetrics(table.getFont());
        int fontHeight = metrics.getHeight();
        table.setRowHeight(row, fontHeight + fontHeight / 2);
//      System.out.println(editor.getPreferredSize() + "\t" + table.getRowHeight(row) + "\t" + table.getHeight());
        editor.setFont(table.getFont());
        if (value != null) {
            editor.setValue(((Double) value).doubleValue());
        }
        return editor;
    }
}