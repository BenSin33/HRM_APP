package com.hrm.utils;

import javax.swing.*;
import java.util.List;

/**
 * Helper class for Category (Allowance, Deduction, etc.) Export/Import operations
 */
public class CategoryExcelHelper {

    public static final String[] ALLOWANCE_HEADERS = {
        "Mã phụ cấp", "Tên phụ cấp", "Số tiền mặc định"
    };

    public static final String[] DEDUCTION_HEADERS = {
        "Mã khấu trừ", "Tên khấu trừ", "Số tiền mặc định"
    };

    public static final String[] DEPARTMENT_HEADERS = {
        "Mã phòng ban", "Tên phòng ban", "Mô tả"
    };

    public static final String[] POSITION_HEADERS = {
        "Mã vị trí", "Tên vị trí", "Mô tả"
    };

    public static final String[] TRINHDO_HEADERS = {
        "Mã trình độ", "Tên trình độ"
    };

    public static void handleCategoryExport(JTable table, JComponent parentComponent, String categoryName) {
        Object[] tableData = ExcelDataManager.extractTableData(table, table.getColumnCount() - 1); // Exclude last action column
        
        String[] headers = (String[]) tableData[0];
        @SuppressWarnings("unchecked")
        List<Object[]> data = (List<Object[]>) tableData[1];
        
        if (data.isEmpty()) {
            JOptionPane.showMessageDialog(parentComponent, 
                "Không có dữ liệu để xuất!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        ExcelExporter.exportToExcelWithDialog(headers, data, parentComponent, categoryName);
    }

    public static void handleCategoryImport(JTable table, JComponent parentComponent, String[] expectedHeaders) {
        var result = ExcelImporter.importFromExcelWithDialog(expectedHeaders, parentComponent);
        
        if (result != null) {
            ExcelDataManager.loadImportedDataToTable(table, result);
            ExcelDataManager.showImportSuccess(table.getRowCount());
        }
    }
}
