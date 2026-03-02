package com.hrm.UI.Employee.LeaveEmp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LeaveHistory extends JPanel {
    public LeaveHistory() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel lblTitle = new JLabel("Lịch sử đơn nghỉ phép");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(lblTitle);
        add(Box.createVerticalStrut(15));

        // Mockup dữ liệu đơn
        add(createLeaveItem("Nghỉ phép năm", "1/2/2025 - 3/2/2025", "Du lịch gia đình", "Chờ duyệt", new Color(255, 193, 7)));
        add(Box.createVerticalStrut(10));
        add(createLeaveItem("Nghỉ ốm", "15/1/2025 - 16/1/2025", "Bị cảm cúm", "Đã duyệt", new Color(34, 197, 94)));
    }

    private JPanel createLeaveItem(String type, String date, String reason, String status, Color statusColor) {
        JPanel item = new JPanel(new BorderLayout(20, 0));
        item.setBackground(new Color(250, 250, 250));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        item.setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setOpaque(false);
        JLabel lblType = new JLabel(type + " - " + date);
        lblType.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel lblReason = new JLabel("Lý do: " + reason);
        lblReason.setForeground(Color.GRAY);
        info.add(lblType);
        info.add(lblReason);

        JLabel lblStatus = new JLabel(status);
        lblStatus.setForeground(statusColor);
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));

        item.add(info, BorderLayout.CENTER);
        item.add(lblStatus, BorderLayout.EAST);
        return item;
    }
}