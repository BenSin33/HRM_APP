package com.hrm.UI.HR.SalaryTab;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class SalaryHeader extends JPanel {
    private JButton btnCalculate;
    private JButton btnUnlock;
    private JButton btnLock;

    public SalaryHeader(ActionListener lockListener, ActionListener unlockListener, 
                       ActionListener calculateListener) {
        setLayout(new BorderLayout());
        setOpaque(false);

        // 1. Tiêu đề
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        
        JLabel title = new JLabel("Quản lý bảng lương");
        title.putClientProperty("FlatLaf.style", "font: bold +10");
        
        JLabel subtitle = new JLabel("Tính & chốt lương");
        subtitle.setForeground(Color.GRAY);
        
        titlePanel.add(title);
        titlePanel.add(subtitle);

        // 2. Các nút bấm hành động
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        btnCalculate = new JButton("Tính lương");
        btnCalculate.putClientProperty("FlatLaf.style", "background: #3b82f6; foreground: #fff; arc: 10");
        btnCalculate.addActionListener(calculateListener);

        btnUnlock = new JButton("Mở khóa");
        btnUnlock.putClientProperty("FlatLaf.style", "background: #ca8a04; foreground: #fff; arc: 10");
        btnUnlock.addActionListener(unlockListener);

        btnLock = new JButton("Khóa lương");   
        btnLock.putClientProperty("FlatLaf.style", "background: #9333ea; foreground: #fff; arc: 10");
        btnLock.addActionListener(lockListener);

        btnPanel.add(btnCalculate);
        btnPanel.add(btnUnlock);
        btnPanel.add(btnLock);

        this.add(titlePanel, BorderLayout.WEST);
        this.add(btnPanel, BorderLayout.EAST);
    }

    public SalaryHeader(ActionListener lockListener, ActionListener unlockListener) {
        this(lockListener, unlockListener, e -> {});
    }

    public JButton getCalculateButton() {
        return btnCalculate;
    }

    public JButton getUnlockButton() {
        return btnUnlock;
    }

    public JButton getLockButton() {
        return btnLock;
    }
}