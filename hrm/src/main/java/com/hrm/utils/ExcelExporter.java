package com.hrm.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Utility class for exporting data to Excel files
 * 
 * HƯỚNG DẪN SỬ DỤNG:
 * ==================
 * Lớp này cung cấp các phương thức để xuất dữ liệu sang tệp Excel (.xlsx)
 * với các tính năng như: định dạng header, tự động điều chỉnh kích thước cột, v.v.
 * 
 * CÁC PHƯƠNG THỨC CHÍNH:
 * 
 * 1. exportToExcelWithDialog() - Xuất Excel với hộp thoại chọn file
 *    Ưu điểm: Cho phép người dùng chọn nơi lưu file
 *    
 *    Cách sử dụng:
 *    String[] headers = {"Mã NV", "Tên", "Phòng", "Lương"};
 *    List<Object[]> data = new ArrayList<>();
 *    data.add(new Object[]{"NV001", "Nguyễn Văn A", "IT", 10000000});
 *    data.add(new Object[]{"NV002", "Trần Thị B", "HR", 8000000});
 *    ExcelExporter.exportToExcelWithDialog(headers, data, parentComponent, "nhanvien");
 * 
 * 2. exportToExcel() - Xuất Excel với đường dẫn cụ thể
 *    Ưu điểm: Có thể xuất file trực tiếp mà không cần hộp thoại
 *    
 *    Cách sử dụng:
 *    String filePath = "C:/output/nhanvien.xlsx";
 *    ExcelExporter.exportToExcel(headers, data, filePath);
 * 
 * LƯU Ý:
 * - Dữ liệu được truyền vào dạng List<Object[]>, mỗi hàng là một mảng Object
 * - Các kiểu dữ liệu được hỗ trợ: String, Number, Boolean, Date
 * - Header sẽ được định dạng với nền xanh, chữ trắng, in đậm
 * - Các cột sẽ tự động điều chỉnh kích thước theo nội dung
 */
public class ExcelExporter {

    /**
     * Export data to Excel file with file chooser dialog
     * @param headers Column headers
     * @param data List of data rows (each row should be Object[])
     * @param parentComponent Parent component for dialog
     * @param defaultFileName Default file name (without extension)
     * @return true if export successful, false otherwise
     */
    public static boolean exportToExcelWithDialog(String[] headers, List<Object[]> data, 
                                                   JComponent parentComponent, String defaultFileName) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
        fileChooser.setSelectedFile(new java.io.File(defaultFileName + "_" + 
            new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xlsx"));
        
        int result = fileChooser.showSaveDialog(parentComponent);
        if (result == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (exportToExcel(headers, data, filePath)) {
                JOptionPane.showMessageDialog(parentComponent, 
                    "Xuất file Excel thành công!\n" + filePath,
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
        }
        return false;
    }

    /**
     * Export data to Excel file
     * @param headers Column headers
     * @param data List of data rows (each row should be Object[])
     * @param filePath Path to save the Excel file
     * @return true if export successful, false otherwise
     */
    public static boolean exportToExcel(String[] headers, List<Object[]> data, String filePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Data");

            // Create header row
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = createHeaderStyle(workbook);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Create data rows
            CellStyle dataStyle = createDataStyle(workbook);
            int rowNum = 1;
            for (Object[] rowData : data) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < rowData.length; i++) {
                    Cell cell = row.createCell(i);
                    setCellValue(cell, rowData[i]);
                    cell.setCellStyle(dataStyle);
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }

            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi xuất file Excel: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Set cell value based on object type
     */
    private static void setCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue(value.toString());
        }
    }

    /**
     * Create header cell style
     */
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }

    /**
     * Create data cell style
     */
    private static CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }
}
