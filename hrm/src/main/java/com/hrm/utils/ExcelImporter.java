package com.hrm.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Utility class for importing data from Excel files
 * Support for flexible column mapping
 * 
 * HƯỚNG DẪN SỬ DỤNG:
 * ==================
 * Lớp này cung cấp các phương thức để nhập dữ liệu từ tệp Excel (.xlsx)
 * với các tính năng: kiểm tra header, ánh xạ cột linh hoạt, xử lý các kiểu dữ liệu khác nhau
 * 
 * CÁC PHƯƠNG THỨC CHÍNH:
 * 
 * 1. importFromExcelWithDialog() - Nhập Excel với hộp thoại chọn file
 *    Ưu điểm: Cho phép người dùng chọn file, tự động kiểm tra header
 *    
 *    Cách sử dụng:
 *    String[] expectedHeaders = {"Mã NV", "Tên", "Phòng", "Lương"};
 *    Map<String, Object> result = ExcelImporter.importFromExcelWithDialog(expectedHeaders, parentComponent);
 *    
 *    if (result != null) {
 *        List<Object[]> data = (List<Object[]>) result.get("data");
 *        // Xử lý dữ liệu
 *    }
 * 
 * 2. importFromExcel() - Nhập Excel với đường dẫn cụ thể và kiểm tra header
 *    Ưu điểm: Có thể nhập file trực tiếp, kiểm tra dữ liệu chính xác
 *    
 *    Cách sử dụng:
 *    String filePath = "C:/input/nhanvien.xlsx";
 *    String[] expectedHeaders = {"Mã NV", "Tên", "Phòng", "Lương"};
 *    Map<String, Object> result = ExcelImporter.importFromExcel(filePath, expectedHeaders, true);
 *    
 *    Tham số thứ 3 (true): Bỏ qua hàng đầu tiên (coi đó là header)
 * 
 * 3. importFromExcel(String filePath) - Nhập đơn giản mà không kiểm tra header
 *    Ưu điểm: Nhanh, áp dụng cho file có định dạng đã biết
 *    
 *    Cách sử dụng:
 *    List<Object[]> data = ExcelImporter.importFromExcel("C:/input/nhanvien.xlsx");
 * 
 * 4. getExcelHeaders() - Lấy danh sách header từ file Excel
 *    Ưu điểm: Kiểm tra định dạng file trước khi nhập
 *    
 *    Cách sử dụng:
 *    String[] headers = ExcelImporter.getExcelHeaders("C:/input/nhanvien.xlsx");
 * 
 * 5. getRowCount() - Lấy số dòng trong tệp Excel
 *    Ưu điểm: Kiểm tra số lượng dữ liệu trong file
 *    
 *    Cách sử dụng:
 *    int rowCount = ExcelImporter.getRowCount("C:/input/nhanvien.xlsx");
 * 
 * LƯU Ý:
 * - Dữ liệu được trả về dạng List<Object[]>, mỗi hàng là một mảng Object
 * - Các kiểu dữ liệu được hỗ trợ: String, Number, Boolean, Date
 * - Header phải khớp với expectedHeaders (tên cột có thể ở vị trí khác nhau)
 * - Kiểm tra header không phân biệt chữ hoa/thường
 * - Nếu file không có dữ liệu hoặc lỗi, sẽ hiển thị thông báo lỗi
 */
public class ExcelImporter {

