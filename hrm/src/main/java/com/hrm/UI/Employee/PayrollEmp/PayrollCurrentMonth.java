package com.hrm.UI.Employee.PayrollEmp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import com.hrm.DAO.Employee.PayrollDAO;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PayrollCurrentMonth extends JPanel {
    public PayrollCurrentMonth(String manv) {
        this(manv, java.time.LocalDate.now().minusMonths(1).getMonthValue(), 
             java.time.LocalDate.now().minusMonths(1).getYear());
    }
    
    public PayrollCurrentMonth(String manv, int selectedMonth, int selectedYear) {
        setLayout(new BorderLayout(0, 15));
        setBackground(new Color(248, 249, 250));
        setBorder(new EmptyBorder(20, 25, 20, 25));

        PayrollDAO dao = new PayrollDAO();
        
        // Lấy dữ liệu lương theo tháng/năm được chọn
        Map<String, Object> currentPayroll = dao.getPayrollByMonth(manv, selectedMonth, selectedYear);
        
        if (currentPayroll != null && !currentPayroll.isEmpty()) {
            String maluong = (String) currentPayroll.get("maluong");
            int thang = (Integer) currentPayroll.get("thang");
            int nam = (Integer) currentPayroll.get("nam");
            double luongcb = (Double) currentPayroll.get("luongcb");
            double songaycong = currentPayroll.get("songaycong") != null ? ((Number) currentPayroll.get("songaycong")).doubleValue() : 0;
            double tong_phucap = (Double) currentPayroll.get("tong_phucap");
            double tong_khautru = (Double) currentPayroll.get("tong_khautru");
            double thuclinh = (Double) currentPayroll.get("thuclinh");

            // Panel tóm tắt
            JPanel summaryPanel = createSummaryPanel(thang, nam, luongcb, tong_phucap, tong_khautru, thuclinh);
            add(summaryPanel, BorderLayout.NORTH);

            // Panel chi tiết các khoản - dạng bảng rõ ràng
            JPanel detailPanel = createDetailPanel(maluong, luongcb, songaycong, tong_phucap, tong_khautru, thuclinh);
            add(detailPanel, BorderLayout.CENTER);
        } else {
            // Nếu không có dữ liệu tháng trước
            JLabel noDataLabel = new JLabel("Không có dữ liệu lương tháng trước");
            noDataLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            noDataLabel.setForeground(Color.GRAY);
            add(noDataLabel, BorderLayout.CENTER);
        }
    }

    private JPanel createSummaryPanel(int thang, int nam, double luongcb, double tong_phucap, double tong_khautru, double thuclinh) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 4, 15, 0));
        panel.setBackground(new Color(248, 249, 250));

        panel.add(createSummaryItem("Lương cơ bản", String.format("%,.0f đ", luongcb), new Color(59, 130, 246)));
        panel.add(createSummaryItem("Phụ cấp", String.format("+%,.0f đ", tong_phucap), new Color(34, 197, 94)));
        panel.add(createSummaryItem("Khấu trừ", String.format("-%,.0f đ", tong_khautru), new Color(239, 68, 68)));
        panel.add(createSummaryItem("Thực lĩnh", String.format("%,.0f đ", thuclinh), new Color(99, 102, 241)));

        return panel;
    }

    private JPanel createSummaryItem(String title, String value, Color color) {
        JPanel item = new JPanel(new BorderLayout(0, 5));
        item.setBackground(Color.WHITE);
        item.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitle.setForeground(Color.GRAY);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblValue.setForeground(color);

        item.add(lblTitle, BorderLayout.NORTH);
        item.add(lblValue, BorderLayout.CENTER);

        return item;
    }

    private JPanel createDetailPanel(String maluong, double luongcb, double songaycong, double tongPhucap, double tongKhautru, double thuclinh) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 249, 250));

        // Tiêu đề
        JLabel lblTitle = new JLabel("Chi tiết bảng lương");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(lblTitle, BorderLayout.NORTH);

        // Panel chứa các dòng chi tiết
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);
        listPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            new EmptyBorder(10, 15, 10, 15)
        ));

        // === PHẦN LƯƠNG CƠ BẢN ===
        listPanel.add(createSectionHeader("LƯƠNG CƠ BẢN"));
        listPanel.add(createDetailRow("Lương cơ bản (hợp đồng)", String.format("%,.0f đ", luongcb), new Color(33, 37, 41)));
        listPanel.add(createDetailRow("Số ngày công thực tế", String.format("%.1f ngày", songaycong), new Color(33, 37, 41)));
        double luongTheoNgayCong = (songaycong > 0) ? luongcb * songaycong / 26.0 : luongcb;
        listPanel.add(createDetailRow("Lương theo ngày công (÷26 ngày)", String.format("%,.0f đ", luongTheoNgayCong), new Color(59, 130, 246)));
        listPanel.add(createDivider());

        // Lấy danh mục phụ cấp và khấu trừ từ DB
        PayrollDAO dao = new PayrollDAO();
        List<Map<String, Object>> allowances = dao.getAllAllowances();
        List<Map<String, Object>> deductions = dao.getAllDeductions();

        // === PHẦN PHỤ CẤP (CỘNG) ===
        listPanel.add(createSectionHeader("CÁC KHOẢN PHỤ CẤP (+)"));
        double sumPhucap = 0;
        if (allowances != null && !allowances.isEmpty()) {
            for (Map<String, Object> item : allowances) {
                String ten = (String) item.get("ten");
                double sotien = (Double) item.get("sotien");
                sumPhucap += sotien;
                listPanel.add(createDetailRow(ten, String.format("+%,.0f đ", sotien), new Color(34, 197, 94)));
            }
        } else {
            listPanel.add(createDetailRow("(Không có khoản phụ cấp)", "", Color.GRAY));
        }
        listPanel.add(createDetailRow("Tổng phụ cấp", String.format("+%,.0f đ", sumPhucap), new Color(34, 197, 94)));
        listPanel.add(createDivider());

        // === PHẦN KHẤU TRỪ ===
        listPanel.add(createSectionHeader("CÁC KHOẢN KHẤU TRỪ (-)"));
        double sumKhautru = 0;
        if (deductions != null && !deductions.isEmpty()) {
            for (Map<String, Object> item : deductions) {
                String ten = (String) item.get("ten");
                double sotien = (Double) item.get("sotien");
                sumKhautru += sotien;
                listPanel.add(createDetailRow(ten, String.format("-%,.0f đ", sotien), new Color(239, 68, 68)));
            }
        } else {
            listPanel.add(createDetailRow("(Không có khoản khấu trừ)", "", Color.GRAY));
        }
        listPanel.add(createDetailRow("Tổng khấu trừ", String.format("-%,.0f đ", sumKhautru), new Color(239, 68, 68)));
        listPanel.add(createDivider());

        // === THỰC LĨNH ===
        listPanel.add(createTotalRow("THỰC LĨNH", String.format("%,.0f đ", thuclinh)));

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSectionHeader(String text) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(new Color(243, 244, 246));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        row.setBorder(new EmptyBorder(8, 10, 8, 10));

        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(new Color(75, 85, 99));
        row.add(lbl, BorderLayout.WEST);
        return row;
    }

    private JPanel createDetailRow(String label, String value, Color valueColor) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        row.setBorder(new EmptyBorder(6, 20, 6, 20));

        JLabel lblName = new JLabel(label);
        lblName.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblName.setForeground(new Color(55, 65, 81));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblValue.setForeground(valueColor);
        lblValue.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(lblName, BorderLayout.WEST);
        row.add(lblValue, BorderLayout.EAST);
        return row;
    }

    private JPanel createTotalRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(new Color(238, 242, 255));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        row.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel lblName = new JLabel(label);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblName.setForeground(new Color(99, 102, 241));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblValue.setForeground(new Color(99, 102, 241));
        lblValue.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(lblName, BorderLayout.WEST);
        row.add(lblValue, BorderLayout.EAST);
        return row;
    }

    private JSeparator createDivider() {
        JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(229, 231, 235));
        return sep;
    }
}
