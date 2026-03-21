package com.hrm.UI.component;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Window;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import com.formdev.flatlaf.extras.FlatSVGIcon;

public class DeleteButton extends JButton {

    public DeleteButton (Component componentSource, String id, Runnable onDeleteSuccess) {

        try{
            this.setIcon(new FlatSVGIcon("icons/delete_button.svg", 32, 32));
        } catch( Exception e) {
            System.err.println("Không tìm thấy icon" + e.getMessage());
        }

        this.putClientProperty("JButton.buttonType", "toolBarButton");
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.setToolTipText("Xoá");

        this.addActionListener( e -> {

            //lấy parent Frame chứa nút để hiển thị thông báo chính giữa
            Window parentWindow = SwingUtilities.getWindowAncestor(componentSource);

            int confirmed = JOptionPane.showConfirmDialog(
                parentWindow,
                "Bạn có chắc chắn muốn xoá dữ liệu này ? \nDữ liệu đã xoá không thể khôi phục !!!",
                "Xác nhận xoá",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if(confirmed == JOptionPane.YES_OPTION){
                onDeleteSuccess.run();
                JOptionPane.showMessageDialog(parentWindow, "Đã xoá dữ liệu thành công !");
            }
            

        });

    }
    
}
