package com.hrm.UI.HR.ContractTab;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionListener;
import com.formdev.flatlaf.FlatClientProperties;

public class ContractFilter extends JPanel {
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private JComboBox<String> typeFilter;
    private ActionListener filterCallback;

    public ContractFilter() {
        initComponent();
    }

    private void initComponent() {
        setLayout(new BorderLayout(15, 0));
        setOpaque(false);
        
        // Search field
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(true);
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        searchPanel.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        
        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setText("Tìm kiếm theo tên, mã NV, mã hợp đồng...");
        searchField.setForeground(new Color(180, 180, 180));
        
        searchField.addFocusListener(new java.awt.event.FocusListener() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals("Tìm kiếm theo tên, mã NV, mã hợp đồng...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Tìm kiếm theo tên, mã NV, mã hợp đồng...");
                    searchField.setForeground(new Color(180, 180, 180));
                }
            }
        });
        
        // Add document listener for real-time search
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                triggerFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                triggerFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                triggerFilter();
            }
        });
        
        searchPanel.add(searchField, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.CENTER);
        
        // Filter buttons panel
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new GridLayout(1, 2, 15, 0));
        filterPanel.setOpaque(false);
        
        // Filter: Trạng thái
        statusFilter = new JComboBox<>(new String[]{
            "Tất cả trạng thái", "Đang hiệu lực", "Sắp hết hạn", "Hết hạn"
        });
        statusFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusFilter.setBackground(Color.WHITE);
        statusFilter.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        statusFilter.setPreferredSize(new Dimension(150, 40));
        statusFilter.addActionListener(e -> triggerFilter());
        
        // Filter: Loại hợp đồng
        typeFilter = new JComboBox<>(new String[]{
            "Tất cả loại HD", "Toàn thời gian", "Bán thời gian", "Thử việc", "Hợp đồng khác"
        });
        typeFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        typeFilter.setBackground(Color.WHITE);
        typeFilter.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        typeFilter.setPreferredSize(new Dimension(150, 40));
        typeFilter.addActionListener(e -> triggerFilter());
        
        filterPanel.add(statusFilter);
        filterPanel.add(typeFilter);
        
        add(filterPanel, BorderLayout.EAST);
    }

    private void triggerFilter() {
        if (filterCallback != null) {
            filterCallback.actionPerformed(null);
        }
    }

    public void setFilterCallback(ActionListener callback) {
        this.filterCallback = callback;
    }

    public String getSearchText() {
        String text = searchField.getText();
        if (text.equals("Tìm kiếm theo tên, mã NV, mã hợp đồng...")) {
            return "";
        }
        return text.trim();
    }

    public String getSelectedStatus() {
        String selected = (String) statusFilter.getSelectedItem();
        return selected.equals("Tất cả trạng thái") ? "" : selected;
    }

    public String getSelectedType() {
        String selected = (String) typeFilter.getSelectedItem();
        return selected.equals("Tất cả loại HD") ? "" : selected;
    }
}
