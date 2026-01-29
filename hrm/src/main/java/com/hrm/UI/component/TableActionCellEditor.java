package com.hrm.UI.component;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.function.Supplier;

public class TableActionCellEditor<T> extends AbstractCellEditor implements TableCellEditor {
    private JPanel panel;
    private JButton btnEdit;
    private T currentData;
    private String id;
    
    // Interface callback để Panel chính xử lý việc lưu vào DB
    public interface ActionHandler<T> {
        void onEdit(T data);
    }

    public TableActionCellEditor(JPanel formPanel, ActionHandler<T> handler, Supplier<T> dataSupplier) {
        panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
        btnEdit = new JButton(new ImageIcon(getClass().getResource("/icons/edit_green.png")));
        btnEdit.setBorderPainted(false);
        btnEdit.setContentAreaFilled(false);

        btnEdit.addActionListener(e -> {
            T data = dataSupplier.get(); // Lấy dữ liệu mới nhất từ DB dựa trên ID dòng
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(panel);
            CRUDDialog<T> dialog = new CRUDDialog<>(frame, "Chỉnh sửa", formPanel, data);
            dialog.setVisible(true);
            
            if (dialog.getResult() != null) {
                handler.onEdit(dialog.getResult());
            }
            fireEditingStopped();
        });
        panel.add(btnEdit);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        panel.setBackground(table.getSelectionBackground());
        return panel;
    }

    @Override
    public Object getCellEditorValue() { return null; }
}