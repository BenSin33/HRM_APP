package com.hrm.UI.HR.CategoryTab;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.formdev.flatlaf.extras.FlatSVGIcon;

public class CategoryActionRenderer extends JPanel implements TableCellRenderer {
    private static final int ICON_SIZE = 22;

    private final JButton editBtn;
    private final JButton deleteBtn;
    private boolean isHovered = false;

    public CategoryActionRenderer() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 8, 4));
        setOpaque(true);

        editBtn = createButton(new FlatSVGIcon("icons/edit_button.svg", ICON_SIZE, ICON_SIZE), "Sửa");
        deleteBtn = createButton(new FlatSVGIcon("icons/delete_button.svg", ICON_SIZE, ICON_SIZE), "Xóa");

        add(editBtn);
        add(deleteBtn);
    }

    private JButton createButton(Icon icon, String tooltip) {
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
