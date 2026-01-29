package com.hrm.UI.HR;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class EmployeeManagePanel extends JPanel {

    JLabel titleLabel;

    public EmployeeManagePanel() {

        titleLabel = new JLabel("Employee Management");

        this.setSize(800, 600);
        this.setLayout(new BorderLayout());
        this.add(titleLabel, BorderLayout.CENTER);

    }
}
