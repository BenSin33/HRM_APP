package com.hrm.UI.Employee.ProfileEmp;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import com.hrm.DAO.Employee.ProfileDAO;

public class ProfileManage extends JPanel {
    
    public ProfileManage(String manv) {
        // 1. Lấy dữ liệu từ DAO
        ProfileDAO dao = new ProfileDAO();
        // Giả sử bạn đã cập nhật ProfileDAO để trả về Map đầy đủ thông tin
        Map<String, String> data = dao.getProfileFullData(manv);

        // 2. Thiết lập Layout chính
        setLayout(new BorderLayout());
        setBackground(new Color(245, 246, 250)); // Màu nền xám nhạt hiện đại

        // 3. Kiểm tra dữ liệu tránh lỗi NullPointer
        if (data == null || data.isEmpty()) {
            add(new JLabel("Không tìm thấy dữ liệu nhân viên: " + manv), BorderLayout.CENTER);
            return;
        }

        // --- PHẦN BÊN TRÁI: SIDEBAR (PROFILE CARD) ---
        // Truyền toàn bộ Map 'data' vào Sidebar để vẽ Avatar và thông tin định danh
        ProfileSidebar sidebar = new ProfileSidebar(data);
        add(sidebar, BorderLayout.WEST);

        // --- PHẦN BÊN PHẢI: NỘI DUNG CHI TIẾT ---
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setOpaque(false);
        
        // Header: Tiêu đề trang
        mainContent.add(new ProfileHeader(), BorderLayout.NORTH);
        
        // Info: Các thẻ chứa thông tin chi tiết (Email, SĐT, Phòng ban...)
        mainContent.add(new ProfileInfo(data), BorderLayout.CENTER);
        
        // Footer: Dòng lưu ý dưới cùng
        mainContent.add(new ProfileFooter(), BorderLayout.SOUTH);
        
        add(mainContent, BorderLayout.CENTER);
    }
}