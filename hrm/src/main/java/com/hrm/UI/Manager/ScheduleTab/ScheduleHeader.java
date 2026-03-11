package com.hrm.UI.Manager.ScheduleTab;
import javax.swing.*;
import java.awt.*;

public class ScheduleHeader extends JPanel {
    
    public ScheduleHeader() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JPanel leftPanel = new JPanel(new BorderLayout(10, 0));
        leftPanel.setBackground(Color.WHITE);

        JLabel icon = new JLabel("📅");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Xếp lịch làm việc");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JLabel subtitle = new JLabel("Quản lý và phân công ca làm việc cho nhân viên trong team");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(100, 100, 100));

        titlePanel.add(title);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitle);

        leftPanel.add(icon, BorderLayout.WEST);
        leftPanel.add(titlePanel, BorderLayout.CENTER);

       
        add(leftPanel, BorderLayout.WEST);
    }
}
