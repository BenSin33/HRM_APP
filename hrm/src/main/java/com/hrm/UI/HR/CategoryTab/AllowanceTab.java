package com.hrm.UI.HR.CategoryTab;

import com.hrm.DAO.AllowanceDAO;
import com.hrm.DTO.AllowanceDTO;
import com.hrm.UI.component.CRUDDialog;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Tab quản lý phụ cấp
 */
public class AllowanceTab extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private AllowanceDAO allowanceDAO;
    private JButton btnAdd, btnRefresh;

    public AllowanceTab() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        putClientProperty(FlatClientProperties.STYLE, "background: #f8f9fa");

        allowanceDAO = new AllowanceDAO();

        // Panel nút bấm
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.NORTH);

        // Panel bảng
        JScrollPane scrollPane = createTablePanel();
        add(scrollPane, BorderLayout.CENTER);

        // Load dữ liệu
        loadData();
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.putClientProperty(FlatClientProperties.STYLE, "background: #ffffff");
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        btnAdd = new JButton("Thêm mới");
        btnAdd.putClientProperty(FlatClientProperties.STYLE, "background: #4CAF50; foreground: #ffffff");
        btnAdd.addActionListener(e -> handleAdd());

        btnRefresh = new JButton("Làm mới");
        btnRefresh.putClientProperty(FlatClientProperties.STYLE, "background: #2196F3; foreground: #ffffff");
        btnRefresh.addActionListener(e -> loadData());

        panel.add(btnAdd);
        panel.add(btnRefresh);

        return panel;
    }

    private JScrollPane createTablePanel() {
        String[] columns = {"Mã phụ cấp", "Tên phụ cấp", "Số tiền mặc định", "Thao tác"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());
                if (row >= 0 && col == 3) {
                    handleTableAction(row, evt.getX(), evt.getPoint().x);
                }
            }
        });

        return new JScrollPane(table);
    }

    private void handleTableAction(int row, int eventX, int pointX) {
        if (row < 0 || row >= tableModel.getRowCount()) return;

        int rectX = table.getCellRect(row, 3, true).x;
        int cellWidth = table.getCellRect(row, 3, true).width;

        if (pointX < rectX + cellWidth / 2) {
            // Edit button
            handleEdit(row);
        } else {
            // Delete button
            handleDelete(row);
        }
    }

    private void handleAdd() {
        AllowanceEditForm form = new AllowanceEditForm();
        CRUDDialog<AllowanceDTO> dialog = new CRUDDialog<>(
            (JFrame) SwingUtilities.getWindowAncestor(this),
            "Thêm phụ cấp",
            form,
            null
        );
        dialog.setVisible(true);
        AllowanceDTO result = dialog.getResult();
        
        if (result != null) {
            if (allowanceDAO.addAllowance(result)) {
                loadData();
                JOptionPane.showMessageDialog(this, "Thêm phụ cấp thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Thêm phụ cấp thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleEdit(int row) {
        int maPhucap = (int) tableModel.getValueAt(row, 0);
        AllowanceDTO dto = allowanceDAO.getAllowanceById(maPhucap);

        if (dto != null) {
            AllowanceEditForm form = new AllowanceEditForm();
            CRUDDialog<AllowanceDTO> dialog = new CRUDDialog<>(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                "Chỉnh sửa phụ cấp",
                form,
                dto
            );
            dialog.setVisible(true);
            AllowanceDTO result = dialog.getResult();
            
            if (result != null) {
                if (allowanceDAO.updateAllowance(result)) {
                    loadData();
                    JOptionPane.showMessageDialog(this, "Cập nhật phụ cấp thành công!");
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật phụ cấp thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void handleDelete(int row) {
        int maPhucap = (int) tableModel.getValueAt(row, 0);
        int option = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa phụ cấp này?",
            "Xác nhận",
            JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            if (allowanceDAO.deleteAllowance(maPhucap)) {
                loadData();
                JOptionPane.showMessageDialog(this, "Xóa phụ cấp thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Xóa phụ cấp thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<AllowanceDTO> allowances = allowanceDAO.getAllAllowances();

        for (AllowanceDTO allowance : allowances) {
            tableModel.addRow(new Object[]{
                allowance.getMaPhucap(),
                allowance.getTenPhucap(),
                String.format("%,.0f VND", allowance.getSoTienMacDinh()),
                "Sửa | Xóa"
            });
        }
    }
}
