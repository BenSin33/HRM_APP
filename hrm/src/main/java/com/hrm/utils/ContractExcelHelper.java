package com.hrm.utils;

import javax.swing.*;
import java.util.List;

/**
 * Helper class for Contract Export/Import operations
 */
public class ContractExcelHelper {

    public static final String[] CONTRACT_HEADERS = {
        "MÃ HD", "MÃ NV", "TÊN NHÂN VIÊN", "PHÒNG BAN", "LOẠI HỢP ĐỒNG", "THỜI HẠN", "LƯƠNG", "TRẠNG THÁI"
    };

    public static void handleContractExport(JTable contractTable, JComponent parentComponent) {
        Object[] tableData = ExcelDataManager.extractTableData(contractTable, 8); // Exclude action column
        
        String[] headers = (String[]) tableData[0];
        @SuppressWarnings("unchecked")
        List<Object[]> data = (List<Object[]>) tableData[1];
        
        if (data.isEmpty()) {
            JOptionPane.showMessageDialog(parentComponent, 
                "Không có dữ liệu để xuất!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        ExcelExporter.exportToExcelWithDialog(headers, data, parentComponent, "HopDong");
    }

    public static void handleContractImport(JTable contractTable, JComponent parentComponent) {
        var result = ExcelImporter.importFromExcelWithDialog(CONTRACT_HEADERS, parentComponent);
        
        if (result != null) {
            ExcelDataManager.loadImportedDataToTable(contractTable, result);
            ExcelDataManager.showImportSuccess(contractTable.getRowCount());
        }
    }
}
