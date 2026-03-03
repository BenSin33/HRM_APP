package com.hrm.UI.Employee.AttendanceEmp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import com.hrm.DAO.Employee.AttendanceDAO;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

public class AttendanceHeader extends JPanel {
    private String manv;
    private AttendanceManage parent;
    private AttendanceDAO attendanceDAO = new AttendanceDAO();
    private JPanel statsPanel;
    private JLabel lblTime;
    private JLabel lblNowDate;
    private Timer clockTimer;

    public AttendanceHeader(String manv, AttendanceManage parent) {
        this.manv = manv;
        this.parent = parent;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));
        setBorder(new EmptyBorder(10, 20, 10, 20));

        // Title Panel
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        JLabel lblMainTitle = new JLabel("Chấm công");
        lblMainTitle.setFont(new Font("Arial", Font.BOLD, 28));
        JLabel lblSubTitle = new JLabel("Quản lý thời gian vào/ra ca làm việc");
        lblSubTitle.setForeground(Color.GRAY);
        titlePanel.add(lblMainTitle);
        titlePanel.add(lblSubTitle);

        // Time Card
        JPanel timeCard = new JPanel(new BorderLayout());
        timeCard.setBackground(new Color(59, 130, 246));
        timeCard.setBorder(new EmptyBorder(25, 20, 25, 20));

        lblTime = new JLabel("", SwingConstants.CENTER);
        lblTime.setFont(new Font("Arial", Font.BOLD, 48));
        lblTime.setForeground(Color.WHITE);

        lblNowDate = new JLabel("", SwingConstants.CENTER);
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

        // Stats Panel
        statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(20, 0, 10, 0));
        refreshStats();

        JPanel topGroup = new JPanel(new BorderLayout());
        topGroup.setOpaque(false);
        topGroup.add(titlePanel, BorderLayout.NORTH);
        topGroup.add(timeCard, BorderLayout.CENTER);

        updateDateTime();
        startClock();

        add(topGroup, BorderLayout.NORTH);
        add(statsPanel, BorderLayout.CENTER);
    }

    private void updateDateTime() {
        LocalDateTime now = LocalDateTime.now();
        String timeStr = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String dateStr = now.format(DateTimeFormatter.ofPattern("EEEE, dd 'tháng' M, yyyy", new Locale("vi", "VN")));
        lblTime.setText(timeStr);
        lblNowDate.setText("Hôm nay " + dateStr);
    }

    private void startClock() {
        if (clockTimer != null && clockTimer.isRunning()) {
            clockTimer.stop();
        }
        clockTimer = new Timer(1000, e -> updateDateTime());
        clockTimer.start();
    }

    @Override
    public void removeNotify() {
        if (clockTimer != null) {
            clockTimer.stop();
        }
        super.removeNotify();
    }

    private void refreshStats() {
        statsPanel.removeAll();
        Map<String, String> stats = attendanceDAO.getMonthlyStats(manv);
        statsPanel.add(createStatCard("Tổng ngày làm", stats.getOrDefault("totalDays", "0"), "", new Color(59, 130, 246)));
        statsPanel.add(createStatCard("Đúng giờ", stats.getOrDefault("onTime", "0"), "", new Color(34, 197, 94)));
        statsPanel.add(createStatCard("Đi muộn/Về sớm", stats.getOrDefault("late", "0"), "", new Color(234, 179, 8)));
        statsPanel.add(createStatCard("Tổng giờ làm", stats.getOrDefault("totalHours", "0.0"), "h", new Color(168, 85, 247)));
        statsPanel.revalidate();
        statsPanel.repaint();
    }

    private void handleAttendance(boolean isCheckIn) {
        try {
            if (isCheckIn) {
                if (attendanceDAO.checkAlreadyCheckedIn(manv)) {
                    JOptionPane.showMessageDialog(this, "Bạn đã check-in hôm nay!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (attendanceDAO.insertCheckIn(manv)) {
                    JOptionPane.showMessageDialog(this, "Check-in thành công!");
                } else {
                    JOptionPane.showMessageDialog(this, "Không có lịch làm việc cho hôm nay!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                if (attendanceDAO.updateCheckOut(manv)) {
                    JOptionPane.showMessageDialog(this, "Check-out thành công!");
                } else {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy lượt Check-in hợp lệ!");
                }
            }
            refreshStats();
            if (parent != null) parent.refreshData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + e.getMessage());
        }
    }

    private JButton createActionButton(String text, boolean isCheckIn) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(220, 45));
        btn.setBackground(new Color(255, 255, 255, 40));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> handleAttendance(isCheckIn));
        return btn;
    }

    private JPanel createStatCard(String title, String value, String unit, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(12, 15, 12, 15)));
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(Color.GRAY);
        JLabel lblVal = new JLabel(value + " " + unit);
        lblVal.setFont(new Font("Arial", Font.BOLD, 22));
        lblVal.setForeground(color);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblVal, BorderLayout.CENTER);
        return card;
    }
}