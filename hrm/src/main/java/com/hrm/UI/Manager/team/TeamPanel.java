package com.hrm.UI.Manager.team;

import com.hrm.UI.Manager.color.ColorScheme;
import com.hrm.Service.NhanVienService;
import com.hrm.utils.SessionManager;
import com.hrm.DTO.UserDTO;
import com.hrm.DAO.NhanVienDAO;
import com.hrm.DTO.Manager.NhanVienDTO;
import javax.swing.*;
import java.awt.*;

public class TeamPanel extends JPanel {
    private NhanVienService nhanVienService;
    private NhanVienDAO nhanVienDAO;
    
    private TeamHeader header;
    private TeamStats stats;
    private TeamTable table;
    private TeamFooter footer;

    public TeamPanel() {
        setLayout(new BorderLayout());
        setBackground(ColorScheme.MAIN_BG);

        nhanVienService = new NhanVienService();
        nhanVienDAO = new NhanVienDAO();

        header = new TeamHeader();
        stats = new TeamStats();
        table = new TeamTable();
        footer = new TeamFooter();

        // Header
        add(header, BorderLayout.NORTH);

        // Content Area
        JPanel contentArea = new JPanel(new BorderLayout(0, 20));
        contentArea.setBackground(ColorScheme.MAIN_BG);
        contentArea.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        contentArea.add(stats, BorderLayout.NORTH);
        contentArea.add(table, BorderLayout.CENTER);
        contentArea.add(footer, BorderLayout.SOUTH);

        add(contentArea, BorderLayout.CENTER);

        // Load data cho team của manager hiện tại
        loadData();
    }

    /**
     * Load dữ liệu team của manager hiện tại
     * Chỉ hiển thị nhân viên cùng phòng ban
     */
    private void loadData() {
        // Lấy thông tin manager hiện tại từ session
        UserDTO currentUser = SessionManager.getInstance().getCurrentUser();
        
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Lỗi: Không tìm thấy thông tin người dùng", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Lấy mã nhân viên của manager
        String manv = currentUser.getManv();
        
        // Lấy thông tin manager từ DB để có mã phòng ban
        NhanVienDTO manager = nhanVienDAO.findById(manv);
        
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
        
        // Lấy dữ liệu team từ service với lọc theo phòng ban
        Object[][] data = nhanVienService.getTableDataForTeamByPhongBan(maphongban);

        int total = data != null ? data.length : 0;
        int active = 0;
        int senior = 0;
        int junior = 0;

        if (data != null) {
            for (Object[] row : data) {
                if (row == null) continue;

                // Cấu trúc row theo service:
                // [0]=maNV, [1]=hoTen, [2]=chucVu (CV01/CV02), [3]=lienHe, [4]=ngayVaoLam, [5]=trangThai
                String chucVu = row.length > 2 && row[2] != null ? row[2].toString().trim() : "";
                String trangThai = row.length > 5 && row[5] != null ? row[5].toString().trim() : "";

                // Đang hoạt động = "Đang làm việc"
                if ("Đang làm việc".equalsIgnoreCase(trangThai)) {
                    active++;
                }

                // CV02 = Senior, CV01 = Junior
                if ("CV02".equalsIgnoreCase(chucVu)) {
                    junior++;
                } else if ("CV01".equalsIgnoreCase(chucVu)) {
                    senior++;
                }
            }
        }

        stats.updateStats(total, active, senior, junior);
        table.setData(data);
    }
    
    // ← GIỮ LẠI METHOD NÀY ĐỂ REFRESH
    public void refresh() {
        loadData();
    }

    public String getSelectedEmployeeId() {
        return table.getSelectedEmployeeId();
    }

    
}