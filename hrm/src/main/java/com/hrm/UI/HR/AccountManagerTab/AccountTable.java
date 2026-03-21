<<<<<<< HEAD
package com.hrm.UI.HR.AccountManagerTab;

import com.formdev.flatlaf.FlatClientProperties;
import com.hrm.DAO.AccountManagerDAO;
import com.hrm.DTO.AccountManagerDTO;
import com.hrm.utils.SessionManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class AccountTable extends JPanel {
    
    private JTable accountTable;
    private DefaultTableModel tableModel;
    private AccountManagerDAO accountDAO;
    private AccountActionRenderer actionRenderer;
    private int hoveredRow = -1;
    private List<AccountManagerDTO> allAccounts;

    public AccountTable() {
        setLayout(new BorderLayout());
        setOpaque(false);
        accountDAO = new AccountManagerDAO();
        allAccounts = new ArrayList<>();
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
        allAccounts = accountDAO.getAllAccounts();
        applyFilter(""); // Load tất cả dữ liệu vào bảng
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
        
        // Truyền thông tin người dùng hiện tại để check xem có thể sửa role không
        String currentUserMaNV = SessionManager.getInstance().getCurrentUser().getManv();
        String currentUserPosition = getCurrentUserPosition(currentUserMaNV);
        String currentUserDepartment = getCurrentUserDepartment(currentUserMaNV);
        editForm.setCurrentUser(currentUserMaNV, currentUserPosition, currentUserDepartment);
        
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

    private String getCurrentUserPosition(String maNV) {
        String sql = "SELECT cv.MACHUCVU FROM nhanvien nv " +
                     "JOIN chucvu cv ON nv.MACHUCVU = cv.MACHUCVU " +
                     "WHERE nv.MANV = ?";
        try (var conn = com.hrm.utils.JDBCConection.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("MACHUCVU");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getCurrentUserDepartment(String maNV) {
        String sql = "SELECT MAPHONGBAN FROM nhanvien WHERE MANV = ?";
        try (var conn = com.hrm.utils.JDBCConection.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("MAPHONGBAN");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
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

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setOpaque(false);
        
        JLabel lblSearch = new JLabel("Tìm kiếm:");
        JTextField searchField = new JTextField(20);
        searchField.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        
        searchPanel.add(lblSearch, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        
        return searchPanel;
    }

    public void applyFilter(String searchText, String phongBan, String role) {
        tableModel.setRowCount(0);
        
        String lowerKeyword = (searchText == null || searchText.trim().isEmpty()) ? "" : searchText.toLowerCase();
        
        for (AccountManagerDTO account : allAccounts) {
            // Filter theo text search
            if (!lowerKeyword.isEmpty()) {
                if (!((account.maNV != null && account.maNV.toLowerCase().contains(lowerKeyword)) ||
                      (account.hoTen != null && account.hoTen.toLowerCase().contains(lowerKeyword)) ||
                      (account.email != null && account.email.toLowerCase().contains(lowerKeyword)) ||
                      (account.roleName != null && account.roleName.toLowerCase().contains(lowerKeyword)))) {
                    continue;
                }
            }
            
            // Filter theo phòng ban
            if (phongBan != null && !phongBan.equals(account.phongBan)) {
                continue;
            }
            
            // Filter theo vai trò
            if (role != null && !role.equals(account.roleName)) {
                continue;
            }
            
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
        updatePageInfo(tableModel.getRowCount());
    }

    public void applyFilter(String searchText) {
        applyFilter(searchText, null, null);
    }

    // Public method để refresh dữ liệu từ Tab
    public void refreshData() {
        tableModel.setRowCount(0);
        loadAccountData();
    }

    public JTable getAccountTable() {
        return accountTable;
    }
}
=======
package com.hrm.UI.HR.AccountManagerTab;

import com.formdev.flatlaf.FlatClientProperties;
import com.hrm.DAO.AccountManagerDAO;
import com.hrm.DTO.AccountManagerDTO;
import com.hrm.utils.SessionManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class AccountTable extends JPanel {
    
    private JTable accountTable;
    private DefaultTableModel tableModel;
    private AccountManagerDAO accountDAO;
    private AccountActionRenderer actionRenderer;
    private int hoveredRow = -1;
    private List<AccountManagerDTO> allAccounts;

    public AccountTable() {
        setLayout(new BorderLayout());
        setOpaque(false);
        accountDAO = new AccountManagerDAO();
        allAccounts = new ArrayList<>();
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
        allAccounts = accountDAO.getAllAccounts();
        applyFilter(""); // Load tất cả dữ liệu vào bảng
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
        
        // Truyền thông tin người dùng hiện tại để check xem có thể sửa role không
        String currentUserMaNV = SessionManager.getInstance().getCurrentUser().getManv();
        String currentUserPosition = getCurrentUserPosition(currentUserMaNV);
        String currentUserDepartment = getCurrentUserDepartment(currentUserMaNV);
        editForm.setCurrentUser(currentUserMaNV, currentUserPosition, currentUserDepartment);
        
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

    private String getCurrentUserPosition(String maNV) {
        String sql = "SELECT cv.MACHUCVU FROM nhanvien nv " +
                     "JOIN chucvu cv ON nv.MACHUCVU = cv.MACHUCVU " +
                     "WHERE nv.MANV = ?";
        try (var conn = com.hrm.utils.JDBCConection.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("MACHUCVU");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getCurrentUserDepartment(String maNV) {
        String sql = "SELECT MAPHONGBAN FROM nhanvien WHERE MANV = ?";
        try (var conn = com.hrm.utils.JDBCConection.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("MAPHONGBAN");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
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

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setOpaque(false);
        
        JLabel lblSearch = new JLabel("Tìm kiếm:");
        JTextField searchField = new JTextField(20);
        searchField.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        
        searchPanel.add(lblSearch, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        
        return searchPanel;
    }

    public void applyFilter(String searchText, String phongBan, String role) {
        tableModel.setRowCount(0);
        
        String lowerKeyword = (searchText == null || searchText.trim().isEmpty()) ? "" : searchText.toLowerCase();
        
        for (AccountManagerDTO account : allAccounts) {
            // Filter theo text search
            if (!lowerKeyword.isEmpty()) {
                if (!((account.maNV != null && account.maNV.toLowerCase().contains(lowerKeyword)) ||
                      (account.hoTen != null && account.hoTen.toLowerCase().contains(lowerKeyword)) ||
                      (account.email != null && account.email.toLowerCase().contains(lowerKeyword)) ||
                      (account.roleName != null && account.roleName.toLowerCase().contains(lowerKeyword)))) {
                    continue;
                }
            }
            
            // Filter theo phòng ban
            if (phongBan != null && !phongBan.equals(account.phongBan)) {
                continue;
            }
            
            // Filter theo vai trò
            if (role != null && !role.equals(account.roleName)) {
                continue;
            }
            
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
        updatePageInfo(tableModel.getRowCount());
    }

    public void applyFilter(String searchText) {
        applyFilter(searchText, null, null);
    }

    // Public method để refresh dữ liệu từ Tab
    public void refreshData() {
        tableModel.setRowCount(0);
        loadAccountData();
    }

    public JTable getAccountTable() {
        return accountTable;
    }
}
>>>>>>> 1cfb281dfcc017364337a4c66ee412a5e8d7de17
