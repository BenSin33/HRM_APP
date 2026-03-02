package com.hrm.UI.HR.Overview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class DashboardOverview extends JPanel {

    public DashboardOverview() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(createMainContent(), BorderLayout.CENTER);
    }

    // ================= MAIN =================
    private JPanel createMainContent() {
        JPanel main = new JPanel(new BorderLayout());
        main.setOpaque(false);

        // Header
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Tổng quan");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JLabel sub = new JLabel("Xin chào! Đây là bảng điều khiển của bạn.");
        sub.setForeground(Color.DARK_GRAY);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        header.add(title);
        header.add(Box.createVerticalStrut(5));
        header.add(sub);

        // Cards
        JPanel cards = new JPanel(new GridLayout(1, 4, 20, 0));
        cards.setOpaque(false);
        cards.setBorder(new EmptyBorder(20, 0, 20, 0));

        cards.add(createStatCard("Tổng nhân viên", "156", new Color(59, 130, 246)));
        cards.add(createStatCard("Đang làm việc", "142", new Color(34, 197, 94)));
        cards.add(createStatCard("Nghỉ phép hôm nay", "8", new Color(234, 179, 8)));
        cards.add(createStatCard("Tổng lương tháng", "2.4 tỷ", new Color(168, 85, 247)));

        // Bottom panels
        JPanel bottom = new JPanel(new GridLayout(1, 3, 20, 0));
        bottom.setOpaque(false);

        bottom.add(createActivityPanel());
        bottom.add(createTaskPanel());
        bottom.add(createDepartmentPanel());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(cards, BorderLayout.NORTH);
        wrapper.add(bottom, BorderLayout.CENTER);

        main.add(header, BorderLayout.NORTH);
        main.add(wrapper, BorderLayout.CENTER);

        return main;
    }

    // ================= STAT CARD =================
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220,220,220)),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel lbValue = new JLabel(value);
        lbValue.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lbValue.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lbTitle = new JLabel(title);
        lbTitle.setForeground(Color.DARK_GRAY);
        lbTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel colorBar = new JPanel();
        colorBar.setBackground(color);
        colorBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 6));

        card.add(lbValue);
        card.add(Box.createVerticalStrut(8));
        card.add(lbTitle);
        card.add(Box.createVerticalStrut(12));
        card.add(colorBar);

        return card;
    }

    // ================= ACTIVITY =================
    private JPanel createActivityPanel() {
        WhitePanel wp = createWhitePanel("Hoạt động gần đây");
        JPanel content = wp.content;

        content.add(makeBullet("Nguyễn Văn A đã được tuyển dụng vào phòng IT"));
        content.add(makeBullet("Trần Thị B đã gửi đơn xin nghỉ phép"));
        content.add(makeBullet("Đã chốt bảng lương tháng 12"));
        content.add(makeBullet("Hoàn thành đánh giá quý 4"));

        return wp.wrapper;
    }

    // ================= TASK =================
    private JPanel createTaskPanel() {
        WhitePanel wp = createWhitePanel("Công việc cần xử lý");
        JPanel content = wp.content;

        content.add(makeTask("Duyệt 5 đơn xin nghỉ phép"));
        content.add(makeTask("Chốt bảng lương tháng 1"));
        content.add(makeTask("Tạo đợt đánh giá Q1 2025"));
        content.add(makeTask("Cập nhật thông tin hợp đồng"));

        return wp.wrapper;
    }

    private JCheckBox makeTask(String text) {
        JCheckBox cb = new JCheckBox(text);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cb.setForeground(Color.BLACK); // chữ đen rõ ràng
        cb.setOpaque(false);           // nền trong suốt
        cb.setBorder(new EmptyBorder(3, 0, 3, 0));
        return cb;
    }

    // ================= DEPARTMENT =================
    private JPanel createDepartmentPanel() {
        WhitePanel wp = createWhitePanel("Tổng quan phòng ban");
        JPanel content = wp.content;

        content.add(makeBullet("Phòng IT: 45"));
        content.add(makeBullet("Phòng Kinh doanh: 62"));
        content.add(makeBullet("Phòng Kế toán: 28"));

        return wp.wrapper;
    }

    // ===== helper class =====
    private static class WhitePanel {
        JPanel wrapper;
        JPanel content;
    }

    private WhitePanel createWhitePanel(String title) {
        WhitePanel wp = new WhitePanel();

        wp.wrapper = new JPanel(new BorderLayout());
        wp.wrapper.setBackground(Color.WHITE);
        wp.wrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220,220,220)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lbTitle = new JLabel(title);
        lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));

        wp.content = new JPanel();
        wp.content.setOpaque(false);
        wp.content.setLayout(new BoxLayout(wp.content, BoxLayout.Y_AXIS));
        wp.content.setBorder(new EmptyBorder(10, 0, 0, 0));

        wp.wrapper.add(lbTitle, BorderLayout.NORTH);
        wp.wrapper.add(wp.content, BorderLayout.CENTER);

        return wp;
    }

    private JLabel makeBullet(String text) {
        JLabel label = new JLabel("• " + text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(Color.BLACK);
        label.setBorder(new EmptyBorder(3, 0, 3, 0));
        return label;
    }

    
}