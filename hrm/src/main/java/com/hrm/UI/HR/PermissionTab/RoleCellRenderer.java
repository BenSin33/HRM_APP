package com.hrm.UI.HR.PermissionTab;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

public class RoleCellRenderer extends DefaultListCellRenderer {

    private final Color activeColor = new Color(126,34,206);
    private final Color activeBg = new Color(243,232,255);

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasfocus){
        
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasfocus);
        
        label.setBorder(BorderFactory.createEmptyBorder(10,10,10,10)); //tạo khoảng cách chữ
        if (isSelected){
            label.setBackground(activeBg);
            label.setForeground(activeColor);
            label.setFont(label.getFont().deriveFont(Font.BOLD)); // In đậm khi được chọn (hàm derivefont để giữ nguyên font gốc)

            //vẽ vạch tím bên trái
            label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0,4,0,0,activeColor), // vạch tím bên trái 4 px
                BorderFactory.createEmptyBorder(10,16,10,10)
            ));
            
        }else{
            label.setBackground(list.getBackground()); // màu nền mặc định của list
            label.setForeground(list.getForeground()); // màu chữ mặc định của list
        }
        return label;

    }

    
}
