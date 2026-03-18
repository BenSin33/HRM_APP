package com.hrm.UI.Employee.AttendanceEmp;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import com.hrm.Service.Employee.AttendanceService;
import com.hrm.DTO.Employee.AttendanceDTO;

public class AttendanceSearch extends JPanel {
    private String manv;
    private DefaultTableModel tableModel;
    private JTable resultTable;
    private YearMonth currentMonth;

    public AttendanceSearch(String manv) {
        this.manv = manv;
        this.currentMonth = java.time.YearMonth.now();
        initUI();
        showCurrentMonthHistory();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setOpaque(false);
        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.NORTH);

        String[] columnNames = {"Tên", "Ngày", "Ca làm", "Giờ vào ca", "Giờ ra ca", "Trạng thái"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultTable = new JTable(tableModel);
        resultTable.setRowHeight(28);
        resultTable.setFont(new Font("Arial", Font.PLAIN, 16));
        resultTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        // Căn giữa dữ liệu các ô
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < resultTable.getColumnCount(); i++) {
            resultTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        // Thêm border cho các ô
        resultTable.setShowGrid(true);
        resultTable.setGridColor(new Color(200, 200, 200));
        JScrollPane tableScroll = new JScrollPane(resultTable);
        tableScroll.setPreferredSize(new Dimension(600, 160));
        add(tableScroll, BorderLayout.CENTER);
    }

