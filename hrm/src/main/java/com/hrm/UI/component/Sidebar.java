package com.hrm.UI.component;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class Sidebar extends JPanel {

    private JPanel contentPanel;        // chứa các nội dung tương ứng
    private CardLayout cardLayout;      // quản lý việc chuyển đổi giữa các nội dung
    private Color sidebarColor = new Color(102,0,204); // màu tím đậm

    public Sidebar (JPanel contentPanel, CardLayout cardLayout , List<SidebarTab> tabsList) {
        this.contentPanel = contentPanel;
        this.cardLayout = cardLayout;

        //thiết lập giao diện sidebar
        this.setPreferredSize(new Dimension(250,0));
        this.setBackground(sidebarColor);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        renderMenu(tabsList);

    }

    private void renderMenu (List<SidebarTab> tabsLists){
        
        this.add(Box.createRigidArea(new Dimension (0,20))); // khoảng cách trên cùng

        for(SidebarTab tab : tabsLists){

            JButton menuButton = createMenuButton(tab);
            this.add(menuButton);
            this.add(Box.createRigidArea(new Dimension(0,10))); // khoảng cách giữa các nút
        }
    }

    private JButton createMenuButton (SidebarTab tab){
        JButton button = new JButton(tab.getTitle());
        button.setAlignmentX(CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200,40));
        button.setFocusPainted(false);
        button.setBackground(new Color(153,51,255)); // màu tím nhạt hơn
        button.setForeground(Color.WHITE);

        // thêm sự kiện chuyển đổi nội dung khi nhấn nút
        button.addActionListener(e -> {
            cardLayout.show(contentPanel, tab.getCardName());
        });

        return button;
    }

}