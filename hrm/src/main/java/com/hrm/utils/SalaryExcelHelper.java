package com.hrm.utils;

import javax.swing.*;
import java.util.List;

/**
 * Helper class for Salary Export/Import operations
 */
public class SalaryExcelHelper {

    public static final String[] SALARY_HEADERS = {
        "Mã lương", "Mã NV", "Họ tên", "Tháng", "Năm", 
        "Lương cơ bản", "Số ngày công", "Tổng phụ cấp", "Tổng khấu trừ", 
        "Ngày chốt", "Thực lĩnh", "Trạng thái", "Tình trạng TT"
    };

    public static void handleSalaryExport(JTable salaryTable, JComponent parentComponent) {
        Object[] tableData = ExcelDataManager.extractTableData(salaryTable, 13); // Exclude action column
        
        String[] headers = (String[]) tableData[0];
        @SuppressWarnings("unchecked")
        List<Object[]> data = (List<Object[]>) tableData[1];
        
        if (data.isEmpty()) {
            JOptionPane.showMessageDialog(parentComponent, 
                "Không có dữ liệu để xuất!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        ExcelExporter.exportToExcelWithDialog(headers, data, parentComponent, "Luong");
    }

    public static void handleSalaryImport(JTable salaryTable, JComponent parentComponent) {
        var result = ExcelImporter.importFromExcelWithDialog(SALARY_HEADERS, parentComponent);
        
        if (result != null) {
            ExcelDataManager.loadImportedDataToTable(salaryTable, result);
            ExcelDataManager.showImportSuccess(salaryTable.getRowCount());
        }
    }
}
