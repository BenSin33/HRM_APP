package com.hrm.UI.Employee.PayrollEmp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class PayrollHeader extends JPanel {
    private JComboBox<String> cbMonth;
    
    public PayrollHeader() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));
        setBorder(new EmptyBorder(15, 25, 15, 25));

        // Tiêu đề
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        titlePanel.setOpaque(false);
        JLabel lblMain = new JLabel("Bảng lương của tôi");
        lblMain.setFont(new Font("Segoe UI", Font.BOLD, 26));
        JLabel lblSub = new JLabel("Xem chi tiết lương cá nhân");
        lblSub.setForeground(Color.GRAY);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titlePanel.add(lblMain);
        titlePanel.add(lblSub);

        // Bộ lọc chọn tháng
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        filterPanel.setOpaque(false);
        
        JLabel lblSelect = new JLabel("Chọn tháng: ");
        lblSelect.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        String[] months = {"Tháng 02/2026", "Tháng 01/2026", "Tháng 12/2025", "Tháng 11/2025", 
                          "Tháng 10/2025", "Tháng 09/2025", "Tháng 08/2025", "Tháng 07/2025",
                          "Tháng 06/2025", "Tháng 05/2025", "Tháng 04/2025", "Tháng 03/2025", "Tháng 02/2025"};
        cbMonth = new JComboBox<>(months);
        cbMonth.setPreferredSize(new Dimension(180, 40));
        cbMonth.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbMonth.setSelectedIndex(0);
        
        filterPanel.add(lblSelect);
        filterPanel.add(cbMonth);

        add(titlePanel, BorderLayout.WEST);
        add(filterPanel, BorderLayout.EAST);
    }
    
    public void addMonthChangeListener(ActionListener listener) {
        cbMonth.addActionListener(listener);
    }
    
    public String getSelectedMonth() {
        return (String) cbMonth.getSelectedItem();
    }
}