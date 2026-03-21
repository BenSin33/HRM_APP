package com.hrm.UI.Manager.ScheduleTab;
import com.hrm.DAO.NhanVienDAO;
import com.hrm.DAO.ScheduleDAO;
import com.hrm.DTO.Manager.NhanVienDTO;
import com.hrm.DTO.Manager.ScheduleDTO;
import com.hrm.DTO.UserDTO;
import com.hrm.Service.PermissionService;
import com.hrm.utils.SessionManager;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ScheduleTable extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private LocalDate currentMonday; // Thứ 2 của tuần hiện tại
    private ScheduleDAO scheduleDAO;
    private NhanVienDAO nhanVienDAO;
    private PermissionService permissionService;
    private SchedulePanel schedulePanel; // Reference to parent for refresh
    @SuppressWarnings("unused")
    private Object[][] currentData;
    private final Map<ScheduleKey, String> pendingChanges = new HashMap<>(); // (manv, date) -> macalam ("C1..C7" hoặc "OFF")

    public ScheduleTable() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true));

        scheduleDAO = new ScheduleDAO();
        nhanVienDAO = new NhanVienDAO();
        permissionService = new PermissionService();
        currentMonday = getCurrentMonday();
        currentData = new Object[0][8];

        String[] columns = {"Nhân viên", "T2", "T3", "T4", "T5", "T6", "T7", "CN"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(90);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(new Color(248, 248, 248));
        table.setSelectionBackground(new Color(245, 245, 255));
        table.setFocusable(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Header
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setPreferredSize(new Dimension(0, 55));
        tableHeader.setReorderingAllowed(false);
        tableHeader.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value != null ? value.toString() : "", SwingConstants.CENTER);
                label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                label.setForeground(new Color(100, 100, 100));
                label.setBackground(Color.WHITE);
                label.setOpaque(true);
                label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(230, 230, 230)));
                return label;
            }
        });

        // Renderers
        table.getColumnModel().getColumn(0).setCellRenderer(new NhanVienRenderer());
        table.getColumnModel().getColumn(0).setPreferredWidth(180);

        CaLamRenderer caLamRenderer = new CaLamRenderer();
        for (int i = 1; i <= 7; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(caLamRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(248, 248, 248));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, BorderLayout.CENTER);

        // ===== ADD MOUSE LISTENER =====
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onTableCellClicked(e);
            }
        });
    }

    /**
     * Handle cell click event
     */
    private void onTableCellClicked(MouseEvent e) {
        // Kiểm tra quyền trước khi cho phép thay đổi
        UserDTO currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null || !permissionService.canEdit(currentUser, "CN10")) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền chỉnh sửa lịch làm việc", 
                "Từ chối truy cập", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int row = table.rowAtPoint(e.getPoint());
        int column = table.columnAtPoint(e.getPoint());

        // Bỏ qua click trên header hoặc cột đầu tiên (tên nhân viên)
        if (row < 0 || column <= 0) {
            return;
        }

        // Lấy thông tin nhân viên
        String employeeInfo = (String) tableModel.getValueAt(row, 0);
        if (employeeInfo == null || !employeeInfo.contains("|")) {
            return;
        }

        String[] parts = employeeInfo.split("\\|");
        String employeeName = parts[0];

        // Lấy mã nhân viên (ưu tiên từ data: ten|chucVu|maNV)
        String manv = parts.length >= 3 ? parts[2] : null;
        if (manv != null && manv.trim().isEmpty()) {
            manv = null;
        }

        // Fallback: Tìm mã nhân viên từ tên
        if (manv == null) {
            manv = findEmployeeCodeByName(employeeName);
        }
        if (manv == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Tính ngày tương ứng với cột
        LocalDate selectedDate = currentMonday.plusDays(column - 1);

        // Lấy danh sách ca làm việc
        List<ScheduleDTO> shifts = scheduleDAO.getAllShifts();

        // Hiển thị dialog
        ShiftSelectionDialog dialog = new ShiftSelectionDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                employeeName,
                selectedDate,
                shifts
        );
        dialog.setVisible(true);

        // Kiểm tra việc chọn
        if (dialog.getResult() == JOptionPane.OK_OPTION) {
            String selectedShift = dialog.getSelectedShift();

            // Cập nhật UI (chưa lưu DB) + đánh dấu pending
            String displayValue = buildDisplayValue(selectedShift, shifts);
            tableModel.setValueAt(displayValue, row, column);
            pendingChanges.put(new ScheduleKey(manv, selectedDate), selectedShift);

            if (schedulePanel != null) {
                schedulePanel.setSaveEnabled(!pendingChanges.isEmpty());
            }
        }
    }

    private String buildDisplayValue(String selectedShift, List<ScheduleDTO> shifts) {
        if (selectedShift == null) return "";
        if ("OFF".equals(selectedShift)) {
            return "OFF";
        }
        // selectedShift là MACALAM (C1..C7) -> tìm giờ làm
        for (ScheduleDTO s : shifts) {
            if (selectedShift.equals(s.getShift())) {
                String code = getShiftCode(selectedShift);
                String time = (s.getStartTime() != null ? s.getStartTime() : "") + "-" + (s.getEndTime() != null ? s.getEndTime() : "");
                return code + "|" + time;
            }
        }
        return getShiftCode(selectedShift);
    }

    private String getShiftCode(String macalam) {
        switch (macalam) {
            case "C1": return "HC";
            case "C2": return "S";
            case "C3": return "C";
            case "C4": return "T";
            case "C5": return "S";
            case "C6": return "C";
            case "C7": return "T";
            default: return "?";
        }
    }

    /**
     * Tìm mã nhân viên theo tên
     */
    private String findEmployeeCodeByName(String employeeName) {
        List<NhanVienDTO> employees = nhanVienDAO.getAll();
        if (employees == null) {
            return null;
        }
        for (NhanVienDTO emp : employees) {
            if (emp.getHoten() != null && emp.getHoten().equals(employeeName)) {
                return emp.getManv();
            }
        }
        return null;
    }

    /**
     * Lấy thứ 2 của tuần hiện tại
     */
    private LocalDate getCurrentMonday() {
        LocalDate today = LocalDate.now();
        return today.minusDays(today.getDayOfWeek().getValue() - 1);
    }

    public void loadTableData(Object[][] data) {
        currentData = data;
        tableModel.setRowCount(0);
        for (Object[] row : data) {
            tableModel.addRow(row);
        }
    }

    public void setData(Object[][] data) {
        currentData = data;
        tableModel.setRowCount(0);
        for (Object[] row : data) {
            tableModel.addRow(row);
        }
    }

    public void updateWeekHeaders(LocalDate newMonday) {
        currentMonday = newMonday;
        String[] days = {"T2", "T3", "T4", "T5", "T6", "T7", "CN"};
        for (int i = 0; i < 7; i++) {
            LocalDate date = currentMonday.plusDays(i);
            String header = "<html><center>" + days[i] + "<br><b>"
                    + date.getDayOfMonth() + "/" + date.getMonthValue()
                    + "</b></center></html>";
            table.getColumnModel().getColumn(i + 1).setHeaderValue(header);
        }
        table.getTableHeader().repaint();
    }

    public void clearPendingChanges() {
        pendingChanges.clear();
        if (schedulePanel != null) {
            schedulePanel.setSaveEnabled(false);
        }
    }

    public boolean savePendingChanges() {
        if (pendingChanges.isEmpty()) return true;
        boolean allOk = true;
        for (Map.Entry<ScheduleKey, String> entry : pendingChanges.entrySet()) {
            ScheduleKey key = entry.getKey();
            String macalam = entry.getValue();
            boolean ok = scheduleDAO.saveOrUpdateSchedule(key.manv, key.date, macalam);
            if (!ok) {
                allOk = false;
                break;
            }
        }
        if (allOk) {
            clearPendingChanges();
        }
        return allOk;
    }

    private static final class ScheduleKey {
        private final String manv;
        private final LocalDate date;

        private ScheduleKey(String manv, LocalDate date) {
            this.manv = manv;
            this.date = date;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ScheduleKey)) return false;
            ScheduleKey that = (ScheduleKey) o;
            return Objects.equals(manv, that.manv) && Objects.equals(date, that.date);
        }

        @Override
        public int hashCode() {
            return Objects.hash(manv, date);
        }
    }

    /**
     * Set reference to parent SchedulePanel for refresh
     */
    public void setSchedulePanel(SchedulePanel panel) {
        this.schedulePanel = panel;
    }
}
