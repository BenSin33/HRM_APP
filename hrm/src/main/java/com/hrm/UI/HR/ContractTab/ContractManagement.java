package com.hrm.UI.HR.ContractTab;

import javax.swing.*;
import java.awt.*;
import com.hrm.DTO.ContractDTO;
import com.hrm.DAO.ContractDAO;
import com.hrm.DTO.UserDTO;
import com.hrm.Service.PermissionService;
import com.hrm.UI.component.CRUDDialog;
import com.hrm.utils.ContractExcelHelper;
import com.hrm.utils.SessionManager;

/**
 * ContractManagement - Giao diện quản lý hợp đồng
 * Tách thành các component nhỏ:
 * - ContractHeader: Hiển thị title, stats card và nút thêm
 * - ContractFilter: Thanh tìm kiếm và filter
 * - ContractTable: Bảng danh sách hợp đồng
 * - ContractStatsCard: Component card thống kê
 * - ContractTableRenderer: Renderer cho cột thao tác
 */
public class ContractManagement extends JPanel {
    private ContractHeader header;
    private ContractTable contractTable;
    private PermissionService permissionService;

    public ContractManagement() {
        permissionService = new PermissionService();
        initComponent();
    }

    private void initComponent() {
        setLayout(new BorderLayout(0, 20));
        setBackground(new Color(245, 245, 245));
        
        // Padding cho toàn bộ panel
        setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // 1. Header: Title + Stats Cards + Nút thêm + Export/Import
        header = new ContractHeader();
        UserDTO currentUser = SessionManager.getInstance().getCurrentUser();
        
        header.setAddButtonEnabled(permissionService.canAdd(currentUser, "CN09_CONTRACT"));
        header.setExportButtonEnabled(permissionService.canExport(currentUser, "CN09_CONTRACT"));
        header.setImportButtonEnabled(permissionService.canAdd(currentUser, "CN09_CONTRACT")); // Import is a form of Add

        header.setOnAddCallback(() -> handleAddContract());
        header.setOnExportCallback(() -> handleExportContract());
        header.setOnImportCallback(() -> handleImportContract());
        this.add(header, BorderLayout.NORTH);

        // 2. Center Panel: Filter + Table
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setOpaque(false);
        
        // Filter: Tìm kiếm + Dropdown filter
        JPanel filterContainer = new JPanel();
        filterContainer.setLayout(new BorderLayout());
        filterContainer.setOpaque(false);
        filterContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        ContractFilter filter = new ContractFilter();
        // Wire up filter callback to trigger table filtering
        filter.setFilterCallback(e -> {
            String searchText = filter.getSearchText();
            String status = filter.getSelectedStatus();
            String type = filter.getSelectedType();
            contractTable.applyFilter(searchText, status, type);
        });
        filterContainer.add(filter, BorderLayout.CENTER);
        centerPanel.add(filterContainer, BorderLayout.NORTH);

        // 3. Table: Danh sách hợp đồng
        contractTable = new ContractTable();
        contractTable.setParentPanel(this);
        centerPanel.add(contractTable, BorderLayout.CENTER);
        
        this.add(centerPanel, BorderLayout.CENTER);
    }

    private void handleAddContract() {
        UserDTO currentUser = SessionManager.getInstance().getCurrentUser();
        if (!permissionService.canAdd(currentUser, "CN09_CONTRACT")) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền thêm hợp đồng.", "Từ chối truy cập", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ContractAddForm addForm = new ContractAddForm();
        CRUDDialog<ContractDTO> dialog = new CRUDDialog<>(
            (JFrame) SwingUtilities.getWindowAncestor(this),
            "Thêm hợp đồng mới",
            addForm,
            null
        );
        
        dialog.setVisible(true);
        
        ContractDTO newContract = dialog.getResult();
        if (newContract != null) {
            ContractDAO contractDAO = new ContractDAO();
            if (contractDAO.addContract(newContract)) {
                JOptionPane.showMessageDialog(this, "Thêm hợp đồng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm hợp đồng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleExportContract() {
        UserDTO currentUser = SessionManager.getInstance().getCurrentUser();
        if (!permissionService.canExport(currentUser, "CN09_CONTRACT")) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền xuất dữ liệu.", "Từ chối truy cập", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ContractExcelHelper.handleContractExport(contractTable.getContractTable(), this);
    }

    private void handleImportContract() {
        UserDTO currentUser = SessionManager.getInstance().getCurrentUser();
        if (!permissionService.canAdd(currentUser, "CN09_CONTRACT")) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền nhập dữ liệu.", "Từ chối truy cập", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ContractExcelHelper.handleContractImport(contractTable.getContractTable(), this);
        refreshData();
    }

    public void refreshData() {
        contractTable.refreshData();
        header.loadStats();
    }
}
