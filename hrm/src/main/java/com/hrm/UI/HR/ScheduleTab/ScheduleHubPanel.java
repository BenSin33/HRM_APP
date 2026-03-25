package com.hrm.UI.HR.ScheduleTab;

import com.formdev.flatlaf.FlatClientProperties;
import com.hrm.DTO.UserDTO;
import com.hrm.UI.Employee.ScheduleEmp.ScheduleManage;
import com.hrm.UI.Manager.ScheduleTab.SchedulePanel;
import com.hrm.utils.SessionManager;

import javax.swing.*;
import java.awt.*;

public class ScheduleHubPanel extends JPanel {
    public ScheduleHubPanel() {
        setLayout(new BorderLayout());
        putClientProperty(FlatClientProperties.STYLE, "background: #f8f9fa");

        JTabbedPane tabs = new JTabbedPane();
        tabs.putClientProperty(FlatClientProperties.STYLE, "arc: 10");

        tabs.addTab("Lịch làm việc (quản lý)", new SchedulePanel());

        UserDTO currentUser = SessionManager.getInstance().getCurrentUser();
        String manv = currentUser != null ? currentUser.getManv() : "";
        tabs.addTab("Lịch làm việc cá nhân", new ScheduleManage(manv));

        add(tabs, BorderLayout.CENTER);
    }
}

