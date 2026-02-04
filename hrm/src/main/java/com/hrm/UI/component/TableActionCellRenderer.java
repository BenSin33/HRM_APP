package com.hrm.UI.component;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import com.formdev.flatlaf.extras.*;;

public class TableActionCellRenderer extends JPanel implements TableCellRenderer{

    public TableActionCellRenderer() {
        this.setLayout(new FlowLayout(FlowLayout.CENTER,15,2));
        this.setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        
                removeAll();
                JButton btnEdit = new JButton(new FlatSVGIcon("icons/edit_button.svg", 32, 32));
                JButton btnDelete = new JButton(new FlatSVGIcon("icons/delete_button.svg", 32, 32));

                btnEdit.putClientProperty("JButton.buttonType", "toolBarButton");
                btnDelete.putClientProperty("JButton.buttonType", "toolBarButton");

                if(isSelected){
                    setBackground(table.getSelectionBackground());
                } else {
                    setBackground(table.getBackground());
                }

                this.add(btnEdit);
                this.add(btnDelete);

                return this;

    }
    
}
