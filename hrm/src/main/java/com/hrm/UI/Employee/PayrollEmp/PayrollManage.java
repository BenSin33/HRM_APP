package com.hrm.UI.Employee.PayrollEmp;

import javax.swing.*;

import com.hrm.Service.Employee.PayrollService;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class PayrollManage extends JPanel {
    private String manv;
    private PayrollHeader header;
    private PayrollCurrentMonth currentMonthPanel;
    private PayrollDetails detailsPanel;
    
    public PayrollManage(String manv) {
        this.manv = manv;
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));

        // Lấy dữ liệu thực tế từ DAO
        PayrollService dao = new PayrollService();
        List<Map<String, Object>> chartData = dao.getSalaryHistory(manv);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(248, 249, 250));

        // Header với bộ lọc
        header = new PayrollHeader();
        header.addMonthChangeListener(e -> updatePayrollData());

        // 1. Thẻ tóm tắt lương
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setBackground(new Color(248, 249, 250));
        summaryPanel.add(new PayrollSummary(manv));
        contentPanel.add(summaryPanel);

        // 2. Chi tiết lương tháng
        detailsPanel = new PayrollDetails(manv, "-1"); // -1 = tháng trước
        contentPanel.add(detailsPanel);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        add(header, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // 3. Biểu đồ
        JLabel lblChartTitle = new JLabel("Biểu đồ lương 12 tháng gần đây");
        lblChartTitle.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 0));
        lblChartTitle.setFont(new Font("Arial", Font.BOLD, 14));
        contentPanel.add(lblChartTitle);
        contentPanel.add(new PayrollChart(chartData));
    }
    
    private void updatePayrollData() {
        String selected = header.getSelectedMonth();
        // Trích xuất tháng từ "Tháng MM/YYYY"
        String[] parts = selected.split("/");
        int selectedMonth = Integer.parseInt(parts[0].replaceAll("[^0-9]", ""));
        int selectedYear = Integer.parseInt(parts[1]);
        
        // Cập nhật các panel
        detailsPanel.updateMonth(selectedMonth, selectedYear);
    }
}
