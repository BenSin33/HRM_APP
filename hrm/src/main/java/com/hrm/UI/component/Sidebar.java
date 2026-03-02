package com.hrm.UI.component;

import java.awt.*;
import java.net.URL;
import java.util.List;
import javax.swing.*;
import com.hrm.UI.LoginUI;
import com.hrm.utils.*;

public class Sidebar extends JPanel {

    private JPanel contentPanel;
    private CardLayout cardLayout;
    private Color sidebarColor = new Color(102, 0, 204);
    
    private boolean isCollapsed = false; // Trạng thái thu gọn
    private final int EXPANDED_WIDTH = 250;
    private final int COLLAPSED_WIDTH = 70;
    
    private JPanel menuContainer; // Panel chứa các nút để bỏ vào ScrollPane

    public Sidebar(JPanel contentPanel, CardLayout cardLayout, List<SidebarTab> tabsList) {
        this.contentPanel = contentPanel;
        this.cardLayout = cardLayout;

        // Thiết lập layout chính cho Sidebar là BorderLayout để chứa ScrollPane
        this.setPreferredSize(new Dimension(EXPANDED_WIDTH, 0));
        this.setBackground(sidebarColor);
        this.setLayout(new BorderLayout());

        // 1. Nút điều khiển thu phóng (Toggle Button)
        JButton toggleBtn = new JButton("<<");
        toggleBtn.setBackground(new Color(128, 0, 255));
        toggleBtn.setForeground(Color.WHITE);
        toggleBtn.setFocusPainted(false);
        toggleBtn.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        toggleBtn.addActionListener(e -> toggleSidebar(toggleBtn));
        this.add(toggleBtn, BorderLayout.NORTH);

        // 2. Container chứa Menu (Sử dụng BoxLayout)
        menuContainer = new JPanel();
        menuContainer.setBackground(sidebarColor);
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        renderMenu(tabsList);

        // 3. JScrollPane để kéo xuống khi menu dài
        JScrollPane scrollPane = new JScrollPane(menuContainer);
        scrollPane.setBorder(null); // Xóa viền
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Cuộn mượt hơn
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); // Tắt cuộn ngang
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        this.add(scrollPane, BorderLayout.CENTER);
    }

    private void toggleSidebar(JButton btn) {
        isCollapsed = !isCollapsed;
        if (isCollapsed) {
            this.setPreferredSize(new Dimension(COLLAPSED_WIDTH, 0));
            btn.setText(">>");
        } else {
            this.setPreferredSize(new Dimension(EXPANDED_WIDTH, 0));
            btn.setText("<<");
        }
        
        // Cập nhật lại giao diện các nút bên trong
        for (Component c : menuContainer.getComponents()) {
            if (c instanceof JButton) {
                JButton menuBtn = (JButton) c;
                // Nếu thu gọn thì ẩn chữ, chỉ để lại icon (nếu có) hoặc chữ cái đầu
                menuBtn.setToolTipText(isCollapsed ? menuBtn.getText() : null);
            }
        }

        this.revalidate();
        this.repaint();
    }

    private void renderMenu(List<SidebarTab> tabsLists) {
        menuContainer.add(Box.createRigidArea(new Dimension(0, 10)));

        // Logo
        try {
            URL url = getClass().getResource("/icons/HRM_Logo.png");
            ImageIcon logoIcon = (url != null) ? new ImageIcon(url) : new ImageIcon();
            JLabel logo = new JLabel(IconResize.resizeIcon(logoIcon, 120, 120));
            logo.setAlignmentX(CENTER_ALIGNMENT);
            menuContainer.add(logo);
            menuContainer.add(Box.createRigidArea(new Dimension(0, 20)));
        } catch (Exception e) {
            System.err.println("Lỗi icon: " + e.getMessage());
        }

        // Render các nút
        for (SidebarTab tab : tabsLists) {
            JButton menuButton = createMenuButton(tab);
            menuContainer.add(menuButton);
            menuContainer.add(Box.createRigidArea(new Dimension(0, 10)));
        }
    }

    private JButton createMenuButton(SidebarTab tab) {
        JButton button = new JButton(tab.getTitle());
        button.setAlignmentX(CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(220, 45));
        button.setFocusPainted(false);
        button.setBackground(new Color(153, 51, 255));
        button.setForeground(Color.WHITE);

        button.addActionListener(e -> {
            if ("LOGOUT".equals(tab.getCardName())) {
                java.awt.Window window = SwingUtilities.getWindowAncestor(this);
                if (window != null) {
                    window.dispose();
                    new LoginUI().setVisible(true);
                }
            } else {
                cardLayout.show(contentPanel, tab.getCardName());
            }
        });

        return button;
    }
}