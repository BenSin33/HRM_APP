package com.hrm.UI.HR.AccountManagerTab;

import com.formdev.flatlaf.FlatClientProperties;
import com.hrm.DAO.AccountManagerDAO;
import com.hrm.DTO.AccountManagerDTO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AccountTable extends JPanel {
    
    private JTable accountTable;
    private DefaultTableModel tableModel;
    private AccountManagerDAO accountDAO;

    public AccountTable() {
        setLayout(new BorderLayout());
        setOpaque(false);
        accountDAO = new AccountManagerDAO();

        // Tạo model bảng
        String[] columns = {"ID", "Tên tài khoản", "Email", "Phòng ban", "Vai trò", "Trạng thái", "Điện thoại", "Hành động"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Thêm dữ liệu từ database
        loadAccountData();

        // Tạo bảng
        accountTable = new JTable(tableModel);
        accountTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        accountTable.setRowHeight(35);
        accountTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        accountTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accountTable.putClientProperty(FlatClientProperties.STYLE, 
            "gridColor: #e0e0e0; background: #ffffff");

        // Đặt độ rộng cột
        accountTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        accountTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        accountTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        accountTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        accountTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        accountTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        accountTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        accountTable.getColumnModel().getColumn(7).setPreferredWidth(80);

        // ScrollPane
        JScrollPane scrollPane = new JScrollPane(accountTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(224, 224, 224), 1));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        add(scrollPane, BorderLayout.CENTER);

        // Chân trang - Thông tin phân trang
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setOpaque(false);
        JLabel pageLabel = new JLabel("Đang tải dữ liệu...");
        pageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerPanel.add(pageLabel);

        add(footerPanel, BorderLayout.SOUTH);
    }

    private void loadAccountData() {
        List<AccountManagerDTO> accounts = accountDAO.getAllAccounts();
        
        for (AccountManagerDTO account : accounts) {
            tableModel.addRow(new Object[]{
                account.maNV,
                account.userId,
                account.email != null ? account.email : "N/A",
                account.phongBan,
                account.roleName,
                account.getStatusText(),
                account.dienThoai != null ? account.dienThoai : "N/A",
                "Chỉnh sửa"
            });
        }
        
        // Cập nhật thông tin phân trang
        updatePageInfo(accounts.size());
    }

    private void updatePageInfo(int totalRecords) {
        // Thay đổi label ở footer (có thể truy cập qua getComponent)
        if (getComponentCount() > 1) {
            JPanel footerPanel = (JPanel) getComponent(1);
            if (footerPanel.getComponentCount() > 0) {
                JLabel pageLabel = (JLabel) footerPanel.getComponent(0);
                pageLabel.setText("Hiển thị tổng cộng " + totalRecords + " tài khoản");
            }
        }
    }

    // Public method để refresh dữ liệu từ Tab
    public void refreshData() {
        tableModel.setRowCount(0);
        loadAccountData();
    }
}
