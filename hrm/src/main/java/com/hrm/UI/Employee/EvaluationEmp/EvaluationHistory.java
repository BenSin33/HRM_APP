package com.hrm.UI.Employee.EvaluationEmp;

import com.hrm.DAO.Employee.EvaluationDAO;
import com.hrm.DTO.Employee.EvaluationHistoryDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Hiển thị danh sách lịch sử đánh giá qua các kỳ (Quý/Năm).
 */
public class EvaluationHistory extends JPanel {

    public EvaluationHistory(String manv) {
        this(new EvaluationDAO().getEvaluationHistory(manv));
    }

    public EvaluationHistory(List<EvaluationHistoryDTO> histories) {
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

        loadData(histories);
    }

    private void loadData(List<EvaluationHistoryDTO> histories) {

        if (histories.isEmpty()) {
            JLabel lblEmpty = new JLabel("Chưa có dữ liệu đánh giá");
            lblEmpty.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            lblEmpty.setForeground(Color.GRAY);
            lblEmpty.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(lblEmpty);
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (int i = 0; i < histories.size(); i++) {
            EvaluationHistoryDTO item = histories.get(i);

            String period = item.getMaDot() == null || item.getMaDot().isBlank()
                    ? "Kỳ chưa xác định"
                    : "Kỳ " + item.getMaDot();

            String score = String.valueOf(item.getTongDiem());
            String rank = getRank(item.getTongDiem());
            Color rankColor = getRankColor(rank);

            String date = item.getNgayDanhGia() == null
                    ? "-"
                    : item.getNgayDanhGia().toLocalDate().format(formatter);

            add(createHistoryRow(period, rank, score, date, rankColor));
            if (i < histories.size() - 1) {
                add(createDivider());
            }
        }
    }

    private String getRank(int score) {
        if (score >= 90) {
            return "Xuất sắc";
        }
        if (score >= 80) {
            return "Tốt";
        }
        if (score >= 65) {
            return "Khá";
        }
        if (score >= 50) {
            return "Trung bình";
        }
        return "Kém";
    }

    private Color getRankColor(String rank) {
        switch (rank) {
            case "Xuất sắc":
                return new Color(34, 197, 94);
            case "Tốt":
                return new Color(59, 130, 246);
            case "Khá":
                return new Color(251, 191, 36);
            case "Trung bình":
                return new Color(249, 115, 22);
            default:
                return new Color(239, 68, 68);
        }
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