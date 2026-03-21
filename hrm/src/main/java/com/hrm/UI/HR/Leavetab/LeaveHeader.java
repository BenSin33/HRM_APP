package com.hrm.UI.HR.Leavetab;

import javax.swing.*;
import java.awt.*;

public class LeaveHeader extends JPanel {

    public LeaveHeader() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 4));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("Quản lý nghỉ phép");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));
        title.setForeground(new Color(17, 24, 39));

        JLabel subtitle = new JLabel("Theo dõi tổng");
        subtitle.setForeground(new Color(107, 114, 128));
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 13f));

        titlePanel.add(title);
        titlePanel.add(subtitle);

        add(titlePanel, BorderLayout.WEST);
    }
}