package com.hrm.UI.Employee.PayrollEmp;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class PayrollChart extends JPanel {
    private List<Map<String, Object>> data;

    public PayrollChart(List<Map<String, Object>> data) {
        this.data = data;
        setPreferredSize(new Dimension(0, 400));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 25, 10, 25),
            BorderFactory.createLineBorder(new Color(230, 230, 230))
        ));
    }

    @Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (data == null || data.isEmpty()) return;

    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    int padding = 50;
    int width = getWidth() - 2 * padding;
    int height = getHeight() - 2 * padding;
    
    // Tìm giá trị lớn nhất, nếu tất cả là 0 thì mặc định là 1 để tránh lỗi chia cho 0
    double maxSalary = data.stream().mapToDouble(m -> (double)m.get("value")).max().orElse(0);
    if (maxSalary == 0) maxSalary = 1000000; // Giả định mức trần 1tr để vẽ trục
    
    int barWidth = width / (data.size() * 2);
    int xGap = width / data.size();

    for (int i = 0; i < data.size(); i++) {
        double salary = (double) data.get(i).get("value");
        String label = (String) data.get(i).get("label");

        // Tính toán tọa độ (Nếu salary = 0 thì barHeight = 0)
        int barHeight = (int) ((salary / maxSalary) * (height - 20));
        int x = padding + i * xGap + (xGap - barWidth) / 2;
        int y = getHeight() - padding - barHeight;

        // Vẽ cột (Nếu bằng 0 thì vẽ một đường gạch ngang mờ hoặc cột cực thấp)
        if (salary > 0) {
            g2.setColor(new Color(59, 130, 246)); 
            g2.fillRoundRect(x, y, barWidth, barHeight, 8, 8);
        } else {
            g2.setColor(new Color(230, 230, 230)); // Màu xám cho tháng không có dữ liệu
            g2.fillRect(x, getHeight() - padding - 2, barWidth, 2);
        }

        // Vẽ số tiền (0.0M)
        g2.setColor(salary > 0 ? Color.DARK_GRAY : Color.LIGHT_GRAY);
        g2.setFont(new Font("Arial", Font.BOLD, 10));
        String priceTag = String.format("%.1fM", salary / 1000000);
        g2.drawString(priceTag, x + (barWidth - g2.getFontMetrics().stringWidth(priceTag))/2, y - 5);

        // Vẽ nhãn tháng
        g2.setColor(Color.GRAY);
        g2.drawString(label, x + (barWidth - g2.getFontMetrics().stringWidth(label))/2, getHeight() - padding + 20);
    }
    g2.dispose();
}
}