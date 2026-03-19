package com.hrm.UI.Employee.HomeEmp;

import javax.swing.*;
import java.awt.*;

import com.hrm.UI.component.Sidebar;

public class HomeManage extends JPanel {

    public HomeManage(String manv, CardLayout cardLayout, JPanel cardContainer, Sidebar sidebar) {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        contentPanel.add(new HomeHeader(manv));
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(new HomeReport(manv));
        contentPanel.add(Box.createVerticalStrut(10));
        // Truyền mã nhân viên, cardLayout, cardContainer và sidebar cho HomeFooter
        contentPanel.add(new HomeFooter(manv, cardLayout, cardContainer, sidebar));
        contentPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(new Color(248, 249, 250));

        add(scrollPane, BorderLayout.CENTER);
    }
}