package com.hrm.utils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class to extract data from JTable and prepare it for export/import
 */
public class ExcelDataManager {

    /**
     * Extract data from JTable
     * @param table JTable to extract from
     * @param excludeColumns Column indices to exclude (e.g., action column)
     * @return Object[] containing headers and data
     */
    public static Object[] extractTableData(JTable table, int... excludeColumns) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int columnCount = model.getColumnCount();
        
        // Get headers
        List<String> headerList = new ArrayList<>();
        List<Integer> columnIndices = new ArrayList<>();
        
        for (int i = 0; i < columnCount; i++) {
            boolean isExcluded = false;
            for (int excludeCol : excludeColumns) {
                if (i == excludeCol) {
                    isExcluded = true;
                    break;
                }
            }
            if (!isExcluded) {
                headerList.add(model.getColumnName(i));
                columnIndices.add(i);
            }
        }
        
        String[] headers = headerList.toArray(new String[0]);
        
        // Get data
        List<Object[]> data = new ArrayList<>();
        for (int row = 0; row < model.getRowCount(); row++) {
            Object[] rowData = new Object[columnIndices.size()];
            for (int i = 0; i < columnIndices.size(); i++) {
                rowData[i] = model.getValueAt(row, columnIndices.get(i));
            }
            data.add(rowData);
        }
        
        return new Object[]{headers, data};
    }

    /**
     * Load imported data into JTable
     * @param table JTable to load data into
     * @param importResult Result from ExcelImporter.importFromExcel()
     */
    @SuppressWarnings("unchecked")
    public static void loadImportedDataToTable(JTable table, Map<String, Object> importResult) {
        if (importResult == null) {
            return;
        }
        
        List<Object[]> data = (List<Object[]>) importResult.get("data");
        
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        
        // Clear existing data
        model.setRowCount(0);
        
        // Add imported data
        for (Object[] rowData : data) {
            model.addRow(rowData);
        }
    }

    /**
     * Show export success dialog and open file location
     */
    public static void showExportSuccess(String filePath) {
        JOptionPane.showMessageDialog(null, 
            "Xuất file Excel thành công!\n\nĐường dẫn: " + filePath,
            "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Show import success dialog
     */
    public static void showImportSuccess(int rowCount) {
        JOptionPane.showMessageDialog(null, 
            "Nhập dữ liệu thành công!\n\nSố dòng nhập: " + rowCount,
            "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }
}
