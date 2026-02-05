package com.hrm.UI.HR.ContractTab;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import com.formdev.flatlaf.extras.FlatSVGIcon;

public class ContractTableRenderer extends JPanel implements TableCellRenderer {
    private JButton editBtn;
    private JButton deleteBtn;
    private static final int ICON_SIZE = 24;
    private static final int BUTTON_SPACING = 8;
    private static final int PADDING = 5;
    
    private boolean isHovered = false;

    public ContractTableRenderer() {
        setLayout(new FlowLayout(FlowLayout.CENTER, BUTTON_SPACING, PADDING));
        setOpaque(true);
        
        editBtn = createActionButton(new FlatSVGIcon("icons/edit_button.svg", ICON_SIZE, ICON_SIZE), "Sửa");
        deleteBtn = createActionButton(new FlatSVGIcon("icons/delete_button.svg", ICON_SIZE, ICON_SIZE), "Xóa");
        
        add(editBtn);
        add(deleteBtn);
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
        
        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(table.getBackground());
        }
        
        if (isHovered) {
            editBtn.setForeground(new Color(66, 133, 244).brighter());
            deleteBtn.setForeground(new Color(229, 57, 53).brighter());
        } else {
            editBtn.setForeground(new Color(66, 133, 244));
            deleteBtn.setForeground(new Color(229, 57, 53));
        }
        
        return this;
    }
}
