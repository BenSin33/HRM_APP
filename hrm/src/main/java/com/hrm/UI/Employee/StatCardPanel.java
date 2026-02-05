package com.hrm.UI.Employee;


import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class StatCardPanel extends JPanel {
    private String title;
    private String value;
    private Color accentColor;

    public StatCardPanel(String title, String value, Color accentColor) {
        this.title = title;
        this.value = value;
        this.accentColor = accentColor;
        
        setOpaque(false); // Quan trọng: Để thấy được góc bo
        setPreferredSize(new Dimension(220, 120));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Tiêu đề nhỏ phía trên
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Inter", Font.PLAIN, 13));
        lblTitle.setForeground(new Color(120, 120, 120));

        // Giá trị lớn ở giữa
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Inter", Font.BOLD, 22));
        lblValue.setForeground(new Color(33, 33, 33));

        add(lblTitle, BorderLayout.NORTH);
        add(lblValue, BorderLayout.CENTER);
        
        // Bạn có thể thêm một thanh màu nhỏ bên cạnh để trang trí giống Figma
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Vẽ nền trắng bo góc
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
        
        // Vẽ đường viền rất nhạt
        g2.setColor(new Color(230, 230, 230));
        g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 20, 20));
        
        g2.dispose();
        super.paintComponent(g);
    }
}