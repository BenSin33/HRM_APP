package com.hrm.UI.Manager.team;
import com.hrm.UI.Manager.color.ColorScheme;
import javax.swing.*;
import java.awt.*;


public class TeamFooter extends JPanel {
    public TeamFooter() {
        setLayout(new BorderLayout(10, 0));
        setBackground(new Color(239, 246, 255));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(191, 219, 254), 1, true),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        setPreferredSize(new Dimension(0, 70));

        JLabel icon = new JLabel("ℹ");
        icon.setFont(new Font("Segoe UI", Font.BOLD, 18));
        icon.setForeground(new Color(59, 130, 246));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(new Color(239, 246, 255));

        JLabel title = new JLabel("Quyền hạn của Manager");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(new Color(30, 64, 175));

        JLabel desc = new JLabel("Bạn chỉ có thể xem thông tin nhân viên trong phòng ban của mình. Không thể chỉnh sửa hoặc xóa thông tin.");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        desc.setForeground(new Color(59, 130, 246));

        textPanel.add(title);
        textPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        textPanel.add(desc);

        add(icon, BorderLayout.WEST);
        add(textPanel, BorderLayout.CENTER);
    }
}
