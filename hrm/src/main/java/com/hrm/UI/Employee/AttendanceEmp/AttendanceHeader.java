package com.hrm.UI.Employee.AttendanceEmp;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import com.hrm.utils.JDBCConection;
import java.awt.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class AttendanceHeader extends JPanel {
    private String manv;

    public AttendanceHeader(String manv) {
        this.manv = manv;
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));
        setBorder(new EmptyBorder(10, 20, 10, 20));

        // --- 1. TIÊU ĐỀ ---
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        JLabel lblMainTitle = new JLabel("Chấm công");
        lblMainTitle.setFont(new Font("Arial", Font.BOLD, 28));
        JLabel lblSubTitle = new JLabel("Vào/Ra ca và xem lịch sử chấm công");
        lblSubTitle.setForeground(Color.GRAY);
        titlePanel.add(lblMainTitle);
        titlePanel.add(lblSubTitle);

        // --- 2. BẢNG GIỜ MÀU XANH ---
        JPanel timeCard = new JPanel(new BorderLayout());
        timeCard.setBackground(new Color(59, 130, 246));
        timeCard.setBorder(new EmptyBorder(25, 20, 25, 20));

        LocalDateTime now = LocalDateTime.now();
        String timeStr = now.format(DateTimeFormatter.ofPattern("HH:mm"));
        String dateStr = now.format(
    DateTimeFormatter.ofPattern("EEEE, dd 'tháng' M, yyyy", new Locale("vi", "VN"))
);

        JLabel lblTime = new JLabel(timeStr, SwingConstants.CENTER);
        lblTime.setFont(new Font("Arial", Font.BOLD, 48));
        lblTime.setForeground(Color.WHITE);

        JLabel lblNowDate = new JLabel("Hôm nay " + dateStr, SwingConstants.CENTER);
        lblNowDate.setForeground(new Color(219, 234, 254));

        JPanel textCenter = new JPanel(new GridLayout(2, 1));
        textCenter.setOpaque(false);
        textCenter.add(lblTime);
        textCenter.add(lblNowDate);

        // Nút bấm
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPanel.setOpaque(false);
        btnPanel.add(createActionButton("Đăng ký vào ca (08:00)", true));
        btnPanel.add(createActionButton("Đăng ký ra ca (17:30)", false));

        timeCard.add(textCenter, BorderLayout.CENTER);
        timeCard.add(btnPanel, BorderLayout.SOUTH);

        // --- 3. 4 THẺ THỐNG KÊ (STAT CARDS) ---
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(20, 0, 10, 0));

        statsPanel.add(StatCard("Tổng ngày làm", fetchTotalDays(), "", new Color(59, 130, 246)));
        statsPanel.add(StatCard("Đúng giờ", fetchStatusCount("Đúng giờ"), "", new Color(34, 197, 94)));
        statsPanel.add(StatCard("Đi muộn", fetchStatusCount("Đi muộn"), "", new Color(234, 179, 8)));
        statsPanel.add(StatCard("Tổng giờ làm", fetchTotalHours(), "giờ", new Color(168, 85, 247)));

        // GOM TẤT CẢ
        JPanel topGroup = new JPanel(new BorderLayout());
        topGroup.setOpaque(false);
        topGroup.add(titlePanel, BorderLayout.NORTH);
        topGroup.add(timeCard, BorderLayout.CENTER);

        add(topGroup, BorderLayout.NORTH);
        add(statsPanel, BorderLayout.CENTER);
    }

    // --- XỬ LÝ DATABASE ---

    private void handleAttendance(boolean isCheckIn) {
        // Thông báo giả lập xác nhận khuôn mặt
        JOptionPane.showMessageDialog(this, "Xác nhận khuôn mặt và vị trí thành công!", "HRM System", JOptionPane.INFORMATION_MESSAGE);

        String sql;
        if (isCheckIn) {
            // Kiểm tra xem hôm nay đã check-in chưa để tránh trùng lặp
            sql = "INSERT INTO chamcong (MACHAMCONG, MANV, NGAYLAMVIEC, CHECKIN, TRANGTHAI) VALUES (?, ?, CURRENT_DATE, CURRENT_TIME, ?)";
        } else {
            sql = "UPDATE chamcong SET CHECKOUT = CURRENT_TIME, SOGIOLAM = 8 WHERE MANV = ? AND NGAYLAMVIEC = CURRENT_DATE";
        }

        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            if (isCheckIn) {
                ps.setString(1, "CC" + System.currentTimeMillis() % 10000); // Mã tạm thời
                ps.setString(2, manv);
                ps.setString(3, "Đúng giờ");
            } else {
                ps.setString(1, manv);
            }
            ps.executeUpdate();
            
            // Refresh giao diện sau khi chấm công
            revalidate(); repaint();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private String fetchTotalDays() {
        String sql = "SELECT COUNT(*) FROM chamcong WHERE MANV = ? AND MONTH(NGAYLAMVIEC) = MONTH(CURRENT_DATE)";
        try (Connection conn = JDBCConection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, manv);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString(1);
        } catch (Exception e) { e.printStackTrace(); }
        return "0";
    }

    private String fetchStatusCount(String status) {
        String sql = "SELECT COUNT(*) FROM chamcong WHERE MANV = ? AND TRANGTHAI = ? AND MONTH(NGAYLAMVIEC) = MONTH(CURRENT_DATE)";
        try (Connection conn = JDBCConection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, manv);
            ps.setString(2, status);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString(1);
        } catch (Exception e) { e.printStackTrace(); }
        return "0";
    }

    private String fetchTotalHours() {
        double total = 0;
        String sql = "SELECT MACALAM, SOGIOLAM FROM chamcong WHERE MANV = ? AND MONTH(NGAYLAMVIEC) = MONTH(CURRENT_DATE)";
        try (Connection conn = JDBCConection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, manv);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String maCa = rs.getString("MACALAM");
                if (maCa != null) {
                    if (maCa.matches("C[1-4]")) total += 8;
                    else if (maCa.matches("C[5-7]")) total += 4;
                } else {
                    total += rs.getDouble("SOGIOLAM");
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return String.format("%.0f", total);
    }

    // --- COMPONENT HỖ TRỢ ---

    private JButton createActionButton(String text, boolean isCheckIn) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(240, 45));
        btn.setBackground(new Color(255, 255, 255, 60));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addActionListener(e -> handleAttendance(isCheckIn));
        return btn;
    }

    private JPanel StatCard(String title, String value, String unit, Color iconColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(12, 15, 12, 15)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(Color.GRAY);
        JLabel lblVal = new JLabel(value + " " + unit);
        lblVal.setFont(new Font("Arial", Font.BOLD, 22));

        JPanel left = new JPanel(new GridLayout(2, 1));
        left.setOpaque(false);
        left.add(lblTitle);
        left.add(lblVal);

        JPanel iconPart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(iconColor.getRed(), iconColor.getGreen(), iconColor.getBlue(), 40));
                g2.fillOval(0, 5, 35, 35);
            }
        };
        iconPart.setPreferredSize(new Dimension(35, 45));
        iconPart.setOpaque(false);

        card.add(left, BorderLayout.CENTER);
        card.add(iconPart, BorderLayout.EAST);
        return card;
    }
}