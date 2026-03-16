package com.hrm.UI.Employee.ProfileEmp;

import java.awt.*;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.*;

public class ProfileHeader extends JPanel {
    public ProfileHeader() {
        setLayout(new GridLayout(2, 1));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        JLabel title = new JLabel("Hồ sơ cá nhân");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        
        JLabel sub = new JLabel("Xem thông tin cá nhân và hợp đồng");
        sub.setForeground(Color.GRAY);

        add(title);
        add(sub);
    }
}