package com.hrm.UI.HR.PermissionTab;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.formdev.flatlaf.FlatClientProperties;

import java.awt.*;

public class RoleBarPanel extends JPanel {

    private JList<String> roleList;

    public RoleBarPanel(){
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(280,0));

        this.putClientProperty(FlatClientProperties.STYLE, "arc: 20");
        this.setBackground(Color.WHITE);

        // dữ liệu mẫu:
        DefaultListModel<String> model = new DefaultListModel<>();
        model.addElement("Nhân sự (HR)");
        model.addElement("Quản lý (Manager)");
        model.addElement("Nhân viên (Employee)");

        roleList = new JList<>(model);
        roleList.setCellRenderer(new RoleCellRenderer());

        this.add(new JScrollPane(roleList),BorderLayout.CENTER);

    }

    public JList<String> getRoleList() {return roleList;}
    
}
