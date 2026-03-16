package com.hrm.UI.Employee.EvaluationEmp;

import com.hrm.DAO.Employee.EvaluationDAO;
import com.hrm.DTO.Employee.EvaluationHistoryDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Locale;

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

        EvaluationDAO evaluationDAO = new EvaluationDAO();
        List<EvaluationHistoryDTO> histories = evaluationDAO.getEvaluationHistory(manv);
        EvaluationHistoryDTO latest = histories.isEmpty() ? null : histories.get(0);

        int latestScore = latest == null ? 0 : latest.getTongDiem();
        String latestRank = getRank(latestScore);
        String latestPeriod = latest == null ? "-" : latest.getMaDot();
        String latestNhanXet = latest == null ? null : latest.getNhanXet();
        String latestQuyetDinh = latest == null ? null : latest.getQuyetDinh();

        // Thêm các thành phần chi tiết (Dữ liệu mẫu - Sau này lấy từ DB)
        // Thành phần 1: Tóm tắt điểm số mới nhất (Xanh dương)
        mainContent.add(new EvaluationSummary(latestScore, latestRank, latestPeriod));
        
        // Thành phần 2: Các chỉ số trung bình (Thẻ nhỏ)
        mainContent.add(createMiniStatsRow(histories));
        
        // Thành phần 3: Nhận xét chi tiết từ quản lý
        mainContent.add(new EvaluationDetail(latestNhanXet));
        
        // Thành phần 4: Lịch sử đánh giá các kỳ trước
        mainContent.add(new EvaluationHistory(histories));
        
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
    private JPanel createMiniStatsRow(List<EvaluationHistoryDTO> histories) {
        JPanel row = new JPanel(new GridLayout(1, 3, 20, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(10, 25, 10, 25));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        double avgScore = 0;
        for (EvaluationHistoryDTO history : histories) {
            avgScore += history.getTongDiem();
        }
        avgScore = histories.isEmpty() ? 0 : avgScore / histories.size();

        String avgDisplay = String.format(Locale.US, "%.1f", avgScore);
        String countDisplay = String.valueOf(histories.size());

        int trend = 0;
        if (histories.size() >= 2) {
            trend = histories.get(0).getTongDiem() - histories.get(1).getTongDiem();
        }

        String trendDisplay = (trend > 0 ? "+" : "") + trend;
        Color trendColor = trend > 0 ? new Color(34, 197, 94) : (trend < 0 ? new Color(239, 68, 68) : new Color(59, 130, 246));

        row.add(createMiniCard("Điểm trung bình", avgDisplay, "", createStarIcon(new Color(251, 191, 36), 16), new Color(251, 191, 36)));
        row.add(createMiniCard("Số lần đánh giá", countDisplay, "", createTrendIcon(true, new Color(34, 197, 94), 16), new Color(34, 197, 94)));
        row.add(createMiniCard("Xu hướng", trendDisplay, "vs kỳ trước", createTrendIcon(trend >= 0, trendColor, 16), trendColor));

        return row;
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

    private JPanel createMiniCard(String title, String val, String sub, ImageIcon icon, Color color) {
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

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setAlignmentX(Component.RIGHT_ALIGNMENT);
        right.add(lblIcon);

        if (sub != null && !sub.isBlank()) {
            right.add(Box.createVerticalStrut(4));
            JLabel lblSub = new JLabel(sub);
            lblSub.setForeground(color);
            lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblSub.setAlignmentX(Component.RIGHT_ALIGNMENT);
            right.add(lblSub);
        }

        card.add(lblT, BorderLayout.NORTH);
        card.add(lblV, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);
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

        JLabel title = new JLabel("Gợi ý cải thiện hiệu suất", createBulbIcon(new Color(30, 64, 175), 16), SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(new Color(30, 64, 175));
        title.setIconTextGap(8);
        
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

    private ImageIcon createStarIcon(Color color, int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);

        int cx = size / 2;
        int cy = size / 2;
        int outer = Math.max(4, size / 2 - 1);
        int inner = Math.max(2, outer / 2);

        Polygon star = new Polygon();
        for (int i = 0; i < 10; i++) {
            double angle = Math.toRadians(-90 + i * 36);
            int radius = (i % 2 == 0) ? outer : inner;
            int x = cx + (int) (Math.cos(angle) * radius);
            int y = cy + (int) (Math.sin(angle) * radius);
            star.addPoint(x, y);
        }

        g2d.fillPolygon(star);
        g2d.dispose();
        return new ImageIcon(image);
    }

    private ImageIcon createTrendIcon(boolean up, Color color, int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        int pad = 3;
        int startY = up ? size - pad : pad;
        int endY = up ? pad : size - pad;

        g2d.drawLine(pad, startY, size - pad - 3, endY + (up ? 0 : 0));

        if (up) {
            g2d.drawLine(size - pad - 3, endY, size - pad - 7, endY + 4);
            g2d.drawLine(size - pad - 3, endY, size - pad - 7, endY - 4);
        } else {
            g2d.drawLine(size - pad - 3, endY, size - pad - 7, endY + 4);
            g2d.drawLine(size - pad - 3, endY, size - pad - 7, endY - 4);
        }

        g2d.dispose();
        return new ImageIcon(image);
    }

    private ImageIcon createBulbIcon(Color color, int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);

        int bulbW = size - 6;
        int bulbH = size - 8;
        g2d.fillOval(3, 1, bulbW, bulbH);
        g2d.fillRoundRect(size / 2 - 3, size - 6, 6, 4, 2, 2);

        g2d.dispose();
        return new ImageIcon(image);
    }
}