package com.hrm.UI.Employee.ProfileEmp;

import javax.swing.*;
import java.awt.*;
import com.hrm.DAO.Employee.ProfileDAO;
import com.hrm.DTO.Employee.ProfileDTO;

public class ProfileManage extends JPanel {
    
    public ProfileManage(String manv) {
        // 1. Lấy dữ liệu từ DAO theo chuẩn DTO
        ProfileDAO dao = new ProfileDAO();
        // Phương thức getFullProfile sẽ trả về đối tượng EmployeeDTO chứa đầy đủ 
        // thông tin cá nhân, công việc và hợp đồng đã được JOIN từ SQL
        ProfileDTO dto = dao.getFullProfile(manv);

        // 2. Thiết lập Layout chính
        setLayout(new BorderLayout());
        setBackground(new Color(245, 246, 250)); // Nền xám nhạt hiện đại

        // 3. Kiểm tra dữ liệu tránh lỗi hiển thị
        if (dto == null || dto.maNV == null) {
            JPanel errorPanel = new JPanel(new GridBagLayout());
            errorPanel.setOpaque(false);
            JLabel lblError = new JLabel("⚠️ Không tìm thấy dữ liệu cho mã nhân viên: " + manv);
            lblError.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            lblError.setForeground(Color.GRAY);
            errorPanel.add(lblError);
            add(errorPanel, BorderLayout.CENTER);
            return;
        }

        // --- PHẦN BÊN TRÁI: SIDEBAR (PROFILE CARD) ---
        // ProfileSidebar nhận EmployeeDTO để vẽ Avatar Gradient và Badge trạng thái
        ProfileSidebar sidebar = new ProfileSidebar(dto);
        add(sidebar, BorderLayout.WEST);

        // --- PHẦN BÊN PHẢI: NỘI DUNG CHI TIẾT ---
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setOpaque(false);
        
        // Header: Tiêu đề trang (Profile Overview / Hồ sơ chi tiết)
        // Lưu ý: Nếu lớp ProfileHeader của bạn chưa có, hãy tạo đơn giản với một JLabel
        mainContent.add(new ProfileHeader(), BorderLayout.NORTH);
        
        // Info: Chứa 3 bảng thông tin (Cá nhân, Công việc, Hợp đồng) + ScrollBar chuyên nghiệp
        // ProfileInfo nhận EmployeeDTO để đổ dữ liệu vào các Card
        ProfileInfo infoPanel = new ProfileInfo(dto);
        mainContent.add(infoPanel, BorderLayout.CENTER);
        
        // Footer: Dòng lưu ý hoặc bản quyền bên dưới
        mainContent.add(new ProfileFooter(), BorderLayout.SOUTH);
        
        add(mainContent, BorderLayout.CENTER);
    }
}

// Gợi ý nhỏ nếu bạn chưa có ProfileHeader:
class ProfileHeader extends JPanel {
    public ProfileHeader() {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 40, 20));
        JLabel title = new JLabel("Chi tiết hồ sơ nhân viên");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(33, 37, 41));
        add(title);
    }
}

// Gợi ý nhỏ nếu bạn chưa có ProfileFooter:
class ProfileFooter extends JPanel {
    public ProfileFooter() {
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(10, 40, 20, 40));
        setLayout(new BorderLayout());
        JLabel note = new JLabel("© 2024 HRM System - Thông tin bảo mật nội bộ");
        note.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        note.setForeground(Color.LIGHT_GRAY);
        add(note, BorderLayout.WEST);
    }
}