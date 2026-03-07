package com.hrm.UI.HR.SalaryTab;

import com.formdev.flatlaf.FlatClientProperties;
import com.hrm.Service.SalaryService;

import javax.swing.*;
import java.awt.*;
import java.time.YearMonth;

public class SalaryManagement extends JPanel {
    private SalaryHeader header;
    private SalarySummary summary;
    private SalaryTable salaryTable;
    private SalaryService salaryService;

    public SalaryManagement() {
        setLayout(new BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        putClientProperty(FlatClientProperties.STYLE, "background: #f8f9fa");

        salaryService = new SalaryService();

        // 1. Summary với callback để cập nhật bảng
        summary = new SalarySummary(() -> {
            onMonthChanged();
        });
        
        // 2. Khởi tạo bảng dữ liệu
        salaryTable = new SalaryTable();

        // 3. Header với callback cho các nút
        header = new SalaryHeader(
            e -> handleLockSalary(),
            e -> handleUnlockSalary(),
            e -> handleCalculateSalary()
        );
        this.add(header, BorderLayout.NORTH);

        // 4. Nội dung chính
        JPanel content = new JPanel(new BorderLayout(0, 20));
        content.setOpaque(false);
        content.add(summary, BorderLayout.NORTH);
        content.add(salaryTable, BorderLayout.CENTER);

        this.add(content, BorderLayout.CENTER);
    }

    private void onMonthChanged() {
        YearMonth selectedMonth = summary.getSelectedMonth();
        if (selectedMonth != null) {
            salaryTable.loadSalaryDataByMonth(selectedMonth.getMonthValue(), selectedMonth.getYear());
        } else {
            // Nếu chọn "Xem tất cả"
            salaryTable.loadAllSalaryData();
        }
    }

    private void handleLockSalary() {
        YearMonth selectedMonth = summary.getSelectedMonth();
        if (selectedMonth == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tháng!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn khóa bảng lương tháng " + selectedMonth.getMonthValue() + "/" + selectedMonth.getYear() + "?",
            "Xác nhận khóa lương",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (salaryService.lockSalariesByMonth(selectedMonth.getMonthValue(), selectedMonth.getYear())) {
                JOptionPane.showMessageDialog(this, "Đã khóa bảng lương thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                salaryTable.refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi khóa bảng lương!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleUnlockSalary() {
        YearMonth selectedMonth = summary.getSelectedMonth();
        if (selectedMonth == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tháng!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn mở khóa bảng lương tháng " + selectedMonth.getMonthValue() + "/" + selectedMonth.getYear() + "?",
            "Xác nhận mở khóa",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (salaryService.unlockSalariesByMonth(selectedMonth.getMonthValue(), selectedMonth.getYear())) {
                JOptionPane.showMessageDialog(this, "Đã mở khóa bảng lương thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                salaryTable.refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi mở khóa bảng lương!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleCalculateSalary() {
        YearMonth selectedMonth = summary.getSelectedMonth();
        if (selectedMonth == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tháng!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn tính lương cho tháng " + selectedMonth.getMonthValue() + "/" + selectedMonth.getYear() + "?",
            "Xác nhận tính lương",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // TODO: Gọi hàm tính lương từ Service
            JOptionPane.showMessageDialog(this, 
                "Đang tính lương cho tháng " + selectedMonth.getMonthValue() + "/" + selectedMonth.getYear() + "...",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
            salaryTable.refreshData();
        }
    }
}