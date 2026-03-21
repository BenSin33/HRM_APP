package com.hrm.UI.component;

import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private JLabel currentSelectedTab = null;
    private JLabel currentHoveredTab = null;
    private JLabel firstMenuTab = null;
    private List<SidebarTab> tabsList;
    private Map<String, JLabel> tabLabelMap = new HashMap<>();

    public Sidebar(JPanel contentPanel, CardLayout cardLayout, List<SidebarTab> tabsList) {
        this.contentPanel = contentPanel;
        this.cardLayout = cardLayout;
        this.tabsList = tabsList;

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
        
        // Tự động chọn tab đầu tiên (Tổng quan) khi mới đăng nhập
        selectFirstTab();
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
            if (c instanceof JLabel && !(c instanceof JLabel)) {
                JLabel menuLabel = (JLabel) c;
                menuLabel.setToolTipText(isCollapsed ? menuLabel.getText() : null);
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
        for (int i = 0; i < tabsLists.size(); i++) {
            SidebarTab tab = tabsLists.get(i);
            JLabel menuLabel = createMenuLabel(tab);
            menuContainer.add(menuLabel);
            tabLabelMap.put(tab.getCardName(), menuLabel);
            
            // Thêm đường ngăn cách giữa các tab (trừ tab cuối)
            if (i < tabsLists.size() - 1) {
                JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
                separator.setMaximumSize(new Dimension(EXPANDED_WIDTH - 20, 1));
                separator.setForeground(Color.WHITE);
                menuContainer.add(separator);
            }
            
            // Lưu tab đầu tiên (không phải LOGOUT)
            if (i == 0 && firstMenuTab == null && !"LOGOUT".equals(tab.getCardName())) {
                firstMenuTab = menuLabel;
            }
        }
    }

    private JLabel createMenuLabel(SidebarTab tab) {
        JLabel label = new JLabel(tab.getTitle());
        label.setAlignmentX(CENTER_ALIGNMENT);
        label.setMaximumSize(new Dimension(EXPANDED_WIDTH - 10, 45));
        label.setPreferredSize(new Dimension(EXPANDED_WIDTH - 10, 45));
        label.setOpaque(false);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));

        label.addMouseListener(new java.awt.event.MouseAdapter() {
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
                    selectTab(label);
                }
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (label != currentSelectedTab) {
                    // Clear previous hovered tab if exists
                    if (currentHoveredTab != null && currentHoveredTab != currentSelectedTab) {
                        currentHoveredTab.setOpaque(false);
                        currentHoveredTab.repaint();
                    }
                    // Apply hover effect to current tab
                    label.setOpaque(true);
                    label.setBackground(new Color(180, 100, 255));
                    label.repaint();
                    currentHoveredTab = label;
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (label != currentSelectedTab && label == currentHoveredTab) {
                    label.setOpaque(false);
                    label.setBackground(sidebarColor);
                    label.repaint();
                    currentHoveredTab = null;
                }
            }
        });

        return label;
    }

    private void selectTab(JLabel label) {
        // Reset all other tabs to initial state (no background)
        for (Component c : menuContainer.getComponents()) {
            if (c instanceof JLabel && c != label) {
                JLabel otherLabel = (JLabel) c;
                otherLabel.setOpaque(false);
                otherLabel.repaint();
            }
        }
        currentSelectedTab = label;
        currentHoveredTab = null;  // Clear hovered tab when selecting a new tab
        label.setOpaque(true);
        label.setBackground(new Color(180, 100, 255));
        label.repaint();
    }

    private void selectFirstTab() {
        if (firstMenuTab != null && !tabsList.isEmpty()) {
            selectTab(firstMenuTab);
            cardLayout.show(contentPanel, tabsList.get(0).getCardName());
        }
    }

    public void selectTabByCardName(String cardName) {
        JLabel label = tabLabelMap.get(cardName);
        if (label != null) {
            cardLayout.show(contentPanel, cardName);
            selectTab(label);
        }
    }
}