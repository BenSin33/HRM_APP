package com.hrm.utils;

import javax.swing.*;
import java.util.List;

/**
 * Helper class for Account Manager Export/Import operations
 */
public class AccountExcelHelper {

    public static final String[] ACCOUNT_HEADERS = {
        "Mã NV", "Họ tên", "Email", "Điện thoại", "Vai trò", "Phòng ban", "Trạng thái"
    };

    public static void handleAccountExport(JTable accountTable, JComponent parentComponent) {
        Object[] tableData = ExcelDataManager.extractTableData(accountTable, 7); // Exclude action column
        
        String[] headers = (String[]) tableData[0];
        @SuppressWarnings("unchecked")
        List<Object[]> data = (List<Object[]>) tableData[1];
        
        if (data.isEmpty()) {
            JOptionPane.showMessageDialog(parentComponent, 
                "Không có dữ liệu để xuất!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        ExcelExporter.exportToExcelWithDialog(headers, data, parentComponent, "TaiKhoan");
    }

    public static void handleAccountImport(JTable accountTable, JComponent parentComponent) {
        var result = ExcelImporter.importFromExcelWithDialog(ACCOUNT_HEADERS, parentComponent);
        
        if (result != null) {
            ExcelDataManager.loadImportedDataToTable(accountTable, result);
            ExcelDataManager.showImportSuccess(accountTable.getRowCount());
        }
    }
}