    /**
     * Import data from Excel file with file chooser dialog
     * Validate headers against expected columns
     * @param expectedHeaders Expected column headers
     * @param parentComponent Parent component for dialog
     * @return Map containing headers and data, or null if cancelled/error
     */
    public static Map<String, Object> importFromExcelWithDialog(String[] expectedHeaders, JComponent parentComponent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
        
        int result = fileChooser.showOpenDialog(parentComponent);
        if (result != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        
        String filePath = fileChooser.getSelectedFile().getAbsolutePath();
        return importFromExcel(filePath, expectedHeaders, true);
    }

    /**
     * Import data from Excel file with header validation
     * @param filePath Path to Excel file
     * @param expectedHeaders Expected column headers
     * @param skipFirstRow Skip first row as header
     * @return Map with "headers" (String[]) and "data" (List<Object[]>), 
     *         or null if headers don't match
     */
    public static Map<String, Object> importFromExcel(String filePath, String[] expectedHeaders, boolean skipFirstRow) {
        try (FileInputStream fileIn = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fileIn)) {

            Sheet sheet = workbook.getSheetAt(0);
            
            // Get headers from file
            String[] fileHeaders = getExcelHeaders(filePath);
            
            // Validate headers
            if (!validateHeaders(fileHeaders, expectedHeaders)) {
                String msg = "Các trường dữ liệu trong file không khớp.\n\n" +
                        "Trường yêu cầu: " + Arrays.toString(expectedHeaders) + "\n" +
                        "Trường trong file: " + Arrays.toString(fileHeaders);
                JOptionPane.showMessageDialog(null, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            
            // Create mapping from file columns to expected columns
            Map<Integer, Integer> columnMapping = createColumnMapping(fileHeaders, expectedHeaders);
            
            // Import data
            List<Object[]> data = new ArrayList<>();
            int startRow = skipFirstRow ? 1 : 0;
            
            for (int rowIdx = startRow; rowIdx <= sheet.getLastRowNum(); rowIdx++) {
                Row row = sheet.getRow(rowIdx);
                if (row == null) {
                    continue;
                }
                
                Object[] rowData = new Object[expectedHeaders.length];
                Arrays.fill(rowData, "");
                
                // Map data from file columns to expected columns
                for (int fileColIdx = 0; fileColIdx < fileHeaders.length; fileColIdx++) {
                    Integer expectedColIdx = columnMapping.get(fileColIdx);
                    if (expectedColIdx != null) {
                        Cell cell = row.getCell(fileColIdx);
                        rowData[expectedColIdx] = getCellValue(cell);
                    }
                }
                
                data.add(rowData);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("headers", expectedHeaders);
            result.put("data", data);
            return result;
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi nhập file Excel: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Validate if file headers match expected headers (order-independent)
     */
    private static boolean validateHeaders(String[] fileHeaders, String[] expectedHeaders) {
        if (fileHeaders.length != expectedHeaders.length) {
            return false;
        }
        
        Set<String> fileHeaderSet = new HashSet<>();
        for (String header : fileHeaders) {
            fileHeaderSet.add(header.trim());
        }
        
        for (String expected : expectedHeaders) {
            if (!fileHeaderSet.contains(expected.trim())) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Create mapping from file column indices to expected column indices
     */
    private static Map<Integer, Integer> createColumnMapping(String[] fileHeaders, String[] expectedHeaders) {
        Map<Integer, Integer> mapping = new HashMap<>();
        
        for (int fileColIdx = 0; fileColIdx < fileHeaders.length; fileColIdx++) {
            String fileHeader = fileHeaders[fileColIdx].trim();
            
            for (int expectedColIdx = 0; expectedColIdx < expectedHeaders.length; expectedColIdx++) {
                if (fileHeader.equalsIgnoreCase(expectedHeaders[expectedColIdx].trim())) {
                    mapping.put(fileColIdx, expectedColIdx);
                    break;
                }
            }
        }
        
        return mapping;
    }

    /**
     * Import data from Excel file (default: sheet 0, skip header)
     */
    public static List<Object[]> importFromExcel(String filePath) {
        List<Object[]> data = new ArrayList<>();

        try (FileInputStream fileIn = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fileIn)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int rowIdx = 1; rowIdx <= sheet.getLastRowNum(); rowIdx++) {
                Row row = sheet.getRow(rowIdx);
                if (row == null) {
                    continue;
                }

                Object[] rowData = new Object[row.getLastCellNum()];
                for (int cellIdx = 0; cellIdx < row.getLastCellNum(); cellIdx++) {
                    Cell cell = row.getCell(cellIdx);
                    rowData[cellIdx] = getCellValue(cell);
                }
                data.add(rowData);
            }

            return data;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi nhập file Excel: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Get headers from Excel file
     */
    public static String[] getExcelHeaders(String filePath) {
        try (FileInputStream fileIn = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fileIn)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            if (headerRow == null) {
                return new String[0];
            }

            String[] headers = new String[headerRow.getLastCellNum()];
            for (int cellIdx = 0; cellIdx < headerRow.getLastCellNum(); cellIdx++) {
                Cell cell = headerRow.getCell(cellIdx);
                headers[cellIdx] = getCellValueAsString(cell);
            }

            return headers;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi đọc headers: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return new String[0];
        }
    }

    /**
     * Get number of rows in Excel sheet
     */
    public static int getRowCount(String filePath) {
        try (FileInputStream fileIn = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fileIn)) {

            Sheet sheet = workbook.getSheetAt(0);
            return sheet.getLastRowNum() + 1;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi đếm dòng: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Get cell value as Object
     */
    private static Object getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    return cell.getNumericCellValue();
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }

    /**
     * Get cell value as String
     */
    private static String getCellValueAsString(Cell cell) {
        Object value = getCellValue(cell);
        return value != null ? value.toString() : "";
    }
}
