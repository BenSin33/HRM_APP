package com.hrm.utils;

import com.hrm.DTO.HR.AttenDanceDTO.EmployeeRowDTO;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * AttendanceExcelExporter – Xuất danh sách chấm công ra file Excel (.xlsx).
 *
 * Sử dụng Apache POI nếu có trong classpath.
 * Fallback về CSV (mở được bằng Excel) nếu không có POI.
 *
 * Cách dùng:
 *   AttendanceExcelExporter.export(parentComponent, data, month, year);
 *
 * Thêm POI vào pom.xml:
 *   <dependency>
 *     <groupId>org.apache.poi</groupId>
 *     <artifactId>poi-ooxml</artifactId>
 *     <version>5.2.5</version>
 *   </dependency>
 */
public class AttendanceExcelExporter {

    // ─── Màu header (Apache POI IndexedColors) ───────────────────
    // Purple = 0x7C3AED → dùng custom color trong POI

    private static final String[] COLUMNS = {
        "STT", "Mã NV", "Họ tên", "Phòng ban", "Chức vụ",
        "Ngày công", "Đi muộn", "Nghỉ phép", "Vắng mặt"
    };

    /**
     * Xuất dữ liệu ra file Excel, mở hộp thoại chọn nơi lưu.
     *
     * @param parent  component cha để hiển thị dialog (có thể null)
     * @param data    danh sách dữ liệu đang hiển thị trên bảng (đã qua filter)
     * @param month   tháng đang xem
     * @param year    năm đang xem
     */
    public static void export(Component parent, List<EmployeeRowDTO> data, int month, int year) {
        if (data == null || data.isEmpty()) {
            JOptionPane.showMessageDialog(parent,
                    "Không có dữ liệu để xuất.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Mở file chooser
        JFileChooser chooser = new JFileChooser();
        String defaultName = String.format("ChamCong_T%02d_%d_%s.xlsx",
                month, year,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")));
        chooser.setSelectedFile(new File(defaultName));
        chooser.setDialogTitle("Lưu file Excel chấm công");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Excel Files (*.xlsx, *.csv)", "xlsx", "csv"));

        int result = chooser.showSaveDialog(parent);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        // Đảm bảo có extension
        if (!file.getName().toLowerCase().endsWith(".xlsx")
                && !file.getName().toLowerCase().endsWith(".csv")) {
            file = new File(file.getAbsolutePath() + ".xlsx");
        }

        // Thử xuất bằng Apache POI; fallback CSV
        final File finalFile = file;
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    exportWithPoi(finalFile, data, month, year);
                    return true;
                } catch (ClassNotFoundException e) {
                    // POI không có trong classpath → xuất CSV
                    File csvFile = new File(
                            finalFile.getAbsolutePath().replace(".xlsx", ".csv"));
                    exportAsCsv(csvFile, data, month, year);
                    return true;
                }
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        int open = JOptionPane.showConfirmDialog(parent,
                                "Xuất file thành công!\n" + finalFile.getAbsolutePath()
                                + "\n\nMở file ngay?",
                                "Thành công", JOptionPane.YES_NO_OPTION,
                                JOptionPane.INFORMATION_MESSAGE);
                        if (open == JOptionPane.YES_OPTION && Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().open(finalFile);
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(parent,
                            "Xuất file thất bại: " + ex.getMessage(),
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    // =============================================================
    // APACHE POI EXPORT (xlsx)
    // =============================================================
    private static void exportWithPoi(File file, List<EmployeeRowDTO> data,
                                       int month, int year)
            throws Exception {

        // Dynamic class loading để không bị lỗi compile nếu thiếu POI
        Class<?> wbClass   = Class.forName("org.apache.poi.xssf.usermodel.XSSFWorkbook");
        Object   workbook  = wbClass.getDeclaredConstructor().newInstance();

        // Tạo sheet
        Object sheet = wbClass.getMethod("createSheet", String.class)
                .invoke(workbook, String.format("Tháng %d-%d", month, year));

        Class<?> sheetClass = sheet.getClass();

        // Helper để tạo hàng
        java.lang.reflect.Method createRow = sheetClass.getMethod("createRow", int.class);

        // Helper để tạo style
        Class<?> csClass    = Class.forName("org.apache.poi.ss.usermodel.CellStyle");
        Object   headerStyle = createHeaderStyle(workbook, wbClass);
        Object   dataStyle   = createDataStyle(workbook, wbClass);
        Object   numberStyle = createNumberStyle(workbook, wbClass);
        Object   titleStyle  = createTitleStyle(workbook, wbClass);

        // ── Dòng 1: Tiêu đề báo cáo ──────────────────────────────
        Object titleRow = createRow.invoke(sheet, 0);
        Object titleCell = createCell(titleRow, 0);
        setCellValue(titleCell, String.format("BÁO CÁO CHẤM CÔNG THÁNG %d NĂM %d", month, year));
        setCellStyle(titleCell, titleStyle);

        // Merge A1:I1
        Class<?> crClass = Class.forName("org.apache.poi.ss.util.CellRangeAddress");
        Object   region  = crClass.getDeclaredConstructor(int.class,int.class,int.class,int.class)
                .newInstance(0, 0, 0, COLUMNS.length - 1);
        sheetClass.getMethod("addMergedRegion", crClass).invoke(sheet, region);

        // ── Dòng 2: Thời gian xuất ──────────────────────────────
        Object subRow  = createRow.invoke(sheet, 1);
        Object subCell = createCell(subRow, 0);
        setCellValue(subCell, "Xuất lúc: "
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        Object subStyle = createDataStyle(workbook, wbClass);

        // ── Dòng 3: Trống ────────────────────────────────────────

        // ── Dòng 4: Header bảng ──────────────────────────────────
        Object headerRow = createRow.invoke(sheet, 3);
        setRowHeight(headerRow, 22);
        for (int i = 0; i < COLUMNS.length; i++) {
            Object cell = createCell(headerRow, i);
            setCellValue(cell, COLUMNS[i]);
            setCellStyle(cell, headerStyle);
        }

        // ── Dòng 5+: Dữ liệu ─────────────────────────────────────
        for (int r = 0; r < data.size(); r++) {
            EmployeeRowDTO dto = data.get(r);
            Object dataRow = createRow.invoke(sheet, r + 4);
            setRowHeight(dataRow, 18);

            Object[] vals = {
                r + 1,
                dto.manv,
                dto.hoTen,
                dto.phongBan != null ? dto.phongBan : "",
                dto.chucVu   != null ? dto.chucVu   : "",
                dto.workDays,
                dto.lateDays,
                dto.leaveDays,
                dto.absentDays
            };

            for (int c = 0; c < vals.length; c++) {
                Object cell = createCell(dataRow, c);
                if (vals[c] instanceof Integer) {
                    setCellValueInt(cell, (Integer) vals[c]);
                    setCellStyle(cell, numberStyle);
                } else {
                    setCellValue(cell, vals[c].toString());
                    setCellStyle(cell, dataStyle);
                }
            }
        }

        // ── Dòng tổng kết ─────────────────────────────────────────
        int sumRow = data.size() + 4;
        Object totalRow = createRow.invoke(sheet, sumRow);
        Object lblCell  = createCell(totalRow, 2);
        setCellValue(lblCell, "TỔNG CỘNG");
        setCellStyle(lblCell, headerStyle);

        // Tổng công = SUM cột F
        int dataStart = 5; // row 5 (1-indexed)
        int dataEnd   = data.size() + 4;
        for (int c = 5; c <= 8; c++) {
            Object cell = createCell(totalRow, c);
            char   col  = (char)('A' + c);
            setCellFormula(cell, String.format("SUM(%c%d:%c%d)", col, dataStart, col, dataEnd));
            setCellStyle(cell, headerStyle);
        }

        // Auto-width cột
        int[] colWidths = {6, 10, 22, 20, 18, 10, 10, 10, 12};
        for (int i = 0; i < colWidths.length; i++) {
            setColumnWidth(sheet, sheetClass, i, colWidths[i]);
        }

        // Lưu file
        try (FileOutputStream fos = new FileOutputStream(file)) {
            wbClass.getMethod("write", OutputStream.class).invoke(workbook, fos);
        }
        wbClass.getMethod("close").invoke(workbook);
    }

    // =============================================================
    // FALLBACK: CSV EXPORT (không cần POI)
    // =============================================================
    private static void exportAsCsv(File file, List<EmployeeRowDTO> data,
                                     int month, int year) throws IOException {
        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            // BOM cho Excel nhận đúng UTF-8
            pw.print('\uFEFF');
            pw.printf("BÁO CÁO CHẤM CÔNG THÁNG %d NĂM %d%n", month, year);
            pw.printf("Xuất lúc: %s%n%n",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            pw.println("STT,Mã NV,Họ tên,Phòng ban,Chức vụ,Ngày công,Đi muộn,Nghỉ phép,Vắng mặt");

            for (int i = 0; i < data.size(); i++) {
                EmployeeRowDTO d = data.get(i);
                pw.printf("%d,%s,\"%s\",\"%s\",\"%s\",%d,%d,%d,%d%n",
                        i + 1, d.manv, d.hoTen,
                        d.phongBan != null ? d.phongBan : "",
                        d.chucVu   != null ? d.chucVu   : "",
                        d.workDays, d.lateDays, d.leaveDays, d.absentDays);
            }

            // Tổng
            int sumWork = data.stream().mapToInt(d -> d.workDays).sum();
            int sumLate = data.stream().mapToInt(d -> d.lateDays).sum();
            int sumLeave= data.stream().mapToInt(d -> d.leaveDays).sum();
            int sumAbs  = data.stream().mapToInt(d -> d.absentDays).sum();
            pw.printf(",,TỔNG CỘNG,,,,%d,%d,%d,%d%n", sumWork, sumLate, sumLeave, sumAbs);
        }
    }

    // =============================================================
    // POI REFLECTION HELPERS
    // =============================================================

    private static Object createCell(Object row, int col) throws Exception {
        return row.getClass().getMethod("createCell", int.class).invoke(row, col);
    }

    private static void setCellValue(Object cell, String val) throws Exception {
        cell.getClass().getMethod("setCellValue", String.class).invoke(cell, val);
    }

    private static void setCellValueInt(Object cell, int val) throws Exception {
        cell.getClass().getMethod("setCellValue", double.class).invoke(cell, (double) val);
    }

    private static void setCellFormula(Object cell, String formula) throws Exception {
        cell.getClass().getMethod("setCellFormula", String.class).invoke(cell, formula);
    }

    private static void setCellStyle(Object cell, Object style) throws Exception {
        Class<?> csIface = Class.forName("org.apache.poi.ss.usermodel.CellStyle");
        cell.getClass().getMethod("setCellStyle", csIface).invoke(cell, style);
    }

    private static void setRowHeight(Object row, int points) throws Exception {
        short height = (short)(points * 20);
        row.getClass().getMethod("setHeight", short.class).invoke(row, height);
    }

    private static void setColumnWidth(Object sheet, Class<?> sheetClass, int col, int chars) throws Exception {
        sheetClass.getMethod("setColumnWidth", int.class, int.class)
                .invoke(sheet, col, chars * 256);
    }

    // ── Style factories ──────────────────────────────────────────

    private static Object createHeaderStyle(Object wb, Class<?> wbClass) throws Exception {
        Object style = wbClass.getMethod("createCellStyle").invoke(wb);
        Class<?> fontClass  = Class.forName("org.apache.poi.ss.usermodel.Font");
        Object   font       = wbClass.getMethod("createFont").invoke(wb);
        font.getClass().getMethod("setBold", boolean.class).invoke(font, true);
        font.getClass().getMethod("setFontHeightInPoints", short.class).invoke(font, (short)11);
        font.getClass().getMethod("setColor", short.class).invoke(font, (short)0xFFFF); // white

        Class<?> iface = Class.forName("org.apache.poi.ss.usermodel.CellStyle");
        style.getClass().getMethod("setFont", fontClass).invoke(style, font);

        // Nền tím #7C3AED → dùng XSSF custom fill
        try {
            Class<?> xssfStyle = Class.forName("org.apache.poi.xssf.usermodel.XSSFCellStyle");
            if (xssfStyle.isInstance(style)) {
                Object color = Class.forName("org.apache.poi.xssf.usermodel.XSSFColor")
                        .getDeclaredConstructor(byte[].class,
                                Class.forName("org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder"))
                        .newInstance(null, null);
                // Simplified: set solid fill via IndexedColors purple (VIOLET = 20)
                Object fillEnum = Class.forName("org.apache.poi.ss.usermodel.FillPatternType")
                        .getField("SOLID_FOREGROUND").get(null);
                style.getClass().getMethod("setFillPattern",
                        Class.forName("org.apache.poi.ss.usermodel.FillPatternType"))
                        .invoke(style, fillEnum);
                style.getClass().getMethod("setFillForegroundColor", short.class)
                        .invoke(style, (short)20); // violet
            }
        } catch (Exception ignored) {
            // Style phụ – không critical
        }

        setAllBorders(style);
        setCenterAlign(style);
        return style;
    }

    private static Object createTitleStyle(Object wb, Class<?> wbClass) throws Exception {
        Object style = wbClass.getMethod("createCellStyle").invoke(wb);
        Object font  = wbClass.getMethod("createFont").invoke(wb);
        font.getClass().getMethod("setBold", boolean.class).invoke(font, true);
        font.getClass().getMethod("setFontHeightInPoints", short.class).invoke(font, (short)14);
        Class<?> fontClass = Class.forName("org.apache.poi.ss.usermodel.Font");
        style.getClass().getMethod("setFont", fontClass).invoke(style, font);
        setCenterAlign(style);
        return style;
    }

    private static Object createDataStyle(Object wb, Class<?> wbClass) throws Exception {
        Object style = wbClass.getMethod("createCellStyle").invoke(wb);
        setAllBorders(style);
        return style;
    }

    private static Object createNumberStyle(Object wb, Class<?> wbClass) throws Exception {
        Object style = wbClass.getMethod("createCellStyle").invoke(wb);
        setAllBorders(style);
        setCenterAlign(style);
        return style;
    }

    private static void setAllBorders(Object style) throws Exception {
        Class<?> bsClass = Class.forName("org.apache.poi.ss.usermodel.BorderStyle");
        Object   thin    = bsClass.getField("THIN").get(null);
        for (String m : new String[]{"setBorderTop","setBorderBottom","setBorderLeft","setBorderRight"}) {
            try { style.getClass().getMethod(m, bsClass).invoke(style, thin); } catch (Exception ignored) {}
        }
    }

    private static void setCenterAlign(Object style) throws Exception {
        Class<?> haClass  = Class.forName("org.apache.poi.ss.usermodel.HorizontalAlignment");
        Class<?> vaClass  = Class.forName("org.apache.poi.ss.usermodel.VerticalAlignment");
        Object   center   = haClass.getField("CENTER").get(null);
        Object   vcenter  = vaClass.getField("CENTER").get(null);
        try {
            style.getClass().getMethod("setAlignment", haClass).invoke(style, center);
            style.getClass().getMethod("setVerticalAlignment", vaClass).invoke(style, vcenter);
        } catch (Exception ignored) {}
    }
}