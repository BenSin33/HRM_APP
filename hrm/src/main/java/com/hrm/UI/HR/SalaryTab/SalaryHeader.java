package com.hrm.UI.HR.SalaryTab;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class SalaryHeader extends JPanel {
    private JButton btnCalculate;
    private JButton btnUnlock;
    private JButton btnLock;
    private JButton btnExport;
    private JButton btnImport;
    private JButton btnRefresh;
    private JButton btnPayment;

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

        btnExport = new JButton("Xuất Excel");
        btnExport.putClientProperty("FlatLaf.style", "background: #059669; foreground: #fff; arc: 10");

        btnImport = new JButton("Nhập Excel");
        btnImport.putClientProperty("FlatLaf.style", "background: #0891b2; foreground: #fff; arc: 10");

        btnRefresh = new JButton("Làm mới");
        btnRefresh.putClientProperty("FlatLaf.style", "background: #6b7280; foreground: #fff; arc: 10");

        btnPayment = new JButton("💳 Thanh toán");
        btnPayment.putClientProperty("FlatLaf.style", "background: #dc2626; foreground: #fff; arc: 10");

        btnPanel.add(btnCalculate);
        btnPanel.add(btnUnlock);
        btnPanel.add(btnLock);
        btnPanel.add(new JSeparator(JSeparator.VERTICAL) {
            {
                setPreferredSize(new Dimension(1, 30));
            }
        });
        btnPanel.add(btnExport);
        btnPanel.add(btnImport);
        btnPanel.add(btnRefresh);
        btnPanel.add(btnPayment);

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

    public JButton getExportButton() {
        return btnExport;
    }

    public JButton getImportButton() {
        return btnImport;
    }

    public JButton getRefreshButton() {
        return btnRefresh;
    }

    public JButton getPaymentButton() {
        return btnPayment;
    }

    public void setLockButtonEnabled(boolean enabled) {
        btnLock.setEnabled(enabled);
        if (!enabled) {
            btnLock.setToolTipText("Bạn không có quyền khóa lương");
        }
    }

    public void setUnlockButtonEnabled(boolean enabled) {
        btnUnlock.setEnabled(enabled);
        if (!enabled) {
            btnUnlock.setToolTipText("Bạn không có quyền mở khóa lương");
        }
    }

    public void setCalculateButtonEnabled(boolean enabled) {
        btnCalculate.setEnabled(enabled);
        if (!enabled) {
            btnCalculate.setToolTipText("Bạn không có quyền tính lương");
        }
    }

    public void setExportButtonEnabled(boolean enabled) {
        btnExport.setEnabled(enabled);
        if (!enabled) {
            btnExport.setToolTipText("Bạn không có quyền xuất dữ liệu lương");
        }
    }

    public void setImportButtonEnabled(boolean enabled) {
        btnImport.setEnabled(enabled);
        if (!enabled) {
            btnImport.setToolTipText("Bạn không có quyền nhập dữ liệu lương");
        }
    }
}