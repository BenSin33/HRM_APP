package com.hrm.UI.HR.SalaryTab;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.time.YearMonth;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import com.formdev.flatlaf.FlatClientProperties;

public class SalarySummary extends JPanel {

    public SalarySummary() {
        
        this.setLayout(new GridLayout(1,4,15,0));  // 1 hàng, 4 cột, khoảng cách ngang 15px
        this.setOpaque(false);  // đặt JPanel trong suốt

        add(new SummaryCard("Tổng lương tháng này", "62.000.000 đ", "#6600cc", true));
        add(new SummaryCard("Số nhân viên", "3", "#ffffff", false));
        add(new SummaryCard("Trung bình lương", "20.666.667 đ", "#ffffff", false));
        add(new MonthSelectorCard());

    }

    // Thẻ hiển thị tổng quan với tiêu đề và giá trị
    private static class SummaryCard extends JPanel {
        public SummaryCard(String title, String value, String hexColor, boolean isDark) {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            // Thiết lập bo góc 15px và màu nền bằng FlatLaf
            putClientProperty(FlatClientProperties.STYLE, "arc: 15; background: " + hexColor);  // Màu nền từ tham số hexColor 

            // Tiêu đề thẻ
            JLabel lblTitle = new JLabel(title);
            lblTitle.setForeground(isDark ? Color.WHITE : Color.GRAY);
            lblTitle.setFont(lblTitle.getFont().deriveFont(13f));

            // Giá trị hiển thị (Dạng text tĩnh)
            JLabel lblValue = new JLabel(value);
            lblValue.setForeground(isDark ? Color.WHITE : Color.BLACK);
            lblValue.putClientProperty(FlatClientProperties.STYLE, "font: bold +6");

            add(lblTitle, BorderLayout.NORTH);
            add(lblValue, BorderLayout.CENTER);
        }
    }

    // Thẻ chọn tháng với JComboBox
    private static class MonthSelectorCard extends JPanel {
    public MonthSelectorCard() {
        setLayout(new BorderLayout(0, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));   // padding bên trong thẻ

         // Thiết lập bo góc 15px và màu nền trắng bằng FlatLaf
        putClientProperty("FlatLaf.style", "arc: 15; background: #ffffff");     // Màu nền trắng và bo góc 15px

        JLabel lblTitle = new JLabel("Tháng");
        lblTitle.setForeground(Color.GRAY);

        // Tạo ComboBox với kiểu dữ liệu là YearMonth thay vì String
        DefaultComboBoxModel<YearMonth> model = new DefaultComboBoxModel<>();
        
        // Lấy tháng hiện tại
        YearMonth currentMonth = YearMonth.now();
        
        // Tự động add 6 tháng gần nhất vào danh sách (Không cần hard code)
        for (int i = 0; i < 6; i++) {
            model.addElement(currentMonth.minusMonths(i));
        }

        JComboBox<YearMonth> cbMonth = new JComboBox<>(model);
        
        // Sử dụng Renderer để hiển thị YearMonth theo định dạng "Tháng MM/yyyy"
        cbMonth.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof YearMonth) {
                    YearMonth ym = (YearMonth) value;
                    setText("Tháng " + ym.getMonthValue() + "/" + ym.getYear());
                }
                return this;
            }
        });

        // Dự định: Khi chọn tháng, YearMonth sẽ cung cấp tham số chuẩn để truy vấn Database
        cbMonth.addActionListener(e -> {
            YearMonth selected = (YearMonth) cbMonth.getSelectedItem();
            // // Logic: Lấy tháng và năm để gọi vào SalaryDAO.java
            System.out.println("Truy vấn tháng: " + selected.getMonthValue() + " Năm: " + selected.getYear());
        });

        add(lblTitle, BorderLayout.NORTH);
        add(cbMonth, BorderLayout.CENTER);
    }
}


    
}