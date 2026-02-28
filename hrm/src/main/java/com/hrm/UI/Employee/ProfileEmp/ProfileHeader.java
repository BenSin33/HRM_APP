package com.hrm.UI.Employee.ProfileEmp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.hrm.DAO.UserDAO;
import com.hrm.utils.JDBCConection;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ProfileHeader extends JPanel {

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
        LocalDate today = LocalDate.now();

        // Định dạng tiếng Việt
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd 'tháng' M, yyyy", new Locale("vi", "VN"));

        String formattedDate = today.format(formatter);

        // Viết hoa chữ cái đầu (vì EEEE mặc định viết thường)
        formattedDate = formattedDate.substring(0, 1).toUpperCase() + formattedDate.substring(1);

        JLabel lblDate = new JLabel("Hôm nay là " + formattedDate);
        lblDate.setFont(new Font("Arial", Font.PLAIN, 14));
        lblDate.setForeground(Color.GRAY);

        welcomePanel.add(lblWelcome);
        welcomePanel.add(lblDate);

        // 2. Phần Stat Cards (Sử dụng GridLayout 1 hàng 4 cột)
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Thêm 4 thẻ vào panel
        statsPanel.add(StatCard("Số ngày nghỉ phép", "12", "ngày", new Color(59, 130, 246)));
        statsPanel.add(StatCard("Lương tháng trước", fetchLastMonthSalary(manv), "triệu", new Color(16, 185, 129)));
        statsPanel.add(StatCard("Số giờ làm tháng này", fetchTotalHours(manv), "giờ", new Color(168, 85, 247)));
        statsPanel.add(StatCard("Điểm đánh giá Q4", fetchLatestEvaluationScore(manv), "/100", new Color(245, 158, 11)));

        add(welcomePanel, BorderLayout.NORTH);
        add(statsPanel, BorderLayout.CENTER);
    }

    public JPanel StatCard(String title, String value, String unit, Color iconColor) {
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(Color.WHITE);
        // Tạo bo góc và đổ bóng giả bằng Border
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(15, 15, 15, 15)));

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

    private String fetchLastMonthSalary(String manv) {
        String salary = "0";
        // Truy vấn lương của tháng liền kề trước đó
        String sql = "SELECT THUCLINH FROM bangluong " +
                "WHERE MANV = ? AND THANG = MONTH(CURRENT_DATE - INTERVAL 1 MONTH) " +
                "AND NAM = YEAR(CURRENT_DATE - INTERVAL 1 MONTH)";

        try (java.sql.Connection conn = JDBCConection.getConnection();
                java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, manv);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double amount = rs.getDouble("THUCLINH");
                    salary = String.format("%.1f", amount / 1000000); // Chuyển sang đơn vị triệu
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return salary;
    }

    private String fetchTotalHours(String manv) {
        double totalHours = 0;
        String sql = "SELECT SUM(SOGIOLAM) as total FROM chamcong " +
                "WHERE MANV = ? AND MONTH(NGAYLAMVIEC) = MONTH(CURRENT_DATE) " +
                "AND YEAR(NGAYLAMVIEC) = YEAR(CURRENT_DATE)";

        try (java.sql.Connection conn = JDBCConection.getConnection();
                java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, manv);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    totalHours = rs.getDouble("total");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Trả về số giờ, nếu là 0 thì hiển thị "0"
        return String.format("%.1f", totalHours);
    }

    private String fetchLatestEvaluationScore(String manv) {
        String score = "0";
        String sql = "SELECT TONGDIEM FROM phieudanhgia WHERE MANV = ? ORDER BY NGAYDANHGIA DESC LIMIT 1";

        try (java.sql.Connection conn = JDBCConection.getConnection();
                java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, manv);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    score = rs.getString("TONGDIEM");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return score;
    }
}
