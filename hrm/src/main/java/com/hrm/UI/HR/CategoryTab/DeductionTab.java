package com.hrm.UI.HR.CategoryTab;

import com.hrm.DAO.DeductionDAO;
import com.hrm.DTO.DeductionDTO;
import com.hrm.UI.component.CRUDDialog;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Tab quản lý khấu trừ
 */
public class DeductionTab extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private DeductionDAO deductionDAO;
    private JButton btnAdd, btnRefresh;

    public DeductionTab() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        putClientProperty(FlatClientProperties.STYLE, "background: #f8f9fa");

        deductionDAO = new DeductionDAO();

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
        String[] columns = {"Mã khấu trừ", "Tên khấu trừ", "Số tiền mặc định", "Thao tác"};
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
        DeductionEditForm form = new DeductionEditForm();
        CRUDDialog<DeductionDTO> dialog = new CRUDDialog<>(
            (JFrame) SwingUtilities.getWindowAncestor(this),
            "Thêm khấu trừ",
            form,
            null
        );
        dialog.setVisible(true);
        DeductionDTO result = dialog.getResult();
        
        if (result != null) {
            if (deductionDAO.addDeduction(result)) {
                loadData();
                JOptionPane.showMessageDialog(this, "Thêm khấu trừ thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Thêm khấu trừ thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleEdit(int row) {
        int maKhauTru = (int) tableModel.getValueAt(row, 0);
        DeductionDTO dto = deductionDAO.getDeductionById(maKhauTru);

        if (dto != null) {
            DeductionEditForm form = new DeductionEditForm();
            CRUDDialog<DeductionDTO> dialog = new CRUDDialog<>(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                "Chỉnh sửa khấu trừ",
                form,
                dto
            );
            dialog.setVisible(true);
            DeductionDTO result = dialog.getResult();
            
            if (result != null) {
                if (deductionDAO.updateDeduction(result)) {
                    loadData();
                    JOptionPane.showMessageDialog(this, "Cập nhật khấu trừ thành công!");
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật khấu trừ thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void handleDelete(int row) {
        int maKhauTru = (int) tableModel.getValueAt(row, 0);
        int option = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa khấu trừ này?",
            "Xác nhận",
            JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            if (deductionDAO.deleteDeduction(maKhauTru)) {
                loadData();
                JOptionPane.showMessageDialog(this, "Xóa khấu trừ thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Xóa khấu trừ thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<DeductionDTO> deductions = deductionDAO.getAllDeductions();

        for (DeductionDTO deduction : deductions) {
            tableModel.addRow(new Object[]{
                deduction.getMaKhauTru(),
                deduction.getTenKhauTru(),
                String.format("%,.0f VND", deduction.getSoTienMacDinh()),
                "Sửa | Xóa"
            });
        }
    }
}
