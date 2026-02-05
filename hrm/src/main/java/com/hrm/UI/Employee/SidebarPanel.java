package com.hrm.UI.Employee;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class SidebarPanel extends JPanel {
    private JPanel menuPanel;
    
    public SidebarPanel() {
        setBackground(new Color(28, 71, 235)); 
        setPreferredSize(new Dimension(260, 0));
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 0, 30, 0));

        // --- PHẦN TRÊN: Logo và Menu ---
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Logo
        JLabel lblLogo = new JLabel("HRM System");
        lblLogo.setFont(new Font("Inter", Font.BOLD, 22));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 40, 0);
        topPanel.add(lblLogo, gbc);

        // Menu Panel
        menuPanel = new JPanel();
        menuPanel.setOpaque(false);
        menuPanel.setLayout(new GridBagLayout());
        GridBagConstraints menuGbc = new GridBagConstraints();
        menuGbc.gridx = 0;
        menuGbc.weightx = 1.0;
        menuGbc.fill = GridBagConstraints.NONE;
        menuGbc.anchor = GridBagConstraints.CENTER;

        // Các nút Menu
        String[] menuTexts = { 
            "Tổng quan",
            "Hồ sơ cá nhân", 
            "Chấm công", 
            "Bảng lương", 
            "Nghỉ phép", 
            "Đánh giá"
        };
        
        for (int i = 0; i < menuTexts.length; i++) {
            JLabel tab = createMenuTab(menuTexts[i], i == 0);
            menuGbc.gridy = i;
            menuGbc.insets = new Insets(0,0, 6, 0);
            menuPanel.add(tab, menuGbc);
        }

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        topPanel.add(menuPanel, gbc);
        
        add(topPanel, BorderLayout.CENTER);

        // --- PHẦN DƯỚI: Thông tin User và Đăng xuất ---
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new GridBagLayout());
        GridBagConstraints bottomGbc = new GridBagConstraints();
        bottomGbc.gridx = 0;
        bottomGbc.weightx = 1.0;
        bottomGbc.fill = GridBagConstraints.HORIZONTAL;

        // Divider
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 100));
        bottomGbc.gridy = 0;
        bottomGbc.insets = new Insets(0, 0, 15, 0);
        bottomPanel.add(sep, bottomGbc);

        // User Info
        JLabel lblUser = new JLabel("<html><body style='color:white;font-family:Inter;'><b>Lê Văn Employee</b><br><font size='2'>Nhân viên (NV001)</font></body></html>");
        bottomGbc.gridy = 1;
        bottomGbc.insets = new Insets(15, 0, 15, 0);
        bottomPanel.add(lblUser, bottomGbc);

        // Logout Button
        JLabel logout = createMenuTab("🚪 Đăng xuất", false);
        bottomGbc.gridy = 2;
        bottomGbc.insets = new Insets(0, 0, 0, 0);
        bottomPanel.add(logout, bottomGbc);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }


    private JLabel createMenuTab(String text, boolean isActive) {
        boolean[] hovered = {false};
        
        JLabel lbl = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (isActive) {
                    g2.setColor(new Color(255, 255, 255, 25));
                    g2.fill(new RoundRectangle2D.Double(5, 5, getWidth() - 10, getHeight() - 10, 10, 10));
                    
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(3));
                    g2.drawLine(5, 10, 5, getHeight() - 10);
                } else if (hovered[0]) {
                    g2.setColor(new Color(255, 255, 255, 12));
                    g2.fill(new RoundRectangle2D.Double(5, 5, getWidth() - 10, getHeight() - 10, 10, 10));
                }
                
                super.paintComponent(g);
            }
        };
        
        lbl.setFont(new Font("Inter", isActive ? Font.BOLD : Font.PLAIN, 18));
        lbl.setForeground(Color.WHITE);
        lbl.setOpaque(true);
        lbl.setBackground(new Color(0, 0, 0, 0));
        lbl.setPreferredSize(new Dimension(220, 45));
        lbl.setBorder(null);
        lbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setVerticalAlignment(SwingConstants.CENTER);
        
        lbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered[0] = true;
                lbl.setFont(new Font("Inter", Font.BOLD, 18));
                lbl.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered[0] = false;
                lbl.setFont(new Font("Inter", isActive ? Font.BOLD : Font.PLAIN, 18));
                lbl.repaint();
            }
        });
        
        return lbl;
    }
}