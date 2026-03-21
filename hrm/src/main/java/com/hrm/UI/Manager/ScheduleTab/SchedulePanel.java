package com.hrm.UI.Manager.ScheduleTab;

import com.hrm.UI.Manager.color.ColorScheme;
import com.hrm.Service.NhanVienService;
import com.hrm.Service.PermissionService;
import com.hrm.DTO.UserDTO;
import com.hrm.utils.SessionManager;
import com.hrm.DAO.NhanVienDAO;
import com.hrm.DTO.Manager.NhanVienDTO;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class SchedulePanel extends JPanel {
    private NhanVienService nhanVienService;
    private PermissionService permissionService;
    private NhanVienDAO nhanVienDAO;
    
    private ScheduleHeader header;
    private ScheduleNavigator navigator;
    private ScheduleTable table;
    private ScheduleLegend legend;
    private LocalDate currentMonday;
    private JButton btnSave;
    private JButton btnResetWeek;

    public SchedulePanel() {
        setLayout(new BorderLayout());
        setBackground(ColorScheme.MAIN_BG);

        nhanVienService = new NhanVienService();
        permissionService = new PermissionService();
        nhanVienDAO = new NhanVienDAO();
        currentMonday = calculateCurrentMonday();

        // Lấy user hiện tại để kiểm tra quyền
        UserDTO currentUser = SessionManager.getInstance().getCurrentUser();
        boolean canEdit = currentUser != null && permissionService.canEdit(currentUser, "CN10");

        // Header
        header = new ScheduleHeader();
        add(header, BorderLayout.NORTH);

        // Content Area
        JPanel contentArea = new JPanel(new BorderLayout(0, 20));
        contentArea.setBackground(ColorScheme.MAIN_BG);
        contentArea.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Navigator
        navigator = new ScheduleNavigator(currentMonday);
        navigator.setOnWeekChanged(newMonday -> {
            currentMonday = newMonday;
            table.clearPendingChanges();
            setSaveEnabled(false);
            table.updateWeekHeaders(currentMonday);
            loadData();
        });

        btnSave = new JButton("Lưu");
        btnSave.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnSave.setBackground(new Color(16, 185, 129));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.setEnabled(false);
        // Disable nút Lưu nếu không có quyền edit
        if (!canEdit) {
            btnSave.setEnabled(false);
            btnSave.setToolTipText("Bạn không có quyền chỉnh sửa lịch làm việc");
        }
        btnSave.addActionListener(e -> onSave());

        btnResetWeek = new JButton("Reset tuần");
        btnResetWeek.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnResetWeek.setBackground(new Color(239, 68, 68));
        btnResetWeek.setForeground(Color.WHITE);
        btnResetWeek.setFocusPainted(false);
        btnResetWeek.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        btnResetWeek.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Disable nút Reset nếu không có quyền edit
        if (!canEdit) {
            btnResetWeek.setEnabled(false);
            btnResetWeek.setToolTipText("Bạn không có quyền xóa lịch làm việc");
        }
        btnResetWeek.addActionListener(e -> onResetWeek());

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightButtons.setBackground(ColorScheme.MAIN_BG);
        rightButtons.add(btnResetWeek);
        rightButtons.add(btnSave);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(ColorScheme.MAIN_BG);
        topBar.add(navigator, BorderLayout.WEST);
        topBar.add(rightButtons, BorderLayout.EAST);
        contentArea.add(topBar, BorderLayout.NORTH);

        // Table
        table = new ScheduleTable();
        table.setSchedulePanel(this); // ← BỎ QUYỀN THAM CHIẾU ĐẾN PARENT
        table.updateWeekHeaders(currentMonday);
        // Disable table nếu không có quyền edit
        table.setEnabled(canEdit);
        contentArea.add(table, BorderLayout.CENTER);

        // Legend
        legend = new ScheduleLegend();
        contentArea.add(legend, BorderLayout.SOUTH);

        add(contentArea, BorderLayout.CENTER);
        
        loadData();
        
        setVisible(true);
        revalidate();
        repaint();
    }

    private LocalDate calculateCurrentMonday() {
        LocalDate today = LocalDate.now();
        return today.minusDays(today.getDayOfWeek().getValue() - 1);
    }

    /**
     * Load dữ liệu lịch từ service
     * Chỉ load nhân viên cùng phòng ban với manager
     */
    private void loadData() {
        // Lấy thông tin manager hiện tại
        UserDTO currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Lỗi: Không tìm thấy thông tin người dùng", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Lấy thông tin manager từ DB để có mã phòng ban
        NhanVienDTO manager = nhanVienDAO.findById(currentUser.getManv());
        if (manager == null) {
            JOptionPane.showMessageDialog(this, "Lỗi: Không tìm thấy thông tin manager", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String maphongban = manager.getMaphongban();
        if (maphongban == null || maphongban.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lỗi: Manager chưa được gán phòng ban", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Load dữ liệu theo phòng ban
        table.loadTableData(nhanVienService.getTableDataForScheduleByPhongBan(currentMonday, maphongban));
    }
    
    /**
     * Refresh bảng lịch sau khi cập nhật database
     */
    public void refresh() {
        loadData();
        table.repaint();
    }

    public void setSaveEnabled(boolean enabled) {
        if (btnSave != null) {
            btnSave.setEnabled(enabled);
        }
    }

    public LocalDate getCurrentMonday() {
        return currentMonday;
    }

    private void onSave() {
        UserDTO currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null || !permissionService.canEdit(currentUser, "CN10")) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền chỉnh sửa lịch làm việc", "Từ chối truy cập", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        boolean success = table.savePendingChanges();
        if (success) {
            setSaveEnabled(false);
            refresh();
            JOptionPane.showMessageDialog(this, "Lưu lịch làm việc thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Có lỗi khi lưu lịch làm việc!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Xử lý reset lịch làm việc của tuần hiện tại
     */
    private void onResetWeek() {
        UserDTO currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null || !permissionService.canEdit(currentUser, "CN10")) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền xóa lịch làm việc", "Từ chối truy cập", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa toàn bộ lịch làm việc của tuần này (bao gồm OFF)?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        boolean success = nhanVienService.clearScheduleForWeek(currentMonday);
        if (success) {
            table.clearPendingChanges();
            setSaveEnabled(false);
            loadData();
            table.repaint();
            JOptionPane.showMessageDialog(
                    this,
                    "Đã reset lịch làm việc cho tuần hiện tại.",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Có lỗi khi reset lịch làm việc tuần này.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}