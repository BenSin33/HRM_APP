package com.hrm.UI.HR.ContractTab;

import javax.swing.*;
import java.awt.*;
import com.hrm.DTO.ContractDTO;
import com.hrm.DAO.ContractDAO;
import com.hrm.UI.component.CRUDDialog;

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

    public ContractManagement() {
        initComponent();
    }

    private void initComponent() {
        setLayout(new BorderLayout(0, 20));
        setBackground(new Color(245, 245, 245));
        
        // Padding cho toàn bộ panel
        setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // 1. Header: Title + Stats Cards + Nút thêm
        header = new ContractHeader();
        header.setOnAddCallback(() -> handleAddContract());
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
        filterContainer.add(filter, BorderLayout.CENTER);
        centerPanel.add(filterContainer, BorderLayout.NORTH);

        // 3. Table: Danh sách hợp đồng
        contractTable = new ContractTable();
        contractTable.setParentPanel(this);
        centerPanel.add(contractTable, BorderLayout.CENTER);
        
        this.add(centerPanel, BorderLayout.CENTER);
    }

    private void handleAddContract() {
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

    public void refreshData() {
        contractTable.refreshData();
        header.loadStats();
    }
}
