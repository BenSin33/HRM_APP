package com.hrm.UI.Employee.HomeEmp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import com.hrm.DAO.Employee.HomeDAO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class HomeHeader extends JPanel {
    private HomeDAO HomeDAO = new HomeDAO();

    public HomeHeader(String manv) {
        String[] HomeData = HomeDAO.getHomeHeaderData(manv);
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250)); // Màu nền xám nhạt
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. Phần lời chào (Welcome Section)
        JPanel welcomePanel = new JPanel(new GridLayout(2, 1));
        welcomePanel.setOpaque(false);

        JLabel lblWelcome = new JLabel("Xin chào, " + HomeData[0] + "!");

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
        statsPanel.add(StatCard("Lương tháng trước", HomeData[1], "triệu", new Color(16, 185, 129)));
        statsPanel.add(StatCard("Số giờ làm", HomeData[2], "giờ", new Color(168, 85, 247)));
        statsPanel.add(StatCard("Số ngày nghỉ phép", HomeData[4], "ngày", new Color(59, 130, 246)));
        statsPanel.add(StatCard("Điểm đánh giá Q4", HomeData[3], "/100", new Color(245, 158, 11)));

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
}
