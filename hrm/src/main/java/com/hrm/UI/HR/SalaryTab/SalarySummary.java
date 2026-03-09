package com.hrm.UI.HR.SalaryTab;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.formdev.flatlaf.FlatClientProperties;
import com.hrm.DTO.Employee.SalaryDTO;
import com.hrm.Service.SalaryService;
import com.toedter.calendar.JDateChooser;

public class SalarySummary extends JPanel {
    public enum FilterMode {
        ALL,
        QUICK_MONTH,
        DATE_RANGE
    }

    private JLabel lblTotalSalary;
    private JLabel lblEmployeeCount;
    private JLabel lblAverageSalary;
    private JComboBox<String> cbFilterMode;
    private JComboBox<YearMonth> cbQuickMonth;
    private JDateChooser dcFrom;
    private JDateChooser dcTo;
    private JPanel quickMonthPanel;
    private JPanel dateRangePanel;

    private SalaryService salaryService;
    private Runnable onMonthChanged;
    private DecimalFormat df = new DecimalFormat("#,###");

    private FilterMode filterMode = FilterMode.ALL;
    private YearMonth selectedMonth = null;
    private YearMonth filterFromMonth = null;
    private YearMonth filterToMonth = null;

    public SalarySummary(Runnable onMonthChanged) {
        this.salaryService = new SalaryService();
        this.onMonthChanged = onMonthChanged;

        this.setLayout(new BorderLayout(0, 10));
        this.setOpaque(false);

        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statsPanel.setOpaque(false);

        lblTotalSalary = new JLabel();
        lblEmployeeCount = new JLabel();
        lblAverageSalary = new JLabel();

        statsPanel.add(new SummaryCard("Tổng lương", lblTotalSalary, "#6600cc", true));
        statsPanel.add(new SummaryCard("Số nhân viên", lblEmployeeCount, "#ffffff", false));
        statsPanel.add(new SummaryCard("Trung bình lương", lblAverageSalary, "#ffffff", false));

        JPanel filterPanel = createFilterPanel();

        add(statsPanel, BorderLayout.CENTER);
        add(filterPanel, BorderLayout.SOUTH);

        updateStatistics();
    }

