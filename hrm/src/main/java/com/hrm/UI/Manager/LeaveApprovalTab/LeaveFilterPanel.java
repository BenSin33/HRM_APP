package com.hrm.UI.Manager.LeaveApprovalTab;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;  

public class LeaveFilterPanel extends JPanel {
    private JButton btnTatCa;
    private JButton btnChoDuyet;
    private JButton btnDaDuyet;
    private JButton btnTuChoi;
    
    private ActionListener filterListener;
    private String currentFilter = "all";

    public LeaveFilterPanel(ActionListener listener) {
        this.filterListener = listener;
        
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setBackground(Color.WHITE);

        btnTatCa = createFilterButton("Tất cả", "all", true);
        btnChoDuyet = createFilterButton("Chờ duyệt (0)", "cho", false);
        btnDaDuyet = createFilterButton("Đã duyệt", "da", false);
        btnTuChoi = createFilterButton("Từ chối", "tu", false);

        btnPanel.add(btnTatCa);
        btnPanel.add(btnChoDuyet);
        btnPanel.add(btnDaDuyet);
        btnPanel.add(btnTuChoi);

        add(btnPanel, BorderLayout.WEST);
    }

    private JButton createFilterButton(String text, String filter, boolean isActive) {
        JButton btn = new JButton(text);
        btn.setActionCommand(filter);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 38));

        setButtonStyle(btn, isActive);

        btn.addActionListener(e -> {
            // Reset tất cả nút
            setButtonStyle(btnTatCa, false);
            setButtonStyle(btnChoDuyet, false);
            setButtonStyle(btnDaDuyet, false);
            setButtonStyle(btnTuChoi, false);
            // Active nút được chọn
            setButtonStyle(btn, true);
            
            if (filterListener != null) {
                filterListener.actionPerformed(new ActionEvent(this, 
                    ActionEvent.ACTION_PERFORMED, filter));
            }
        });

        return btn;
    }

    private void setButtonStyle(JButton btn, boolean isActive) {
        if (isActive) {
            btn.setBackground(new Color(99, 102, 241));
            btn.setForeground(Color.WHITE);
            btn.setBorderPainted(false);
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(new Color(80, 80, 80));
            btn.setBorderPainted(true);
            btn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        }
    }

    public void updatePendingCount(int count) {
        btnChoDuyet.setText("Chờ duyệt (" + count + ")");
    }

    public String getCurrentFilter() {
        return currentFilter;
    }
}

