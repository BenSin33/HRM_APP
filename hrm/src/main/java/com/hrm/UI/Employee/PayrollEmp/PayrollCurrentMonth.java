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
            double tong_phucap = (Double) currentPayroll.get("tong_phucap");
            double tong_khautru = (Double) currentPayroll.get("tong_khautru");
            double thuclinh = (Double) currentPayroll.get("thuclinh");

            // Panel tóm tắt
            JPanel summaryPanel = createSummaryPanel(thang, nam, luongcb, tong_phucap, tong_khautru, thuclinh);
            add(summaryPanel, BorderLayout.NORTH);

            // Panel chi tiết các khoản
            JPanel detailPanel = createDetailPanel(maluong);
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
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitle.setForeground(Color.GRAY);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblValue.setForeground(color);

        item.add(lblTitle, BorderLayout.NORTH);
        item.add(lblValue, BorderLayout.CENTER);

        return item;
    }

    private JPanel createDetailPanel(String maluong) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 249, 250));

        // Tiêu đề
        JLabel lblTitle = new JLabel("Chi tiết các khoản");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(lblTitle, BorderLayout.NORTH);

        // Panel chứa các dòng chi tiết
        JPanel detailsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        detailsPanel.setBackground(new Color(248, 249, 250));

        PayrollDAO dao = new PayrollDAO();
        List<Map<String, Object>> items = dao.getPayrollDetailItems(maluong);

        if (items != null && !items.isEmpty()) {
            for (Map<String, Object> item : items) {
                detailsPanel.add(createDetailCard(item));
            }
        }

        JScrollPane scrollPane = new JScrollPane(detailsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
    
    private JPanel createDetailCard(Map<String, Object> item) {
        String tenkhoan = (String) item.get("tenkhoan");
        String loai = (String) item.get("loai");
        double sotien = (Double) item.get("sotien");
        Color bgColor = "CONG".equals(loai) ? new Color(209, 250, 229) : new Color(254, 226, 226);
        Color textColor = "CONG".equals(loai) ? new Color(34, 197, 94) : new Color(239, 68, 68);
        String sotiendisplay = "CONG".equals(loai) ? String.format("+%,.0f đ", sotien) : String.format("-%,.0f đ", sotien);

        JPanel card = new JPanel(new GridLayout(2, 1, 0, 5));
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(textColor),
            new EmptyBorder(10, 10, 10, 10)
        ));
        card.setPreferredSize(new Dimension(100, 90));
        
        JLabel lblName = new JLabel(tenkhoan);
        lblName.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblName.setForeground(Color.BLACK);
        lblName.setHorizontalAlignment(JLabel.CENTER);
        
        JLabel lblAmount = new JLabel(sotiendisplay);
        lblAmount.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblAmount.setForeground(textColor);
        lblAmount.setHorizontalAlignment(JLabel.CENTER);
        
        card.add(lblName);
        card.add(lblAmount);
        
        return card;
    }
}
