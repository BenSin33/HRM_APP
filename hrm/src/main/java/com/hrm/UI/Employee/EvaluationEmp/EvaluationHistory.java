package com.hrm.UI.Employee.EvaluationEmp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Hiển thị danh sách lịch sử đánh giá qua các kỳ (Quý/Năm).
 */
public class EvaluationHistory extends JPanel {

    public EvaluationHistory() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        // Đổ bóng nhẹ và bo góc bằng cách sử dụng Border
        setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(10, 25, 10, 25),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(20, 20, 20, 20)
            )
        ));

        // Tiêu đề phần lịch sử
        JLabel lblTitle = new JLabel("Lịch sử đánh giá");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(lblTitle);
        add(Box.createVerticalStrut(15));

        // Thêm các dòng dữ liệu (Sau này sẽ dùng vòng lặp ResultSet từ Database)
        add(createHistoryRow("Kỳ Q4 2024", "Xuất sắc", "95", "10/01/2025", new Color(34, 197, 94)));
        add(createDivider());
        add(createHistoryRow("Kỳ Q3 2024", "Tốt", "88", "10/10/2024", new Color(59, 130, 246)));
        add(createDivider());
        add(createHistoryRow("Kỳ Q2 2024", "Khá", "82", "05/07/2024", new Color(251, 191, 36)));
    }

    /**
     * Tạo một dòng hiển thị thông tin đánh giá cũ
     */
    private JPanel createHistoryRow(String period, String rank, String score, String date, Color rankColor) {
        JPanel row = new JPanel(new BorderLayout(20, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        // Bên trái: Kỳ đánh giá và Ngày
        JPanel left = new JPanel(new GridLayout(2, 1));
        left.setOpaque(false);
        JLabel lblPeriod = new JLabel(period);
        lblPeriod.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel lblDate = new JLabel("Ngày đánh giá: " + date);
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDate.setForeground(Color.GRAY);
        left.add(lblPeriod);
        left.add(lblDate);

        // Bên phải: Điểm số và Xếp loại
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        right.setOpaque(false);
        
        JLabel lblScore = new JLabel(score + " điểm");
        lblScore.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JLabel lblRank = new JLabel(rank);
        lblRank.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRank.setForeground(rankColor);
        lblRank.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(rankColor, 1),
            new EmptyBorder(2, 8, 2, 8)
        ));

        right.add(lblScore);
        right.add(lblRank);

        row.add(left, BorderLayout.WEST);
        row.add(right, BorderLayout.EAST);
        
        return row;
    }

    private JComponent createDivider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(240, 240, 240));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }
}