package com.hrm.UI.Employee.AttendanceEmp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import com.hrm.utils.JDBCConection;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class AttendanceHeader extends JPanel {
    private String manv;
    private AttendanceManage parent;

    public AttendanceHeader(String manv, AttendanceManage parent) {
        this.manv = manv;
        this.parent = parent;
        initUI();
    }

    private void initUI() {
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
        String dateStr = now.format(DateTimeFormatter.ofPattern("EEEE, dd 'tháng' M, yyyy", new Locale("vi", "VN")));

        JLabel lblTime = new JLabel(timeStr, SwingConstants.CENTER);
        lblTime.setFont(new Font("Arial", Font.BOLD, 48));
        lblTime.setForeground(Color.WHITE);

        JLabel lblNowDate = new JLabel("Hôm nay " + dateStr, SwingConstants.CENTER);
        lblNowDate.setForeground(new Color(219, 234, 254));

        JPanel textCenter = new JPanel(new GridLayout(2, 1));
        textCenter.setOpaque(false);
        textCenter.add(lblTime);
        textCenter.add(lblNowDate);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPanel.setOpaque(false);
        btnPanel.add(createActionButton("Đăng ký vào ca", true));
        btnPanel.add(createActionButton("Đăng ký ra ca", false));

        timeCard.add(textCenter, BorderLayout.CENTER);
        timeCard.add(btnPanel, BorderLayout.SOUTH);

        // --- 3. THỐNG KÊ ---
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(20, 0, 10, 0));

        statsPanel.add(StatCard("Tổng ngày làm", fetchTotalDays(), "", new Color(59, 130, 246)));
        statsPanel.add(StatCard("Đúng giờ", fetchStatusCount("Đúng giờ"), "", new Color(34, 197, 94)));
        statsPanel.add(StatCard("Đi muộn", fetchStatusCount("Đi muộn"), "", new Color(234, 179, 8)));
        statsPanel.add(StatCard("Tổng giờ làm", fetchTotalHours(), "giờ", new Color(168, 85, 247)));

        JPanel topGroup = new JPanel(new BorderLayout());
        topGroup.setOpaque(false);
        topGroup.add(titlePanel, BorderLayout.NORTH);
        topGroup.add(timeCard, BorderLayout.CENTER);

        add(topGroup, BorderLayout.NORTH);
        add(statsPanel, BorderLayout.CENTER);
    }

    private void handleAttendance(boolean isCheckIn) {
        try (Connection conn = JDBCConection.getConnection()) {
            if (isCheckIn) {
                // Kiểm tra đã check-in chưa
                String checkSql = "SELECT COUNT(*) FROM chamcong WHERE MANV = ? AND NGAYLAMVIEC = CURRENT_DATE";
                try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                    ps.setString(1, manv);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(this, "Bạn đã điểm danh vào ca hôm nay!", "Thông báo",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }

                // Thực hiện Check-in (Sử dụng CASE để xét đi muộn dựa trên bảng calam)
                String sqlCheckIn = "INSERT INTO chamcong (MACHAMCONG, MANV, NGAYLAMVIEC, CHECKIN, TRANGTHAI, MACALAM) "
                        +
                        "SELECT ?, ?, CURRENT_DATE, CURRENT_TIME, " +
                        "CASE WHEN CURRENT_TIME <= c.GIOVAOCA THEN 'Đúng giờ' ELSE 'Đi muộn' END, l.MACALAM " +
                        "FROM lichlamviec l " +
                        "INNER JOIN calam c ON l.MACALAM = c.MACALAM " +
                        "WHERE l.MANV = ? AND l.NGAYLAMVIEC = CURRENT_DATE";
                try (PreparedStatement ps = conn.prepareStatement(sqlCheckIn)) {
                    String maCC = "CC" + System.currentTimeMillis() % 1000000; // Mã duy nhất hơn
                    ps.setString(1, maCC);
                    ps.setString(2, manv);
                    ps.setString(3, manv);
                    if (ps.executeUpdate() > 0) {
                        JOptionPane.showMessageDialog(this, "Check-in thành công!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Bạn không có lịch làm việc hôm nay!", "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                // Thực hiện Check-out (Xử lý logic ca gãy/ca đêm)
                String sqlCheckOut = "UPDATE chamcong cc " +
                        "JOIN calam c ON cc.MACALAM = c.MACALAM " +
                        "SET cc.CHECKOUT = CURRENT_TIME, " +
                        "cc.SOGIOLAM = CASE " +
                        "   WHEN CURRENT_TIME >= cc.CHECKIN THEN ROUND(TIME_TO_SEC(TIMEDIFF(CURRENT_TIME, cc.CHECKIN))/3600, 1) "
                        +
                        "   ELSE ROUND((TIME_TO_SEC(TIMEDIFF('23:59:59', cc.CHECKIN)) + TIME_TO_SEC(CURRENT_TIME))/3600, 1) END, "
                        +
                        "cc.TRANGTHAI = CASE " +
                        "   WHEN cc.TRANGTHAI = 'Đi muộn' THEN 'Đi muộn' " +
                        "   WHEN CURRENT_TIME < c.GIOTANCA THEN 'Về sớm' ELSE 'Đúng giờ' END " +
                        "WHERE cc.MANV = ? AND cc.NGAYLAMVIEC = CURRENT_DATE AND cc.CHECKOUT IS NULL";

                try (PreparedStatement ps = conn.prepareStatement(sqlCheckOut)) {
                    ps.setString(1, manv);
                    if (ps.executeUpdate() > 0) {
                        JOptionPane.showMessageDialog(this, "Check-out thành công!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Không tìm thấy lượt Check-in hợp lệ!");
                    }
                }
            }
            if (parent != null)
                parent.refreshData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi CSDL: " + e.getMessage());
        }
    }

    private String fetchTotalDays() {
        String sql = "SELECT COUNT(*) FROM chamcong WHERE MANV = ? AND MONTH(NGAYLAMVIEC) = MONTH(CURRENT_DATE) AND YEAR(NGAYLAMVIEC) = YEAR(CURRENT_DATE)";
        try (Connection conn = JDBCConection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, manv);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getString(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }

    private String fetchStatusCount(String status) {
        String sql = "SELECT COUNT(*) FROM chamcong WHERE MANV = ? AND TRANGTHAI = ? AND MONTH(NGAYLAMVIEC) = MONTH(CURRENT_DATE) AND YEAR(NGAYLAMVIEC) = YEAR(CURRENT_DATE)";
        try (Connection conn = JDBCConection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, manv);
            ps.setString(2, status);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getString(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }

    private String fetchTotalHours() {
        double total = 0;
        String sql = "SELECT SUM(SOGIOLAM) FROM chamcong WHERE MANV = ? AND MONTH(NGAYLAMVIEC) = MONTH(CURRENT_DATE) AND YEAR(NGAYLAMVIEC) = YEAR(CURRENT_DATE)";
        try (Connection conn = JDBCConection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, manv);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                total = rs.getDouble(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.format("%.1f", total);
    }

    private JButton createActionButton(String text, boolean isCheckIn) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(240, 45));
        btn.setBackground(new Color(255, 255, 255, 60));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(255, 255, 255, 100));
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(255, 255, 255, 60));
            }
        });

        btn.addActionListener(e -> handleAttendance(isCheckIn));
        return btn;
    }

    private JPanel StatCard(String title, String value, String unit, Color iconColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(12, 15, 12, 15)));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(Color.GRAY);
        JLabel lblVal = new JLabel((value == null ? "0" : value) + " " + unit);
        lblVal.setFont(new Font("Arial", Font.BOLD, 22));

        JPanel left = new JPanel(new GridLayout(2, 1));
        left.setOpaque(false);
        left.add(lblTitle);
        left.add(lblVal);

        card.add(left, BorderLayout.CENTER);
        return card;
    }
}