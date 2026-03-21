package com.hrm.UI.component;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import com.formdev.flatlaf.extras.FlatSVGIcon;

public class TableActionCellRenderer extends JPanel implements TableCellRenderer {
    private JButton btnEdit;
    private JButton btnDelete;
    private static final int ICON_SIZE = 28;
    private static final int BUTTON_SPACING = 12;
    private static final int PADDING = 5;
    
    // Lưu trạng thái hover
    private boolean isHovered = false;

    public TableActionCellRenderer() {
        setLayout(new FlowLayout(FlowLayout.CENTER, BUTTON_SPACING, PADDING));
        setOpaque(true);
        
        // Khởi tạo nút với icon
        btnEdit = createActionButton(new FlatSVGIcon("icons/edit_button.svg", ICON_SIZE, ICON_SIZE), "Sửa");
        btnDelete = createActionButton(new FlatSVGIcon("icons/delete_button.svg", ICON_SIZE, ICON_SIZE), "Xóa");
        
        add(btnEdit);
        add(btnDelete);
    }

    private JButton createActionButton(Icon icon, String tooltip) {
        JButton btn = new JButton(icon);
        btn.setToolTipText(tooltip);
        btn.setPreferredSize(new Dimension(ICON_SIZE + 8, ICON_SIZE + 8));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.putClientProperty("JButton.buttonType", "toolBarButton");
        return btn;
    }

    public void setHovered(boolean hovered) {
        this.isHovered = hovered;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
            boolean hasFocus, int row, int column) {
        
        // Đặt màu nền
        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(table.getBackground());
        }
        
        // Thay đổi màu nút tùy theo trạng thái hover
        if (isHovered) {
            btnEdit.setForeground(new Color(66, 133, 244).brighter()); // Xanh sáng hơn
            btnDelete.setForeground(new Color(229, 57, 53).brighter()); // Đỏ sáng hơn
            btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        } else {
            btnEdit.setForeground(new Color(66, 133, 244));
            btnDelete.setForeground(new Color(229, 57, 53));
            btnEdit.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            btnDelete.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
        
        return this;
    }
}
