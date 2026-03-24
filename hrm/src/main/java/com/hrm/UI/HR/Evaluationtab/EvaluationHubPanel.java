package com.hrm.UI.HR.Evaluationtab;

import com.formdev.flatlaf.FlatClientProperties;
import com.hrm.DTO.UserDTO;
import com.hrm.UI.Employee.EvaluationEmp.EvaluationManage;
import com.hrm.utils.SessionManager;

import javax.swing.*;
import java.awt.*;

public class EvaluationHubPanel extends JPanel {
    public EvaluationHubPanel() {
        setLayout(new BorderLayout());
        putClientProperty(FlatClientProperties.STYLE, "background: #f8f9fa");

        JTabbedPane tabs = new JTabbedPane();
        tabs.putClientProperty(FlatClientProperties.STYLE, "arc: 10");

        tabs.addTab("Quản lý đánh giá", new EvaluationManagement());

        UserDTO currentUser = SessionManager.getInstance().getCurrentUser();
        String manv = currentUser != null ? currentUser.getManv() : "";
        tabs.addTab("Đánh giá cá nhân", new EvaluationManage(manv));

        add(tabs, BorderLayout.CENTER);
    }
}

