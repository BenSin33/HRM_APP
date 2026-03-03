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
    
    private boolean isCollapsed = false;
    private final int EXPANDED_WIDTH = 250;
    private final int COLLAPSED_WIDTH = 70;
    private final int ANIMATION_DURATION = 300;
    
    private JPanel menuContainer;
    private JPanel sidebarPanel;
    private JScrollPane scrollPane;

    public Sidebar(JPanel contentPanel, CardLayout cardLayout, List<SidebarTab> tabsList) {
        this.contentPanel = contentPanel;
        this.cardLayout = cardLayout;

        this.setBackground(sidebarColor);
        this.setLayout(new BorderLayout());

        // 1. Nút điều khiển thu phóng
        JButton toggleBtn = new JButton("<<");
        toggleBtn.setBackground(new Color(128, 0, 255));
        toggleBtn.setForeground(Color.WHITE);
        toggleBtn.setFocusPainted(false);
        toggleBtn.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        toggleBtn.setFont(new Font("Arial", Font.BOLD, 14));
        toggleBtn.addActionListener(e -> toggleSidebarSmooth(toggleBtn));
        this.add(toggleBtn, BorderLayout.NORTH);

        // 2. Sidebar panel chứa content
        sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setBackground(sidebarColor);
        
        // 3. Container chứa Menu
        menuContainer = new JPanel();
        menuContainer.setBackground(sidebarColor);
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        renderMenu(tabsList);

        // 4. JScrollPane
        scrollPane = new JScrollPane(menuContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        sidebarPanel.add(scrollPane, BorderLayout.CENTER);
        this.add(sidebarPanel, BorderLayout.CENTER);
        
        this.setPreferredSize(new Dimension(EXPANDED_WIDTH, 0));
    }

    private void toggleSidebarSmooth(JButton btn) {
        isCollapsed = !isCollapsed;
        int targetWidth = isCollapsed ? COLLAPSED_WIDTH : EXPANDED_WIDTH;
        btn.setText(isCollapsed ? ">>" : "<<");
        
        // Animate width change
        new Thread(() -> {
            int currentWidth = isCollapsed ? EXPANDED_WIDTH : COLLAPSED_WIDTH;
            long startTime = System.currentTimeMillis();
            
            while (true) {
                long elapsed = System.currentTimeMillis() - startTime;
                if (elapsed >= ANIMATION_DURATION) {
                    SwingUtilities.invokeLater(() -> {
                        setPreferredSize(new Dimension(targetWidth, 0));
                        getParent().revalidate();
                        getParent().repaint();
                    });
                    break;
                }
                
                float progress = (float) elapsed / ANIMATION_DURATION;
                int newWidth = (int) (currentWidth + (targetWidth - currentWidth) * progress);
                
                SwingUtilities.invokeLater(() -> {
                    setPreferredSize(new Dimension(newWidth, 0));
                    getParent().revalidate();
                    getParent().repaint();
                });
                
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        
        // Update button visibility
        for (Component c : menuContainer.getComponents()) {
            if (c instanceof JButton) {
                JButton menuBtn = (JButton) c;
                menuBtn.setToolTipText(isCollapsed ? menuBtn.getText() : null);
            }
        }
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