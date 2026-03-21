package com.hrm.UI.HR.SalaryTab;

import com.formdev.flatlaf.FlatClientProperties;
import com.hrm.Service.SalaryService;
import com.hrm.utils.SalaryExcelHelper;

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
        
        // Thêm listeners cho nút export/import/refresh/payment
        header.getExportButton().addActionListener(e -> handleExportSalary());
        header.getImportButton().addActionListener(e -> handleImportSalary());
        header.getRefreshButton().addActionListener(e -> handleRefreshData());
        header.getPaymentButton().addActionListener(e -> handlePaymentSalary());
        
        this.add(header, BorderLayout.NORTH);

        // 4. Nội dung chính
        JPanel content = new JPanel(new BorderLayout(0, 20));
        content.setOpaque(false);
        content.add(summary, BorderLayout.NORTH);
        content.add(salaryTable, BorderLayout.CENTER);

        this.add(content, BorderLayout.CENTER);
    }

    private void onMonthChanged() {
        if (summary.getFilterMode() == SalarySummary.FilterMode.QUICK_MONTH && summary.getSelectedMonth() != null) {
            YearMonth selectedMonth = summary.getSelectedMonth();
            salaryTable.loadSalaryDataByMonth(selectedMonth.getMonthValue(), selectedMonth.getYear());
            return;
        }

        YearMonth fromMonth = summary.getFilterFromMonth();
        YearMonth toMonth = summary.getFilterToMonth();
        if (summary.getFilterMode() == SalarySummary.FilterMode.DATE_RANGE && fromMonth != null && toMonth != null) {
            salaryTable.loadSalaryDataByDateRange(fromMonth, toMonth);
        } else {
            salaryTable.loadAllSalaryData();
        }
    }

    private YearMonth getFromMonthForAction() {
        if (summary.getFilterMode() == SalarySummary.FilterMode.QUICK_MONTH && summary.getSelectedMonth() != null) {
            return summary.getSelectedMonth();
        }
        if (summary.getFilterMode() == SalarySummary.FilterMode.DATE_RANGE) {
            return summary.getFilterFromMonth();
        }
        return null;
    }

    private YearMonth getToMonthForAction() {
        if (summary.getFilterMode() == SalarySummary.FilterMode.QUICK_MONTH && summary.getSelectedMonth() != null) {
            return summary.getSelectedMonth();
        }
        if (summary.getFilterMode() == SalarySummary.FilterMode.DATE_RANGE) {
            return summary.getFilterToMonth();
        }
        return null;
    }

    private void handleRefreshData() {
        salaryTable.refreshData();
        JOptionPane.showMessageDialog(this, "Dữ liệu đã được làm mới!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleLockSalary() {
        YearMonth fromMonth = getFromMonthForAction();
        YearMonth toMonth = getToMonthForAction();
        
        if (fromMonth == null || toMonth == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bộ lọc tháng hoặc khoảng ngày trước khi khóa lương!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn khóa bảng lương từ " + fromMonth.getMonthValue() + "/" + fromMonth.getYear() + 
            " đến " + toMonth.getMonthValue() + "/" + toMonth.getYear() + "?",
            "Xác nhận khóa lương",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Khóa từng tháng trong khoảng
            boolean success = true;
            YearMonth current = fromMonth;
            while (!current.isAfter(toMonth)) {
                if (!salaryService.lockSalariesByMonth(current.getMonthValue(), current.getYear())) {
                    success = false;
                    break;
                }
                current = current.plusMonths(1);
            }
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Đã khóa bảng lương thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                salaryTable.refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi khóa bảng lương!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleUnlockSalary() {
        YearMonth fromMonth = getFromMonthForAction();
        YearMonth toMonth = getToMonthForAction();
        
        if (fromMonth == null || toMonth == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bộ lọc tháng hoặc khoảng ngày trước khi mở khóa lương!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn mở khóa bảng lương từ " + fromMonth.getMonthValue() + "/" + fromMonth.getYear() + 
            " đến " + toMonth.getMonthValue() + "/" + toMonth.getYear() + "?",
            "Xác nhận mở khóa",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Mở khóa từng tháng trong khoảng
            boolean success = true;
            YearMonth current = fromMonth;
            while (!current.isAfter(toMonth)) {
                if (!salaryService.unlockSalariesByMonth(current.getMonthValue(), current.getYear())) {
                    success = false;
                    break;
                }
                current = current.plusMonths(1);
            }
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Đã mở khóa bảng lương thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                salaryTable.refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi mở khóa bảng lương!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleCalculateSalary() {
        YearMonth fromMonth = getFromMonthForAction();
        YearMonth toMonth = getToMonthForAction();
        
        if (fromMonth == null || toMonth == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bộ lọc tháng hoặc khoảng ngày trước khi tính lương!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn tính lương từ " + fromMonth.getMonthValue() + "/" + fromMonth.getYear() + 
            " đến " + toMonth.getMonthValue() + "/" + toMonth.getYear() + "?\n\n" +
            "Công thức tính:\n" +
            "Thực lĩnh = (Lương cơ bản × Hệ số trình độ × (Số ngày công / 26)) + Tổng phụ cấp - Tổng khấu trừ",
            "Xác nhận tính lương",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = salaryService.calculateSalaryForMonthRange(
                    fromMonth.getMonthValue(), 
                    fromMonth.getYear(),
                    toMonth.getMonthValue(), 
                    toMonth.getYear()
                );
                
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Tính lương thành công từ " + fromMonth.getMonthValue() + "/" + fromMonth.getYear() + 
                        " đến " + toMonth.getMonthValue() + "/" + toMonth.getYear() + "!",
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                    salaryTable.refreshData();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Lỗi khi tính lương. Vui lòng kiểm tra dữ liệu chấm công.",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Lỗi khi tính lương: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleExportSalary() {
        SalaryExcelHelper.handleSalaryExport(salaryTable.getSalaryTable(), this);
    }

    private void handleImportSalary() {
        SalaryExcelHelper.handleSalaryImport(salaryTable.getSalaryTable(), this);
    }

    private void handlePaymentSalary() {
        YearMonth fromMonth = getFromMonthForAction();
        YearMonth toMonth = getToMonthForAction();
        
        if (fromMonth == null || toMonth == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bộ lọc tháng hoặc khoảng ngày trước khi thanh toán lương!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn cập nhật trạng thái thanh toán cho bảng lương từ " + fromMonth.getMonthValue() + "/" + fromMonth.getYear() + 
            " đến " + toMonth.getMonthValue() + "/" + toMonth.getYear() + "?",
            "Xác nhận thanh toán",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Cập nhật từng tháng trong khoảng
            boolean success = true;
            YearMonth current = fromMonth;
            while (!current.isAfter(toMonth)) {
                if (!salaryService.updatePaymentStatusByMonth(current.getMonthValue(), current.getYear())) {
                    success = false;
                    break;
                }
                current = current.plusMonths(1);
            }
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Đã cập nhật trạng thái thanh toán thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                salaryTable.refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật trạng thái thanh toán!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}