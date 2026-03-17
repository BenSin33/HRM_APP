package com.hrm.UI.Manager.team;
import javax.swing.*;
import java.awt.*;

public class TeamHeader extends JPanel {
    private JLabel title;
    private JLabel subtitle;

    public TeamHeader() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Left panel with title
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Color.WHITE);

        title = new JLabel("Đội nhóm - Phòng Quản Lý");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));

        subtitle = new JLabel("Danh sách thành viên trong team");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(100, 100, 100));

        leftPanel.add(title);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(subtitle);

        add(leftPanel, BorderLayout.WEST);
    }
}
