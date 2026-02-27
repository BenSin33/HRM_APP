package com.hrm.UI.Employee.ProfileEmp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.hrm.DAO.UserDAO;
import com.hrm.utils.JDBCConection;

import java.awt.*;

public class ProfileHeader extends JPanel {
    private UserDAO userDAO = new UserDAO();

    public ProfileHeader(String manv) {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250)); // Màu nền xám nhạt
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. Phần lời chào (Welcome Section)
        JPanel welcomePanel = new JPanel(new GridLayout(2, 1));
        welcomePanel.setOpaque(false);

        String fullname = fetchEmployeeName(manv);
        JLabel lblWelcome = new JLabel("Xin chào, " + fullname + "!");
        
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 24));
        lblWelcome.setForeground(new Color(33, 37, 41));

        JLabel lblDate = new JLabel("Hôm nay là Thứ Bảy, 28 tháng 2, 2026");
        lblDate.setFont(new Font("Arial", Font.PLAIN, 14));
        lblDate.setForeground(Color.GRAY);

        welcomePanel.add(lblWelcome);
        welcomePanel.add(lblDate);

        // 2. Phần Stat Cards (Sử dụng GridLayout 1 hàng 4 cột)
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Thêm 4 thẻ vào panel
        statsPanel.add(StatCard("Số ngày phép còn lại", "12", "ngày", new Color(59, 130, 246)));
        statsPanel.add(StatCard("Lương tháng này", "22.5", "triệu", new Color(16, 185, 129)));
        statsPanel.add(StatCard("Số giờ làm tháng này", "168", "giờ", new Color(168, 85, 247)));
        statsPanel.add(StatCard("Điểm đánh giá Q4", "95", "/100", new Color(245, 158, 11)));

        add(welcomePanel, BorderLayout.NORTH);
        add(statsPanel, BorderLayout.CENTER);
    }
    public JPanel StatCard(String title, String value, String unit, Color iconColor) {
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(Color.WHITE);
        // Tạo bo góc và đổ bóng giả bằng Border
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        // Nội dung bên trái (Chữ)
        JPanel leftPanel = new JPanel(new GridLayout(2, 1));
        leftPanel.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 13));
        lblTitle.setForeground(Color.GRAY);

        // Panel chứa số và đơn vị
        JPanel valPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        valPanel.setOpaque(false);
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.BOLD, 20));
        JLabel lblUnit = new JLabel(unit);
        lblUnit.setForeground(Color.GRAY);
        
        valPanel.add(lblValue);
        valPanel.add(lblUnit);

        leftPanel.add(lblTitle);
        leftPanel.add(valPanel);

        // Nội dung bên phải (Icon giả định bằng một ô màu)
        JPanel iconPanel = new JPanel();
        iconPanel.setPreferredSize(new Dimension(40, 40));
        iconPanel.setBackground(iconColor);
        // Lưu ý: Trong thực tế bạn nên dùng JLabel kèm ImageIcon vào đây

        cardPanel.add(leftPanel, BorderLayout.CENTER);
        cardPanel.add(iconPanel, BorderLayout.EAST);
        return cardPanel;
    }
    private String fetchEmployeeName(String manv) {
        String name = "Nhân viên"; // Giá trị mặc định
        String sql = "SELECT HOTEN FROM nhanvien WHERE MANV = ?";
        
        try (java.sql.Connection conn = JDBCConection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, manv);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    name = rs.getString("HOTEN");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }
}
