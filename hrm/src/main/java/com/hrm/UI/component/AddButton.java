package com.hrm.UI.component;

import java.awt.Cursor;

import javax.swing.JButton;
import javax.swing.JPanel;
public class AddButton extends JButton{
    public AddButton(String title, JPanel formPanel, Runnable onSuccess) {
        super("Thêm mới");
        putClientProperty("JButton.buttonType", "roundRect");
        this.setBackground(new java.awt.Color(102, 0, 204)); // Màu tím đặc trưng
        this.setForeground(java.awt.Color.WHITE);
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));

        /*
        this.addActionListener( e -> {
            //tự động tìm parent frame (HR Dashboard)
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            
            // Mở dialog CRUD hiển thị form thêm
            CRUDDialog<T> dialog = new CRUDDialog<>(parentFrame, "Thêm mới" + title, formPanel, null);
            dialog.setVisible(true);

            if(dialog.getResult() != null) {
            
                //logic insert vào database ở đây
                
                onSuccess.run();

        })
        */
    }
}
