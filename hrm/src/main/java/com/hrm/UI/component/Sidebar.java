package com.hrm.UI.component;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.hrm.UI.LoginUI;
import com.hrm.utils.IconResize;
import com.hrm.utils.SidebarIcon;

public class Sidebar extends JPanel {

    private JPanel contentPanel;
    private CardLayout cardLayout;
    private Color sidebarColor = new Color(102, 0, 204);

    private boolean isCollapsed = false;
    private final int EXPANDED_WIDTH = 280;
    private final int COLLAPSED_WIDTH = 70;
    private final int ANIMATION_DURATION = 300;
    
    private JPanel menuContainer;
    private JPanel sidebarPanel;
    private JScrollPane scrollPane;
    private JPanel currentSelectedTab = null;
    private JPanel currentHoveredTab = null;
    private JPanel firstMenuTab = null;
    private List<SidebarTab> tabsList;
    private Map<String, JPanel> tabLabelMap = new HashMap<>();

    public Sidebar(JPanel contentPanel, CardLayout cardLayout, List<SidebarTab> tabsList) {
        this.contentPanel = contentPanel;
        this.cardLayout = cardLayout;
        this.tabsList = tabsList;

        this.setBackground(sidebarColor);
        this.setLayout(new BorderLayout());

        // 1. Nút điều khiển thu phóng
        JButton toggleBtn = new JButton("\u25C0");
        toggleBtn.setBackground(new Color(128, 0, 255));
        toggleBtn.setForeground(Color.WHITE);
        toggleBtn.setFocusPainted(false);
        toggleBtn.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        toggleBtn.setFont(new Font("Arial", Font.BOLD, 14));
        toggleBtn.addActionListener(e -> {
            toggleSidebarSmooth(toggleBtn);
            toggleBtn.setText(isCollapsed ? "\u25C0" : "\u25B6");
        });
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
        
        // Tự động chọn tab đầu tiên (Tổng quan) khi mới đăng nhập
        selectFirstTab();
    }

    private void toggleSidebarSmooth(JButton btn) {
        isCollapsed = !isCollapsed;
        int targetWidth = isCollapsed ? COLLAPSED_WIDTH : EXPANDED_WIDTH;
        btn.setText(isCollapsed ? "\u25B6" : "\u25C0");
        
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
            if (c instanceof JPanel) {
                JPanel menuPanel = (JPanel) c;
                menuPanel.setToolTipText(isCollapsed ? getTabTitleFromPanel(menuPanel) : null);
            }
        }
    }

    private String getTabTitleFromPanel(JPanel panel) {
        for (Component c : panel.getComponents()) {
            if (c instanceof JLabel && c.getFont() != null && c.getFont().isBold()) {
                return ((JLabel) c).getText();
            }
        }
        return null;
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
        for (int i = 0; i < tabsLists.size(); i++) {
            SidebarTab tab = tabsLists.get(i);
            JPanel menuPanel = createMenuLabel(tab);
            menuContainer.add(menuPanel);
            tabLabelMap.put(tab.getCardName(), menuPanel);
            
            // Thêm đường ngăn cách giữa các tab (trừ tab cuối)
            if (i < tabsLists.size() - 1) {
                JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
                separator.setMaximumSize(new Dimension(EXPANDED_WIDTH - 20, 1));
                separator.setForeground(Color.WHITE);
                menuContainer.add(separator);
            }
            
            // Lưu tab đầu tiên (không phải LOGOUT)
            if (i == 0 && firstMenuTab == null && !"LOGOUT".equals(tab.getCardName())) {
                firstMenuTab = menuPanel;
            }
        }
    }

    private JPanel createMenuLabel(SidebarTab tab) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(EXPANDED_WIDTH - 10, 40));
        panel.setPreferredSize(new Dimension(EXPANDED_WIDTH - 10, 40));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        ImageIcon icon = SidebarIcon.getIcon(tab.getCardName());
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setPreferredSize(new Dimension(32, 32));
        iconLabel.setMaximumSize(new Dimension(32, 32));

        JLabel textLabel = new JLabel(tab.getTitle());
        textLabel.setForeground(Color.WHITE);
        textLabel.setFont(new Font("Arial", Font.BOLD, 13));
        textLabel.setPreferredSize(new Dimension(200, 40));
        textLabel.setMaximumSize(new Dimension(200, 40));

        panel.add(iconLabel);
        panel.add(Box.createHorizontalStrut(12));
        panel.add(textLabel);
        panel.add(Box.createHorizontalGlue());

        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if ("LOGOUT".equals(tab.getCardName())) {
                    int confirm = JOptionPane.showConfirmDialog(
                        SwingUtilities.getWindowAncestor(Sidebar.this),
                        "Bạn có chắc chắn muốn đăng xuất không?",
                        "Xác nhận đăng xuất",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        java.awt.Window window = SwingUtilities.getWindowAncestor(Sidebar.this);
                        if (window != null) {
                            window.dispose();
                            new LoginUI().setVisible(true);
                        }
                    }
                } else {
                    cardLayout.show(contentPanel, tab.getCardName());
                    selectTab(panel);
                }
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (panel != currentSelectedTab) {
                    if (currentHoveredTab != null && currentHoveredTab != currentSelectedTab) {
                        currentHoveredTab.setOpaque(false);
                        currentHoveredTab.repaint();
                    }
                    panel.setOpaque(true);
                    panel.setBackground(new Color(180, 100, 255));
                    panel.repaint();
                    currentHoveredTab = panel;
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (panel != currentSelectedTab && panel == currentHoveredTab) {
                    panel.setOpaque(false);
                    panel.setBackground(sidebarColor);
                    panel.repaint();
                    currentHoveredTab = null;
                }
            }
        });

        return panel;
    }

    private void selectTab(JPanel panel) {
        for (Component c : menuContainer.getComponents()) {
            if (c instanceof JPanel && c != panel) {
                JPanel otherPanel = (JPanel) c;
                otherPanel.setOpaque(false);
                otherPanel.repaint();
            }
        }
        currentSelectedTab = panel;
        currentHoveredTab = null;
        panel.setOpaque(true);
        panel.setBackground(new Color(180, 100, 255));
        panel.repaint();
    }

    private void selectFirstTab() {
        if (firstMenuTab != null && !tabsList.isEmpty()) {
            selectTab(firstMenuTab);
            cardLayout.show(contentPanel, tabsList.get(0).getCardName());
        }
    }

    public void selectTabByCardName(String cardName) {
        JPanel panel = tabLabelMap.get(cardName);
        if (panel != null) {
            cardLayout.show(contentPanel, cardName);
            selectTab(panel);
        }
    }
}