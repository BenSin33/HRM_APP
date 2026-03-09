package com.hrm.UI.HR.AccountManagerTab;

import com.formdev.flatlaf.FlatClientProperties;
import com.hrm.DAO.AccountManagerDAO;
import com.hrm.DTO.AccountManagerDTO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class AccountTable extends JPanel {
    
    private JTable accountTable;
    private DefaultTableModel tableModel;
    private AccountManagerDAO accountDAO;
    private AccountActionRenderer actionRenderer;
    private int hoveredRow = -1;

    public AccountTable() {
        setLayout(new BorderLayout());
        setOpaque(false);
        accountDAO = new AccountManagerDAO();
        initComponent();
    }

    private void initComponent() {
        // Tạo model bảng với nhiều cột thông tin hơn
        String[] columns = {"Mã NV", "Họ tên", "Email", "Điện thoại", "Vai trò", "Phòng ban", "Trạng thái", "Thao tác"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Thêm dữ liệu từ database
        loadAccountData();

        // Tạo bảng
        accountTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (column == 7 && renderer instanceof AccountActionRenderer) {
                    ((AccountActionRenderer) renderer).setHovered(row == hoveredRow);
                }
                return c;
            }
        };
        accountTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        accountTable.setRowHeight(35);
        accountTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        accountTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accountTable.putClientProperty(FlatClientProperties.STYLE, 
            "gridColor: #e0e0e0; background: #ffffff");

        // Đặt độ rộng cột
        accountTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        accountTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        accountTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        accountTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        accountTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        accountTable.getColumnModel().getColumn(5).setPreferredWidth(120);
        accountTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        accountTable.getColumnModel().getColumn(7).setPreferredWidth(100);

        actionRenderer = new AccountActionRenderer();
        accountTable.getColumnModel().getColumn(7).setCellRenderer(actionRenderer);

        // Xử lý mouse click trên cột thao tác
        accountTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = accountTable.rowAtPoint(e.getPoint());
                int col = accountTable.columnAtPoint(e.getPoint());
                
                if (row != -1 && col == 7) {
                    String maNV = (String) tableModel.getValueAt(row, 0);
                    Rectangle cellRect = accountTable.getCellRect(row, col, false);
                    int relativeX = e.getX() - (int)cellRect.getX();
                    int buttonWidth = cellRect.width / 2;
                    
                    if (relativeX < buttonWidth) {
                        handleEdit(maNV);
                    } else {
                        handleToggleStatus(maNV);
                    }
                }
            }
        });

        // Xử lý mouse hover
        accountTable.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = accountTable.rowAtPoint(e.getPoint());
                int col = accountTable.columnAtPoint(e.getPoint());
                
                if (row != -1 && col == 7) {
                    if (hoveredRow != row) {
                        hoveredRow = row;
                        accountTable.repaint();
                    }
                } else {
                    if (hoveredRow != -1) {
                        hoveredRow = -1;
                        accountTable.repaint();
                    }
                }
            }
        });

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
                account.hoTen,
                account.email != null ? account.email : "N/A",
                account.dienThoai != null ? account.dienThoai : "N/A",
                account.roleName,
                account.phongBan,
                account.getStatusText(),
                ""
            });
        }
        
        // Cập nhật thông tin phân trang
        updatePageInfo(accounts.size());
    }

    private void updatePageInfo(int totalRecords) {
        // Thay đổi label ở footer
        if (getComponentCount() > 1) {
            JPanel footerPanel = (JPanel) getComponent(1);
            if (footerPanel.getComponentCount() > 0) {
                JLabel pageLabel = (JLabel) footerPanel.getComponent(0);
                pageLabel.setText("Hiển thị tổng cộng " + totalRecords + " tài khoản");
            }
        }
    }

    private void handleEdit(String maNV) {
        AccountManagerDTO account = accountDAO.getAccountByMaNV(maNV);
        if (account == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy tài khoản!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        AccountEditForm editForm = new AccountEditForm();
        editForm.setFormData(account);
        
        int result = JOptionPane.showConfirmDialog(this, editForm, "Cập nhật tài khoản", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION && editForm.validateForm()) {
            AccountManagerDTO updated = editForm.getFormData();
            if (accountDAO.updateAccount(updated)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleToggleStatus(String maNV) {
        AccountManagerDTO account = accountDAO.getAccountByMaNV(maNV);
        if (account == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy tài khoản!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String message = account.status == 1 ? 
            "Bạn chắc chắn muốn khóa tài khoản này?" : 
            "Bạn chắc chắn muốn kích hoạt tài khoản này?";
        
        int confirm = JOptionPane.showConfirmDialog(this, message, "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int newStatus = account.status == 1 ? 0 : 1;
            if (accountDAO.setAccountStatus(account.maNV, newStatus)) {
                JOptionPane.showMessageDialog(this, "Cập nhật trạng thái thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Public method để refresh dữ liệu từ Tab
    public void refreshData() {
        tableModel.setRowCount(0);
        loadAccountData();
    }
}