    public SalarySummary() {
        this(() -> {});
    }

    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new BorderLayout(0, 8));
        filterPanel.setOpaque(false);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        modePanel.setOpaque(false);

        JLabel lblMode = new JLabel("Kiểu lọc:");
        lblMode.putClientProperty(FlatClientProperties.STYLE, "font: bold");
        cbFilterMode = new JComboBox<>(new String[]{"Xem tất cả", "Lọc nhanh theo tháng", "Lọc theo ngày (dd/MM/yyyy)"});
        cbFilterMode.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        cbFilterMode.addActionListener(e -> switchFilterMode());

        modePanel.add(lblMode);
        modePanel.add(cbFilterMode);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        inputPanel.setOpaque(false);

        quickMonthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        quickMonthPanel.setOpaque(false);
        quickMonthPanel.add(new JLabel("Tháng:"));
        cbQuickMonth = new JComboBox<>();
        cbQuickMonth.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        populateMonthComboBox(cbQuickMonth);
        quickMonthPanel.add(cbQuickMonth);

        dateRangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        dateRangePanel.setOpaque(false);
        dateRangePanel.add(new JLabel("Từ ngày:"));
        dcFrom = createDateChooser();
        dateRangePanel.add(dcFrom);
        dateRangePanel.add(new JLabel("Đến ngày:"));
        dcTo = createDateChooser();
        dateRangePanel.add(dcTo);

        inputPanel.add(quickMonthPanel);
        inputPanel.add(dateRangePanel);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionPanel.setOpaque(false);

        JButton btnApply = new JButton("Áp dụng");
        btnApply.putClientProperty(FlatClientProperties.STYLE, "background: #3b82f6; foreground: #fff; arc: 8");
        btnApply.addActionListener(e -> applyFilter());

        JButton btnViewAll = new JButton("Xem tất cả");
        btnViewAll.putClientProperty(FlatClientProperties.STYLE, "background: #6b7280; foreground: #fff; arc: 8");
        btnViewAll.addActionListener(e -> viewAllSalaries());

        actionPanel.add(btnApply);
        actionPanel.add(btnViewAll);

        filterPanel.add(modePanel, BorderLayout.NORTH);
        filterPanel.add(inputPanel, BorderLayout.CENTER);
        filterPanel.add(actionPanel, BorderLayout.SOUTH);

        switchFilterMode();
        return filterPanel;
    }

    private JDateChooser createDateChooser() {
        JDateChooser chooser = new JDateChooser();
        chooser.setDateFormatString("dd/MM/yyyy");
        chooser.setPreferredSize(new java.awt.Dimension(130, 30));
        return chooser;
    }

    private void populateMonthComboBox(JComboBox<YearMonth> comboBox) {
        DefaultComboBoxModel<YearMonth> model = new DefaultComboBoxModel<>();
        YearMonth currentMonth = YearMonth.now();

        for (int i = 0; i < 24; i++) {
            model.addElement(currentMonth.minusMonths(i));
        }

        comboBox.setModel(model);
        comboBox.setRenderer(new DefaultListCellRenderer() {
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
        comboBox.setSelectedItem(currentMonth);
    }

    private void switchFilterMode() {
        String selected = (String) cbFilterMode.getSelectedItem();
        if ("Lọc nhanh theo tháng".equals(selected)) {
            filterMode = FilterMode.QUICK_MONTH;
        } else if ("Lọc theo ngày (dd/MM/yyyy)".equals(selected)) {
            filterMode = FilterMode.DATE_RANGE;
        } else {
            filterMode = FilterMode.ALL;
        }

        quickMonthPanel.setVisible(filterMode == FilterMode.QUICK_MONTH);
        dateRangePanel.setVisible(filterMode == FilterMode.DATE_RANGE);
        revalidate();
        repaint();
    }

    private void applyFilter() {
        switchFilterMode();

        if (filterMode == FilterMode.QUICK_MONTH) {
            selectedMonth = (YearMonth) cbQuickMonth.getSelectedItem();
            filterFromMonth = null;
            filterToMonth = null;
            if (selectedMonth == null) {
                JOptionPane.showMessageDialog(this, 
                    "Vui lòng chọn tháng cần xem.", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else if (filterMode == FilterMode.DATE_RANGE) {
            Date fromDate = dcFrom.getDate();
            Date toDate = dcTo.getDate();

            if (fromDate == null || toDate == null) {
                JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn đủ từ ngày và đến ngày.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalDate fromLocalDate = fromDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate toLocalDate = toDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            if (fromLocalDate.isAfter(toLocalDate)) {
                JOptionPane.showMessageDialog(this,
                    "Từ ngày phải nhỏ hơn hoặc bằng đến ngày.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            selectedMonth = null;
            filterFromMonth = YearMonth.from(fromLocalDate);
            filterToMonth = YearMonth.from(toLocalDate);
        } else {
            selectedMonth = null;
            filterFromMonth = null;
            filterToMonth = null;
        }

        updateStatistics();
        if (onMonthChanged != null) {
            onMonthChanged.run();
        }
    }

    private void viewAllSalaries() {
        cbFilterMode.setSelectedItem("Xem tất cả");
        switchFilterMode();
        selectedMonth = null;
        filterFromMonth = null;
        filterToMonth = null;
        updateStatistics();
        if (onMonthChanged != null) {
            onMonthChanged.run();
        }
    }

    private void updateStatistics() {
        List<SalaryDTO> salaries;

        if (filterMode == FilterMode.QUICK_MONTH && selectedMonth != null) {
            salaries = salaryService.getSalariesByMonthYear(selectedMonth.getMonthValue(), selectedMonth.getYear());
        } else if (filterMode == FilterMode.DATE_RANGE && filterFromMonth != null && filterToMonth != null) {
            salaries = salaryService.getSalariesByDateRange(
                filterFromMonth.getMonthValue(), filterFromMonth.getYear(),
                filterToMonth.getMonthValue(), filterToMonth.getYear()
            );
        } else {
            salaries = salaryService.getAllSalaries();
        }

        BigDecimal totalSalary = BigDecimal.ZERO;
        int employeeCount = salaries.size();

        for (SalaryDTO salary : salaries) {
            if (salary.thucLinh != null) {
                totalSalary = totalSalary.add(salary.thucLinh);
            }
        }

        BigDecimal averageSalary = employeeCount > 0 ? 
            totalSalary.divide(new BigDecimal(employeeCount), java.math.RoundingMode.HALF_UP) : 
            BigDecimal.ZERO;

        lblTotalSalary.setText(df.format(totalSalary.doubleValue()) + " đ");
        lblEmployeeCount.setText(String.valueOf(employeeCount));
        lblAverageSalary.setText(df.format(averageSalary.doubleValue()) + " đ");
    }

    public FilterMode getFilterMode() {
        return filterMode;
    }

    public YearMonth getSelectedMonth() {
        return selectedMonth;
    }

    public YearMonth getFilterFromMonth() {
        return filterFromMonth;
    }

    public YearMonth getFilterToMonth() {
        return filterToMonth;
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
}