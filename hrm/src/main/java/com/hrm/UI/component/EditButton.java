package com.hrm.UI.component;

import java.awt.Cursor;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import com.formdev.flatlaf.extras.*;

public class EditButton<T> extends JButton{
    
    public EditButton(JFrame parent, JPanel formPanel, Supplier<T> dataSupplier, Consumer<T> onUpdate) {

        try {
            this.setIcon(new FlatSVGIcon("/icons/edit_button.svg",32,32));
        }catch(Exception e ){
            System.err.println("Không tìm thấy icon" + e.getMessage());
        }
        this.putClientProperty("JButton.buttonType", "toolBarButton");
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.setToolTipText("Sửa");

        this.addActionListener( e -> {
            T data = dataSupplier.get(); // lấy dữ liệu cũ từ database
            CRUDDialog<T> dialog = new CRUDDialog<>(parent, "cập nhật thông tin", formPanel, data);
            dialog.setVisible(true);

            T result = dialog.getResult();
            if(result != null) {
                onUpdate.accept(result); // thực hiện sửa vào trong database
            }

        });
        
    }
}
