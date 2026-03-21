package com.hrm.UI.HR.CategoryTab;

import com.formdev.flatlaf.FlatClientProperties;
import com.hrm.DAO.DepartmentCategoryDAO;
import com.hrm.DTO.DepartmentCategoryDTO;
import com.hrm.UI.component.CRUDDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;

/**
 * Tab quản lý phòng ban
 */
public class DepartmentTab extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private DepartmentCategoryDAO departmentDAO;
    private CategoryActionRenderer actionRenderer;
    private int hoveredRow = -1;

    public DepartmentTab() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        putClientProperty(FlatClientProperties.STYLE, "background: #f8f9fa");

        departmentDAO = new DepartmentCategoryDAO();

        add(createButtonPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);

        loadData();
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.putClientProperty(FlatClientProperties.STYLE, "background: #ffffff");
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        JButton btnAdd = new JButton("Thêm mới");
        btnAdd.putClientProperty(FlatClientProperties.STYLE, "background: #4CAF50; foreground: #ffffff");
        btnAdd.addActionListener(e -> handleAdd());

        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.putClientProperty(FlatClientProperties.STYLE, "background: #2196F3; foreground: #ffffff");
        btnRefresh.addActionListener(e -> loadData());

        panel.add(btnAdd);
        panel.add(btnRefresh);

        return panel;
    }

    private JScrollPane createTablePanel() {
        String[] columns = {"Mã phòng ban", "Tên phòng ban", "Thao tác"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (column == 2 && renderer instanceof CategoryActionRenderer) {
                    ((CategoryActionRenderer) renderer).setHovered(row == hoveredRow);
                }
                return c;
            }
        };
        table.setRowHeight(36);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(2).setPreferredWidth(140);

        actionRenderer = new CategoryActionRenderer();
        table.getColumnModel().getColumn(2).setCellRenderer(actionRenderer);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());
                if (row >= 0 && col == 2) {
                    handleTableAction(row, evt.getPoint().x);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                hoveredRow = -1;
                table.repaint();
            }
        });

        table.addMouseMotionListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row != -1 && col == 2) {
                    if (hoveredRow != row) {
                        hoveredRow = row;
                        table.repaint();
                    }
                } else if (hoveredRow != -1) {
                    hoveredRow = -1;
                    table.repaint();
                }
            }
        });

        return new JScrollPane(table);
    }

    private void handleTableAction(int row, int pointX) {
        if (row < 0 || row >= tableModel.getRowCount()) return;

        int rectX = table.getCellRect(row, 2, true).x;
        int cellWidth = table.getCellRect(row, 2, true).width;

        if (pointX < rectX + cellWidth / 2) {
            handleEdit(row);
        } else {
            handleDelete(row);
        }
    }

    private void handleAdd() {
        DepartmentEditForm form = new DepartmentEditForm();
        form.setMaPhongBan(departmentDAO.generateNextMaPhongBan());

        CRUDDialog<DepartmentCategoryDTO> dialog = new CRUDDialog<>(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                "Thêm phòng ban",
                form,
                null
        );
        dialog.setVisible(true);

        DepartmentCategoryDTO result = dialog.getResult();
        if (result != null) {
            if (result.getMaPhongBan() == null || result.getMaPhongBan().trim().isEmpty()) {
                result.setMaPhongBan(departmentDAO.generateNextMaPhongBan());
            }

            if (departmentDAO.addDepartment(result)) {
                loadData();
                JOptionPane.showMessageDialog(this, "Thêm phòng ban thành công!");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Thêm phòng ban thất bại!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleEdit(int row) {
        String maPhongBan = String.valueOf(tableModel.getValueAt(row, 0));
        DepartmentCategoryDTO dto = departmentDAO.getDepartmentById(maPhongBan);

        if (dto != null) {
            DepartmentEditForm form = new DepartmentEditForm();
            CRUDDialog<DepartmentCategoryDTO> dialog = new CRUDDialog<>(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    "Chỉnh sửa phòng ban",
                    form,
                    dto
            );
            dialog.setVisible(true);

            DepartmentCategoryDTO result = dialog.getResult();
            if (result != null) {
                if (departmentDAO.updateDepartment(result)) {
                    loadData();
                    JOptionPane.showMessageDialog(this, "Cập nhật phòng ban thành công!");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Cập nhật phòng ban thất bại!",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void handleDelete(int row) {
        String maPhongBan = String.valueOf(tableModel.getValueAt(row, 0));
        int option = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa phòng ban này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            if (departmentDAO.deleteDepartment(maPhongBan)) {
                loadData();
                JOptionPane.showMessageDialog(this, "Xóa phòng ban thành công!");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Xóa phòng ban thất bại! Có thể phòng ban đang được sử dụng.",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<DepartmentCategoryDTO> list = departmentDAO.getAllDepartments();

        for (DepartmentCategoryDTO item : list) {
            tableModel.addRow(new Object[]{
                    item.getMaPhongBan(),
                    item.getTenPhongBan(),
                    ""
            });
        }
    }
}
