package com.hrm.UI.component;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.function.Supplier;

public class TableActionCellEditor<T> extends AbstractCellEditor implements TableCellEditor {
    private JPanel panel;
    private JButton btnEdit;
    private JButton btnDelete;
    
    public interface ActionHandler<T> {
        void onEdit(T data);
        void onDelete(T data); 
    }

    public TableActionCellEditor(JPanel formPanel, ActionHandler<T> handler, Supplier<T> dataSupplier) {
        panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 2));
        panel.setOpaque(true);
        
        // --- NÚT SỬA ---
        // Sử dụng EditButton để đồng bộ giao diện FlatLaf (nếu bạn đã có file EditButton.java)
        btnEdit = new EditButton<>((JFrame)null, formPanel, dataSupplier, handler::onEdit);

        // --- NÚT XÓA ---
        // Sửa lỗi: Lấy ID thực tế từ dataSupplier để truyền vào DeleteButton
        btnDelete = new DeleteButton(panel, "bản ghi", () -> {
            T data = dataSupplier.get();
            handler.onDelete(data);
            fireEditingStopped(); // Quan trọng: Dừng chế độ edit để cập nhật lại JTable
        });
        
        // Ghi đè lại ActionListener của btnDelete để lấy ID động mỗi khi bấm
        btnDelete.addActionListener(e -> {
             // Cập nhật lại logic nếu bạn muốn hiển thị ID cụ thể trong Dialog xác nhận
             // Ví dụ: DeleteAction.execute(panel, dataSupplier.get().toString(), ...)
        });

        panel.add(btnEdit);
        panel.add(btnDelete);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        // Thiết lập màu nền khớp với màu chọn của bảng
        panel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        return panel;
    }

    @Override
    public Object getCellEditorValue() { return null; }
}