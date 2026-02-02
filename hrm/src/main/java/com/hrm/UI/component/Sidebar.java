package com.hrm.UI.component;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.net.URL;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.hrm.UI.LoginUI;
import com.hrm.utils.*;

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

        try {
            URL url = getClass().getResource("/icons/HRM_Logo.png");
            ImageIcon LogoIcon = (url != null) ? new ImageIcon(url) : new ImageIcon();

            JLabel Logo = new JLabel(IconResize.resizeIcon(LogoIcon, 150, 150));
            Logo.setAlignmentX(CENTER_ALIGNMENT);
            this.add(Logo);
            this.add(Box.createRigidArea(new Dimension(0,30))); // khoảng cách dưới logo
    
        } catch (Exception e) {
            System.err.println("lỗi hiển thị Icon " + e.getMessage());
        }

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
            if(tab.getCardName().equals("LOGOUT")){
                java.awt.Window window = javax.swing.SwingUtilities.getWindowAncestor(this); // Lấy cửa sổ cha

                if(window != null){
                    window.dispose(); // Đóng cửa sổ cha
                    new LoginUI().setVisible(true); // Mở lại cửa sổ đăng nhập
                }
            }else {
                cardLayout.show(contentPanel, tab.getCardName());
            }
        });

        return button;
    }

}