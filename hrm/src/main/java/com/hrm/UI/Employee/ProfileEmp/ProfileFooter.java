package com.hrm.UI.Employee.ProfileEmp;
import java.awt.*;
import javax.swing.*;

class ProfileFooter extends JPanel {
    public ProfileFooter() {
        setBackground(new Color(245, 246, 250));
        setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));
        setLayout(new BorderLayout());

        JPanel noteBox = new JPanel();
        noteBox.setBackground(new Color(232, 244, 255));
        noteBox.setBorder(BorderFactory.createLineBorder(new Color(180, 210, 255)));
        
        JLabel lblNote = new JLabel("ⓘ Lưu ý: Bạn chỉ có thể xem thông tin cá nhân. Nếu cần cập nhật, vui lòng liên hệ phòng nhân sự.");
        lblNote.setForeground(new Color(0, 100, 200));
        noteBox.add(lblNote);

        add(noteBox, BorderLayout.CENTER);
    }
}