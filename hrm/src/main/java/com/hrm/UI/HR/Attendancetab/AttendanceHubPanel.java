package com.hrm.UI.HR.Attendancetab;

import com.formdev.flatlaf.FlatClientProperties;
import com.hrm.DTO.UserDTO;
import com.hrm.UI.Employee.AttendanceEmp.AttendanceManage;
import com.hrm.utils.SessionManager;

import javax.swing.*;
import java.awt.*;

public class AttendanceHubPanel extends JPanel {
    public AttendanceHubPanel() {
        setLayout(new BorderLayout());
        putClientProperty(FlatClientProperties.STYLE, "background: #f8f9fa");

        JTabbedPane tabs = new JTabbedPane();
        tabs.putClientProperty(FlatClientProperties.STYLE, "arc: 10");

        tabs.addTab("Quản lý chấm công", new AttenDanceManagement());

        UserDTO currentUser = SessionManager.getInstance().getCurrentUser();
        String manv = currentUser != null ? currentUser.getManv() : "";
        tabs.addTab("Chấm công cá nhân", new AttendanceManage(manv));

        add(tabs, BorderLayout.CENTER);
    }
}

