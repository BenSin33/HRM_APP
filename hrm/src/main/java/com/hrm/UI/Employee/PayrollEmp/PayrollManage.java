package com.hrm.UI.Employee.PayrollEmp;

import javax.swing.*;

import com.hrm.DAO.Employee.PayrollDAO;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class PayrollManage extends JPanel {
    public PayrollManage(String manv) {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));

        // Lấy dữ liệu thực tế từ DAO
        PayrollDAO dao = new PayrollDAO();
        List<Map<String, Object>> chartData = dao.getSalaryHistory(manv);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(248, 249, 250));

        // 1. Thẻ tổng quan lương
        contentPanel.add(new PayrollSummary("22.500.000"));
        
        

        // 3. Chi tiết lương
        contentPanel.add(new PayrollDetails());

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        add(new PayrollHeader(), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // 2. BIỂU ĐỒ (Thêm mới ở đây)
        JLabel lblChartTitle = new JLabel("Biểu đồ lương 5 tháng gần nhất");
        lblChartTitle.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 0));
        lblChartTitle.setFont(new Font("Arial", Font.BOLD, 14));
        contentPanel.add(lblChartTitle);
        contentPanel.add(new PayrollChart(chartData));
    }
}
