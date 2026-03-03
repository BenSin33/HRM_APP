package com.hrm.UI.Employee.ScheduleEmp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;

public class ScheduleHeader extends JPanel {
    public ScheduleHeader(LocalDate weekStart) {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));
        setBorder(new EmptyBorder(15, 25, 15, 25));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        titlePanel.setOpaque(false);
        JLabel lblMainTitle = new JLabel("Lịch làm việc của tôi");
        lblMainTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        JLabel lblSubTitle = new JLabel("Xem lịch làm việc được phân công trong tuần");
        lblSubTitle.setForeground(Color.GRAY);
        lblSubTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titlePanel.add(lblMainTitle);
        titlePanel.add(lblSubTitle);

        add(titlePanel, BorderLayout.WEST);
    }
}
