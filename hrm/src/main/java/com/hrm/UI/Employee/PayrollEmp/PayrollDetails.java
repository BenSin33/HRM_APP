package com.hrm.UI.Employee.PayrollEmp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;

import com.hrm.Service.Employee.PayrollService;
import com.hrm.DTO.Employee.PayrollSummaryDTO;
import com.hrm.Service.PayrollSummaryService;
import com.hrm.utils.ExcelExporter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
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
        String[] columnNames = {"Tháng/Năm", "Lương cơ bản", "Phụ cấp", "Khấu trừ", "Thực lĩnh", "Trạng thái", "Thao tác"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Chỉ cột Thao tác cho phép tương tác
            }
        };
        
        // Lấy dữ liệu chi tiết lương từ database
        PayrollService dao = new PayrollService();
        List<Map<String, Object>> payrollData = dao.getPayrollDetails(manv);
        
        if (payrollData != null && !payrollData.isEmpty()) {
            for (Map<String, Object> row : payrollData) {
                int thang = ((Number) row.get("thang")).intValue();
                int nam = ((Number) row.get("nam")).intValue();
                double luongcb = ((Number) row.get("luongcb")).doubleValue();
                double phucap = ((Number) row.get("phucap")).doubleValue();
                double khautru = ((Number) row.get("khautru")).doubleValue();
                double thuclinh = ((Number) row.get("thuclinh")).doubleValue();
                int trangthai = ((Number) row.get("trangthai")).intValue();
                String tinhtrangtt = (String) row.get("tinhtrangtt");
                
                String trangThaiText = (tinhtrangtt != null && !tinhtrangtt.isEmpty()) ? tinhtrangtt : "Chưa thanh toán";
                
                model.addRow(new Object[]{
                    thang + "/" + nam,
                    String.format("%,.0f đ", luongcb),
                    String.format("%,.0f đ", phucap),
                    String.format("%,.0f đ", khautru),
                    String.format("%,.0f đ", thuclinh),
                    trangThaiText,
                    "actions" // placeholder cho nút
                });
            }
        }
        
        // Tạo và cấu hình bảng
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(230, 230, 230));
        table.setRowHeight(38);
        table.setShowGrid(true);
        table.setGridColor(new Color(200, 200, 200));
        table.setIntercellSpacing(new Dimension(1, 1));
        
        // Căn giữa tất cả các cột dữ liệu
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < 6; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        // Căn giữa header
        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        
        // Gán renderer + editor cho cột Thao tác
        table.getColumnModel().getColumn(6).setCellRenderer(new ActionCellRenderer());
        table.getColumnModel().getColumn(6).setCellEditor(new ActionCellEditor(table, manv, payrollData));
        table.getColumnModel().getColumn(6).setPreferredWidth(200);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            new EmptyBorder(0, 0, 0, 0)
        ));
        scrollPane.setPreferredSize(new Dimension(0, 220));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // Renderer cho cột thao tác
    private static class ActionCellRenderer extends JPanel implements TableCellRenderer {
        private final JButton btnExcel = new JButton("Xuất Excel");

        public ActionCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 3));
            setOpaque(true);
            btnExcel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            btnExcel.setBackground(new Color(34, 197, 94));
            btnExcel.setForeground(Color.WHITE);
            btnExcel.setFocusPainted(false);
            btnExcel.setPreferredSize(new Dimension(90, 28));
            add(btnExcel);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return this;
        }
    }

    // Editor cho cột thao tác
    private class ActionCellEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 3));
        private final JButton btnExcel = new JButton("Xuất Excel");
        private int currentRow;

        public ActionCellEditor(JTable table, String manv, List<Map<String, Object>> payrollData) {
            panel.setOpaque(true);
            btnExcel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            btnExcel.setBackground(new Color(34, 197, 94));
            btnExcel.setForeground(Color.WHITE);
            btnExcel.setFocusPainted(false);
            btnExcel.setPreferredSize(new Dimension(90, 28));
            panel.add(btnExcel);

            btnExcel.addActionListener(e -> {
                fireEditingStopped();
                if (payrollData != null && currentRow < payrollData.size()) {
                    Map<String, Object> row = payrollData.get(currentRow);
                    int thang = ((Number) row.get("thang")).intValue();
                    int nam = ((Number) row.get("nam")).intValue();
                    double luongcb = ((Number) row.get("luongcb")).doubleValue();
                    double phucap = ((Number) row.get("phucap")).doubleValue();
                    double khautru = ((Number) row.get("khautru")).doubleValue();
                    double thuclinh = ((Number) row.get("thuclinh")).doubleValue();
                    int trangthai = ((Number) row.get("trangthai")).intValue();
                    String tinhtrangtt = (String) row.get("tinhtrangtt");

                    String[] headers = {"Tháng/Năm", "Lương cơ bản", "Phụ cấp", "Khấu trừ", "Thực lĩnh", "Trạng thái", "Tình trạng TT"};
                    List<Object[]> data = new ArrayList<>();
                    data.add(new Object[]{
                        thang + "/" + nam,
                        String.format("%,.0f", luongcb),
                        String.format("%,.0f", phucap),
                        String.format("%,.0f", khautru),
                        String.format("%,.0f", thuclinh),
                        trangthai == 1 ? "Đã chốt" : "Chưa chốt",
                        tinhtrangtt != null ? tinhtrangtt : ""
                    });
                    ExcelExporter.exportToExcelWithDialog(headers, data, panel, "Luong_" + manv + "_" + thang + "_" + nam);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = row;
            panel.setBackground(Color.WHITE);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "actions";
        }
    }
}