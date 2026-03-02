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

// Lớp ProfileHeader và ProfileFooter đã được tách ra thành các file riêng
// ProfileHeader.java và ProfileFooter.java
