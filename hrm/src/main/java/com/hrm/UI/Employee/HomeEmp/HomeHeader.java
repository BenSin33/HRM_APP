package com.hrm.UI.Employee.HomeEmp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import com.hrm.DAO.Employee.HomeDAO;
import com.hrm.DTO.Employee.HomeDTO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class HomeHeader extends JPanel {
    private HomeDAO HomeDAO = new HomeDAO();

    public HomeHeader(String manv) {
        HomeDTO data = HomeDAO.getHomeHeaderData(manv);
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250)); // Màu nền xám nhạt
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. Phần lời chào (Welcome Section)
        JPanel welcomePanel = new JPanel(new GridLayout(2, 1));
        welcomePanel.setOpaque(false); // Đặt panel này trong suốt để hiển thị màu nền của HomeHeader

        JLabel lblWelcome = new JLabel("Xin chào, " + data.getHoTen() + "!");
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
        statsPanel.add(StatCard("Lương tháng trước", String.format("%.1f", data.getLuongThangTruoc()), "triệu",
                createStatIcon("salary", new Color(16, 185, 129), 26)));
        statsPanel.add(
                StatCard("Số giờ làm", String.format("%.1f", data.getTongGioLam()), "giờ", createStatIcon("time", new Color(168, 85, 247), 26)));
        statsPanel.add(StatCard("Số ngày nghỉ phép", String.valueOf(data.getSoNgayNghiPhep()), "ngày",
                createStatIcon("leave", new Color(59, 130, 246), 26)));
        statsPanel.add(
                StatCard("Điểm đánh giá Q4", String.valueOf(data.getDiemDanhGia()), "/100", createStatIcon("score", new Color(245, 158, 11), 26)));
        add(welcomePanel, BorderLayout.NORTH);
        add(statsPanel, BorderLayout.CENTER);
    }

    public JPanel StatCard(String title, String value, String unit, ImageIcon icon) {
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

                // Nội dung bên phải (ImageIcon)
                JLabel iconLabel = new JLabel(icon);
                iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
                iconLabel.setVerticalAlignment(SwingConstants.CENTER);
                iconLabel.setPreferredSize(new Dimension(40, 40));

        cardPanel.add(leftPanel, BorderLayout.CENTER);
                cardPanel.add(iconLabel, BorderLayout.EAST);
        return cardPanel;
    }

        private ImageIcon createStatIcon(String type, Color color, int size) {
                BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = image.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(color);
                g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                switch (type) {
                        case "salary":
                                g2d.drawRoundRect(3, 7, size - 6, size - 12, 5, 5);
                                g2d.drawLine(6, size / 2, size - 6, size / 2);
                                break;
                        case "time":
                                g2d.drawOval(3, 3, size - 6, size - 6);
                                g2d.drawLine(size / 2, size / 2, size / 2, 7);
                                g2d.drawLine(size / 2, size / 2, size - 8, size / 2 + 2);
                                break;
                        case "leave":
                                g2d.drawRoundRect(4, 5, size - 8, size - 8, 4, 4);
                                g2d.drawLine(7, 3, 7, 8);
                                g2d.drawLine(size - 8, 3, size - 8, 8);
                                break;
                        case "score":
                                Polygon star = new Polygon();
                                int cx = size / 2;
                                int cy = size / 2;
                                int outer = size / 2 - 2;
                                int inner = outer / 2;
                                for (int i = 0; i < 10; i++) {
                                        double angle = Math.toRadians(-90 + i * 36);
                                        int r = (i % 2 == 0) ? outer : inner;
                                        star.addPoint(cx + (int) (Math.cos(angle) * r), cy + (int) (Math.sin(angle) * r));
                                }
                                g2d.drawPolygon(star);
                                break;
                        default:
                                g2d.fillOval(4, 4, size - 8, size - 8);
                                break;
                }

                g2d.dispose();
                return new ImageIcon(image);
        }
}
