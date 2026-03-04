package com.hrm.UI.Employee.ProfileEmp;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
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
            JLabel lblError = new JLabel("Không tìm thấy dữ liệu cho mã nhân viên: " + manv,
                    createWarningIcon(new Color(245, 158, 11), 16), SwingConstants.LEFT);
            lblError.setIconTextGap(8);
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

    private ImageIcon createWarningIcon(Color color, int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);

        Polygon triangle = new Polygon();
        triangle.addPoint(size / 2, 1);
        triangle.addPoint(1, size - 2);
        triangle.addPoint(size - 2, size - 2);
        g2d.fillPolygon(triangle);

        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(size / 2 - 1, 4, 2, 7, 1, 1);
        g2d.fillOval(size / 2 - 1, 12, 2, 2);

        g2d.dispose();
        return new ImageIcon(image);
    }
}

// Lớp ProfileHeader và ProfileFooter đã được tách ra thành các file riêng
// ProfileHeader.java và ProfileFooter.java
