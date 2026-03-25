package com.hrm.UI.HR.SalaryTab;

import com.formdev.flatlaf.FlatClientProperties;
import com.hrm.UI.Employee.PayrollEmp.PayrollManage;
import com.hrm.DTO.UserDTO;
import com.hrm.utils.SessionManager;

import javax.swing.*;
import java.awt.*;

/**
 * HR: Gom 2 tab con cho mục "Quản lý lương":
 * - Quản lý lương (bảng lương toàn công ty)
 * - Bảng lương cá nhân (reuse panel Employee)
 */
public class PayrollHubPanel extends JPanel {

    public PayrollHubPanel() {
        setLayout(new BorderLayout());
        putClientProperty(FlatClientProperties.STYLE, "background: #f8f9fa");

        JTabbedPane tabs = new JTabbedPane();
        tabs.putClientProperty(FlatClientProperties.STYLE, "arc: 10");

        // Tab 1: HR salary management hiện có
        tabs.addTab("Quản lý lương", new SalaryManagement());

        // Tab 2: Bảng lương cá nhân (lấy từ Employee)
        UserDTO currentUser = SessionManager.getInstance().getCurrentUser();
        String manv = currentUser != null ? currentUser.getManv() : "";
        tabs.addTab("Bảng lương cá nhân", new PayrollManage(manv));

        add(tabs, BorderLayout.CENTER);
    }
}

