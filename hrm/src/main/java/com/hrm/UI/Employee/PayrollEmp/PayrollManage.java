package com.hrm.UI.Employee.PayrollEmp;

import javax.swing.*;
import java.awt.*;

public class PayrollManage extends JPanel {
    public PayrollManage(String manv) {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));

        // Sử dụng một JPanel trung tâm để chứa Summary và Details (vì chúng nằm trong vùng cuộn)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(248, 249, 250));

        contentPanel.add(new PayrollSummary("22.500.000"));
        contentPanel.add(new PayrollDetails());
        
        // Thêm phần Lịch sử lương (Tùy chọn nếu bạn muốn hiển thị bảng như trong hình)
        // contentPanel.add(new PayrollHistory()); 

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(new PayrollHeader(), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
}