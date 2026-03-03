package com.hrm.UI.HR.SalaryTab;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.YearMonth;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import com.formdev.flatlaf.FlatClientProperties;
import com.hrm.Service.SalaryService;
import com.hrm.Service.SalaryService.SalaryStatistics;

public class SalarySummary extends JPanel {
    private JLabel lblTotalSalary;
    private JLabel lblEmployeeCount;
    private JLabel lblAverageSalary;
    private JComboBox<YearMonth> cbMonth;
    private SalaryService salaryService;
    private Runnable onMonthChanged;
    private DecimalFormat df = new DecimalFormat("#,###");

    public SalarySummary(Runnable onMonthChanged) {
        this.salaryService = new SalaryService();
        this.onMonthChanged = onMonthChanged;
        
        this.setLayout(new GridLayout(1, 4, 15, 0));
        this.setOpaque(false);

        lblTotalSalary = new JLabel();
        lblEmployeeCount = new JLabel();
        lblAverageSalary = new JLabel();
        
        add(new SummaryCard("Tổng lương tháng này", lblTotalSalary, "#6600cc", true));
        add(new SummaryCard("Số nhân viên", lblEmployeeCount, "#ffffff", false));
        add(new SummaryCard("Trung bình lương", lblAverageSalary, "#ffffff", false));
        add(new MonthSelectorCard());
        
        // Load dữ liệu ban đầu
        updateStatistics();
    }

    // Constructor mặc định cho tương thích
    public SalarySummary() {
        this(() -> {});
    }

    private void updateStatistics() {
        YearMonth selected = (YearMonth) cbMonth.getSelectedItem();
        if (selected == null) {
            selected = YearMonth.now();
        }
        
        int thang = selected.getMonthValue();
        int nam = selected.getYear();
        
        SalaryStatistics stats = salaryService.getSalaryStatistics(thang, nam);
        
        lblTotalSalary.setText(df.format(stats.totalSalary.doubleValue()) + " đ");
        lblEmployeeCount.setText(String.valueOf(stats.employeeCount));
        lblAverageSalary.setText(df.format(stats.averageSalary.doubleValue()) + " đ");
    }

    public YearMonth getSelectedMonth() {
        return (YearMonth) cbMonth.getSelectedItem();
    }

    // Thẻ hiển thị tổng quan với tiêu đề và giá trị động
    private static class SummaryCard extends JPanel {
        public SummaryCard(String title, JLabel valueLabel, String hexColor, boolean isDark) {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            putClientProperty(FlatClientProperties.STYLE, "arc: 15; background: " + hexColor);

            JLabel lblTitle = new JLabel(title);
            lblTitle.setForeground(isDark ? Color.WHITE : Color.GRAY);
            lblTitle.setFont(lblTitle.getFont().deriveFont(13f));

            valueLabel.setForeground(isDark ? Color.WHITE : Color.BLACK);
            valueLabel.putClientProperty(FlatClientProperties.STYLE, "font: bold +6");

            add(lblTitle, BorderLayout.NORTH);
            add(valueLabel, BorderLayout.CENTER);
        }
    }

    // Thẻ chọn tháng với JComboBox
    private class MonthSelectorCard extends JPanel {
        public MonthSelectorCard() {
            setLayout(new BorderLayout(0, 5));
            setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

            putClientProperty("FlatLaf.style", "arc: 15; background: #ffffff");

            JLabel lblTitle = new JLabel("Tháng");
            lblTitle.setForeground(Color.GRAY);

            DefaultComboBoxModel<YearMonth> model = new DefaultComboBoxModel<>();
            
            YearMonth currentMonth = YearMonth.now();
            
            for (int i = 0; i < 12; i++) {
                model.addElement(currentMonth.minusMonths(i));
            }

            cbMonth = new JComboBox<>(model);
            cbMonth.setSelectedItem(currentMonth);
            
            cbMonth.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof YearMonth ym) {
                        setText("Tháng " + ym.getMonthValue() + "/" + ym.getYear());
                    }
                    return this;
                }
            });

            cbMonth.addActionListener(e -> {
                updateStatistics();
                if (onMonthChanged != null) {
                    onMonthChanged.run();
                }
            });

            add(lblTitle, BorderLayout.NORTH);
            add(cbMonth, BorderLayout.CENTER);
        }
    }
}