package com.hrm.UI.Employee;

import javax.swing.*;
import java.awt.*;

public class HeaderPanel extends JPanel {
    public HeaderPanel(String employeeName) {
        setLayout(new BorderLayout());
        setOpaque(false); // Đặt trong suốt để hiển thị nền từ JPanel cha

        // Phần text chào hỏi
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel lblWelcome = new JLabel("Xin chào, " + employeeName + "!");
        lblWelcome.setFont(new Font("Inter", Font.BOLD, 28));
        lblWelcome.setForeground(new Color(33, 33, 33));

        JLabel lblDate = new JLabel("Hôm nay là Thứ Tư, 4 tháng 2, 2026");
        lblDate.setFont(new Font("Inter", Font.PLAIN, 14));
        lblDate.setForeground(Color.GRAY);

        textPanel.add(lblWelcome);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(lblDate);

        add(textPanel, BorderLayout.WEST);
    }
}