    private JPanel createSearchPanel() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);

        // --- Dòng 1: Chọn ngày + nút tải lại ---
        JPanel row1 = new JPanel(new BorderLayout());
        row1.setOpaque(false);
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        JLabel lblNgay = new JLabel("Chọn ngày: ");
        lblNgay.setFont(new Font("Arial", Font.BOLD, 18));
        leftPanel.add(lblNgay);
        JComboBox<Integer> cbDay = new JComboBox<>();
        JComboBox<Integer> cbMonth = new JComboBox<>();
        JComboBox<Integer> cbYear = new JComboBox<>();
        cbDay.setFont(new Font("Arial", Font.PLAIN, 16));
        cbMonth.setFont(new Font("Arial", Font.PLAIN, 16));
        cbYear.setFont(new Font("Arial", Font.PLAIN, 16));
        for (int i = 1; i <= 31; i++) cbDay.addItem(i);
        for (int i = 1; i <= 12; i++) cbMonth.addItem(i);
        int nowYear = java.time.LocalDate.now().getYear();
        for (int i = nowYear - 1; i <= nowYear + 1; i++) cbYear.addItem(i);
        leftPanel.add(cbDay);
        leftPanel.add(new JLabel("/"));
        leftPanel.add(cbMonth);
        leftPanel.add(new JLabel("/"));
        leftPanel.add(cbYear);
        JButton btnSearchDate = new JButton("Tìm kiếm");
        btnSearchDate.setFont(new Font("Arial", Font.BOLD, 16));
        btnSearchDate.setBackground(new Color(59, 130, 246));
        btnSearchDate.setForeground(Color.WHITE);
        leftPanel.add(btnSearchDate);

        JButton btnReload = new JButton("Tải lại");
        btnReload.setFont(new Font("Arial", Font.BOLD, 16));
        btnReload.setBackground(new Color(34, 197, 94));
        btnReload.setForeground(Color.WHITE);
        btnReload.setFocusPainted(false);
        btnReload.setPreferredSize(new Dimension(100, 32));

        row1.add(leftPanel, BorderLayout.CENTER);
        row1.add(btnReload, BorderLayout.EAST);
        // Sự kiện nút tải lại
        btnReload.addActionListener(e -> {
            showCurrentMonthHistory();
        });

        // --- Dòng 2: Trạng thái & Ca làm ---
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.setOpaque(false);
        JLabel lblTrangThai = new JLabel("Trạng thái: ");
        lblTrangThai.setFont(new Font("Arial", Font.BOLD, 18));
        row2.add(lblTrangThai);
        String[] statuses = { "Tất cả", "Đúng giờ", "Đi muộn/Về sớm" };
        JComboBox<String> cbStatus = new JComboBox<>(statuses);
        cbStatus.setFont(new Font("Arial", Font.PLAIN, 16));
        row2.add(cbStatus);
        JLabel lblCaLam = new JLabel("  Ca làm: ");
        lblCaLam.setFont(new Font("Arial", Font.BOLD, 18));
        row2.add(lblCaLam);
        AttendanceService shiftDao = new AttendanceService();
        LinkedHashMap<String, String> shiftDisplayMap = shiftDao.getShiftDisplayMap();
        JComboBox<String> cbShift = new JComboBox<>();
        cbShift.addItem("Tất cả");
        for (String display : shiftDisplayMap.values()) {
            cbShift.addItem(display);
        }
        cbShift.setFont(new Font("Arial", Font.PLAIN, 16));
        row2.add(cbShift);
        JButton btnSearchStatus = new JButton("Tìm kiếm");
        btnSearchStatus.setFont(new Font("Arial", Font.BOLD, 16));
        btnSearchStatus.setBackground(new Color(59, 130, 246));
        btnSearchStatus.setForeground(Color.WHITE);
        row2.add(btnSearchStatus);

        container.add(row1);
        container.add(row2);

        // Xử lý sự kiện tìm kiếm
        btnSearchDate.addActionListener(e -> {
            AttendanceService dao = new AttendanceService();
            ArrayList<AttendanceDTO> results = dao.searchAttendance(
                    manv,
                    (int) cbDay.getSelectedItem(),
                    (int) cbMonth.getSelectedItem(),
                    (int) cbYear.getSelectedItem(),
                    "Tất cả",
                    "Tất cả");
            tableModel.setRowCount(0);
            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu phù hợp!");
            } else {
                for (AttendanceDTO dto : results) {
                    String ten = manv;
                    String ngay = dto.getNgayLamViec() != null ? dto.getNgayLamViec().toString() : "";
                    String caLam = dto.getMaCaLam() != null ? dto.getMaCaLam() : "";
                    String gioVao = dto.getCheckIn() != null ? dto.getCheckIn().toString().substring(0,5) : "";
                    String gioRa = dto.getCheckOut() != null ? dto.getCheckOut().toString().substring(0,5) : "";
                    String trangThai = dto.getTrangThai() != null ? dto.getTrangThai() : "";
                    tableModel.addRow(new Object[]{ten, ngay, caLam, gioVao, gioRa, trangThai});
                }
            }
        });
        btnSearchStatus.addActionListener(e -> {
            AttendanceService dao = new AttendanceService();
            String selectedShiftDisplay = cbShift.getSelectedItem().toString();
            String selectedShiftCode = "Tất cả";
            if (!"Tất cả".equals(selectedShiftDisplay)) {
                for (java.util.Map.Entry<String, String> entry : shiftDisplayMap.entrySet()) {
                    if (entry.getValue().equals(selectedShiftDisplay)) {
                        selectedShiftCode = entry.getKey();
                        break;
                    }
                }
            }
            ArrayList<AttendanceDTO> results = dao.searchAttendance(
                    manv,
                    null,
                    null, 
                    null,
                    cbStatus.getSelectedItem().toString(),
                    selectedShiftCode);
            tableModel.setRowCount(0);
            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu phù hợp!");
            } else {
                for (AttendanceDTO dto : results) {
                    String ten = manv;
                    String ngay = dto.getNgayLamViec() != null ? dto.getNgayLamViec().toString() : "";
                    String caLam = dto.getMaCaLam() != null ? dto.getMaCaLam() : "";
                    String gioVao = dto.getCheckIn() != null ? dto.getCheckIn().toString().substring(0,5) : "";
                    String gioRa = dto.getCheckOut() != null ? dto.getCheckOut().toString().substring(0,5) : "";
                    String trangThai = dto.getTrangThai() != null ? dto.getTrangThai() : "";
                    tableModel.addRow(new Object[]{ten, ngay, caLam, gioVao, gioRa, trangThai});
                }
            }
        });

        return container;
    }

    private void showCurrentMonthHistory() {
        AttendanceService dao = new AttendanceService();
        ArrayList<AttendanceDTO> results = dao.searchAttendance(
            manv,
            null,
            currentMonth.getMonthValue(),
            currentMonth.getYear(),
            "Tất cả",
            "Tất cả"
        );
        tableModel.setRowCount(0);
        for (AttendanceDTO dto : results) {
            String ten = manv;
            String ngay = dto.getNgayLamViec() != null ? dto.getNgayLamViec().toString() : "";
            String caLam = dto.getMaCaLam() != null ? dto.getMaCaLam() : "";
            String gioVao = dto.getCheckIn() != null ? dto.getCheckIn().toString().substring(0,5) : "";
            String gioRa = dto.getCheckOut() != null ? dto.getCheckOut().toString().substring(0,5) : "";
            String trangThai = dto.getTrangThai() != null ? dto.getTrangThai() : "";
            tableModel.addRow(new Object[]{ten, ngay, caLam, gioVao, gioRa, trangThai});
        }
    }
}
