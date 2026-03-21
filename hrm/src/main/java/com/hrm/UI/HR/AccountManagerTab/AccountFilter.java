package com.hrm.UI.HR.AccountManagerTab;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;
import com.formdev.flatlaf.FlatClientProperties;
import com.hrm.utils.JDBCConection;

public class AccountFilter extends JPanel {
    private JTextField searchField;
    private JComboBox<String> cbPhongBan;
    private JComboBox<String> cbRole;
    private ActionListener filterCallback;

    public AccountFilter() {
        initComponent();
        loadPhongBanData();
        loadRoleData();
    }

    private void initComponent() {
        setLayout(new BorderLayout(10, 0));
        setOpaque(false);
        
        // Search field
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(true);
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        searchPanel.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        
        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setText("Tìm kiếm theo tên, mã NV, email...");
        searchField.setForeground(new Color(180, 180, 180));
        
        searchField.addFocusListener(new java.awt.event.FocusListener() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals("Tìm kiếm theo tên, mã NV, email...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Tìm kiếm theo tên, mã NV, email...");
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
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setOpaque(false);
        
        filterPanel.add(new JLabel("Phòng ban:"));
        cbPhongBan = new JComboBox<>();
        cbPhongBan.addItem("-- Tất cả --");
        cbPhongBan.addActionListener(e -> triggerFilter());
        filterPanel.add(cbPhongBan);
        
        filterPanel.add(new JLabel("Vai trò:"));
        cbRole = new JComboBox<>();
        cbRole.addItem("-- Tất cả --");
        cbRole.addActionListener(e -> triggerFilter());
        filterPanel.add(cbRole);
        
        add(filterPanel, BorderLayout.EAST);
    }

    private void loadPhongBanData() {
        String sql = "SELECT TENPHONGBAN FROM phongban ORDER BY TENPHONGBAN ASC";
        try (Connection conn = JDBCConection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                cbPhongBan.addItem(rs.getString("TENPHONGBAN"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadRoleData() {
        String sql = "SELECT ROLENAME FROM role ORDER BY ROLENAME ASC";
        try (Connection conn = JDBCConection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                cbRole.addItem(rs.getString("ROLENAME"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        if (text.equals("Tìm kiếm theo tên, mã NV, email...")) {
            return "";
        }
        return text.trim();
    }

    public String getSelectedPhongBan() {
        Object selected = cbPhongBan.getSelectedItem();
        if (selected != null && !selected.toString().equals("-- Tất cả --")) {
            return selected.toString();
        }
        return null;
    }

    public String getSelectedRole() {
        Object selected = cbRole.getSelectedItem();
        if (selected != null && !selected.toString().equals("-- Tất cả --")) {
            return selected.toString();
        }
        return null;
    }
}
