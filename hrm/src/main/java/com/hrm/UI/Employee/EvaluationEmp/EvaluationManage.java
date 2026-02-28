package com.hrm.UI.Employee.EvaluationEmp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Lớp quản lý chính cho tab Đánh giá hiệu suất.
 * Hiển thị tổng quan điểm, chi tiết nhận xét và lịch sử đánh giá qua các kỳ.
 */
public class EvaluationManage extends JPanel {
    private String manv;
    private JPanel mainContent;

    public EvaluationManage(String manv) {
        this.manv = manv;
        initUI();
    }

    private void initUI() {
        // Thiết lập layout chính
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250)); // Màu xám nhạt đồng bộ

        // 1. Tiêu đề trang (Header đơn giản)
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(15, 25, 10, 25));
        
        JLabel lblTitle = new JLabel("Đánh giá hiệu suất");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        JLabel lblSub = new JLabel("Xem kết quả và phản hồi đánh giá định kỳ của bạn");
        lblSub.setForeground(Color.GRAY);
        
        headerPanel.add(lblTitle);
        headerPanel.add(lblSub);
        add(headerPanel, BorderLayout.NORTH);

        // 2. Tạo Container chứa tất cả nội dung đánh giá
        mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(new Color(248, 249, 250));

        // Thêm các thành phần chi tiết (Dữ liệu mẫu - Sau này lấy từ DB)
        // Thành phần 1: Tóm tắt điểm số mới nhất (Xanh dương)
        mainContent.add(new EvaluationSummary(95, "Xuất sắc"));
        
        // Thành phần 2: Các chỉ số trung bình (Thẻ nhỏ)
        mainContent.add(createMiniStatsRow());
        
        // Thành phần 3: Nhận xét chi tiết từ quản lý
        mainContent.add(new EvaluationDetail());
        
        // Thành phần 4: Lịch sử đánh giá các kỳ trước
        mainContent.add(new EvaluationHistory());
        
        // Thành phần 5: Gợi ý cải thiện (Footer của nội dung)
        mainContent.add(createSuggestionPanel());

        // 3. Đưa tất cả vào JScrollPane để có thể cuộn xuống xem lịch sử
        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(new Color(248, 249, 250));

        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Tạo hàng chứa các thông số thống kê nhỏ (Điểm TB, Số lần đánh giá, Xu hướng)
     */
    private JPanel createMiniStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, 20, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(10, 25, 10, 25));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        row.add(createMiniCard("Điểm trung bình", "89.3", "★", new Color(251, 191, 36)));
        row.add(createMiniCard("Số lần đánh giá", "3", "↗", new Color(34, 197, 94)));
        row.add(createMiniCard("Xu hướng", "+7", "vs Q3", new Color(59, 130, 246)));

        return row;
    }

    private JPanel createMiniCard(String title, String val, String sub, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel lblT = new JLabel(title);
        lblT.setForeground(Color.GRAY);
        JLabel lblV = new JLabel(val);
        lblV.setFont(new Font("Segoe UI", Font.BOLD, 22));
        JLabel lblS = new JLabel(sub);
        lblS.setForeground(color);

        card.add(lblT, BorderLayout.NORTH);
        card.add(lblV, BorderLayout.CENTER);
        card.add(lblS, BorderLayout.EAST);
        return card;
    }

    private JPanel createSuggestionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(239, 246, 255)); // Màu xanh dương cực nhạt
        panel.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(10, 25, 30, 25),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(191, 219, 254)),
                new EmptyBorder(15, 20, 15, 20)
            )
        ));

        JLabel title = new JLabel("💡 Gợi ý cải thiện hiệu suất");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(new Color(30, 64, 175));
        
        JLabel content = new JLabel("<html>• Tham gia các khóa học chuyên môn<br>• Tăng cường tương tác trong các buổi họp team</html>");
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    public void refreshData() {
        removeAll();
        initUI();
        revalidate();
        repaint();
    }
}