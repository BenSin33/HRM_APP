package com.hrm.UI.Employee.PayrollEmp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.hrm.DAO.Employee.PayrollDAO;
import com.hrm.DTO.Employee.PayrollSummaryDTO;
import com.hrm.Service.PayrollSummaryService;

import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class PayrollDetails extends JPanel {
    private String manv;
    private int selectedMonth;
    private int selectedYear;
    private PayrollCurrentMonth currentMonthPanel;
    
    public PayrollDetails(String manv, String monthOffset) {
        this.manv = manv;
        
        // Parse monthOffset
        if ("-1".equals(monthOffset)) {
            LocalDate prev = LocalDate.now().minusMonths(1);
            this.selectedMonth = prev.getMonthValue();
            this.selectedYear = prev.getYear();
        } else {
            LocalDate now = LocalDate.now();
            this.selectedMonth = now.getMonthValue();
            this.selectedYear = now.getYear();
        }
        
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));

        // Panel chi tiết lương tháng được chọn
        currentMonthPanel = new PayrollCurrentMonth(manv, selectedMonth, selectedYear);
        
        // Panel bảng thống kê lương từng tháng
        JPanel statsPanel = createPayrollStatsPanel(manv);

        // Thêm vào với divider
        add(currentMonthPanel, BorderLayout.NORTH);
        add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.CENTER);
        add(statsPanel, BorderLayout.SOUTH);
    }
    
    public void updateMonth(int month, int year) {
        this.selectedMonth = month;
        this.selectedYear = year;
        
        // Xóa toàn bộ component cũ
        removeAll();
        
        // Tạo lại UI
        initializeUI();
        
        // Vẽ lại
        revalidate();
        repaint();
    }

    private JPanel createPayrollStatsPanel(String manv) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 249, 250));
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Tiêu đề
        JLabel lblTitle = new JLabel("Thống kê lương từng tháng");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(lblTitle, BorderLayout.NORTH);

        // Tạo bảng chi tiết lương
        String[] columnNames = {"Tháng/Năm", "Lương cơ bản", "Phụ cấp", "Khấu trừ", "Thực lĩnh"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        
        // Lấy dữ liệu chi tiết lương từ database
        PayrollDAO dao = new PayrollDAO();
        List<Map<String, Object>> payrollData = dao.getPayrollDetails(manv);
        
        if (payrollData != null && !payrollData.isEmpty()) {
            for (Map<String, Object> row : payrollData) {
                int thang = ((Number) row.get("thang")).intValue();
                int nam = ((Number) row.get("nam")).intValue();
                double luongcb = ((Number) row.get("luongcb")).doubleValue();
                double phucap = ((Number) row.get("phucap")).doubleValue();
                double khautru = ((Number) row.get("khautru")).doubleValue();
                double thuclinh = ((Number) row.get("thuclinh")).doubleValue();
                
                model.addRow(new Object[]{
                    thang + "/" + nam,
                    String.format("%,.0f đ", luongcb),
                    String.format("%,.0f đ", phucap),
                    String.format("%,.0f đ", khautru),
                    String.format("%,.0f đ", thuclinh)
                });
            }
        }
        
        // Tạo và cấu hình bảng
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(230, 230, 230));
        table.setRowHeight(30);
        table.setEnabled(false); // Không cho chỉnh sửa
        table.setShowGrid(true); // Hiển thị grid lines
        table.setGridColor(new Color(200, 200, 200)); // Màu grid
        table.setIntercellSpacing(new Dimension(1, 1)); // Khoảng cách giữa các ô
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            new EmptyBorder(0, 0, 0, 0)
        ));
        scrollPane.setPreferredSize(new Dimension(0, 180));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
}