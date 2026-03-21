package com.hrm.UI.HR.CategoryTab;

import com.formdev.flatlaf.FlatClientProperties;
import com.hrm.DAO.EvaluationCriteriaDAO;
import com.hrm.DTO.EvaluationCriteriaDTO;
import com.hrm.UI.component.CRUDDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;

/**
 * Tab quản lý tiêu chí đánh giá
 */
public class EvaluationCriteriaTab extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private EvaluationCriteriaDAO criteriaDAO;
    private CategoryActionRenderer actionRenderer;
    private int hoveredRow = -1;

    public EvaluationCriteriaTab() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        putClientProperty(FlatClientProperties.STYLE, "background: #f8f9fa");

        criteriaDAO = new EvaluationCriteriaDAO();

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
        String[] columns = {"Mã tiêu chí", "Tên tiêu chí", "Điểm", "Thao tác"};
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
                if (column == 3 && renderer instanceof CategoryActionRenderer) {
                    ((CategoryActionRenderer) renderer).setHovered(row == hoveredRow);
                }
                return c;
            }
        };
        table.setRowHeight(36);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(3).setPreferredWidth(140);

        actionRenderer = new CategoryActionRenderer();
        table.getColumnModel().getColumn(3).setCellRenderer(actionRenderer);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());
                if (row >= 0 && col == 3) {
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
                if (row != -1 && col == 3) {
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

        int rectX = table.getCellRect(row, 3, true).x;
        int cellWidth = table.getCellRect(row, 3, true).width;

        if (pointX < rectX + cellWidth / 2) {
            handleEdit(row);
        } else {
            handleDelete(row);
        }
    }

    private void handleAdd() {
        EvaluationCriteriaEditForm form = new EvaluationCriteriaEditForm();
        form.setMaTieuChi(criteriaDAO.generateNextMaTieuChi());

        CRUDDialog<EvaluationCriteriaDTO> dialog = new CRUDDialog<>(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                "Thêm tiêu chí đánh giá",
                form,
                null
        );
        dialog.setVisible(true);

        EvaluationCriteriaDTO result = dialog.getResult();
        if (result != null) {
            if (result.getMaTieuChi() == null || result.getMaTieuChi().trim().isEmpty()) {
                result.setMaTieuChi(criteriaDAO.generateNextMaTieuChi());
            }
            if (criteriaDAO.addCriteria(result)) {
                loadData();
                JOptionPane.showMessageDialog(this, "Thêm tiêu chí đánh giá thành công!");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Thêm tiêu chí đánh giá thất bại!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleEdit(int row) {
        String maTieuChi = String.valueOf(tableModel.getValueAt(row, 0));
        EvaluationCriteriaDTO dto = criteriaDAO.getCriteriaById(maTieuChi);

        if (dto != null) {
            EvaluationCriteriaEditForm form = new EvaluationCriteriaEditForm();
            CRUDDialog<EvaluationCriteriaDTO> dialog = new CRUDDialog<>(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    "Chỉnh sửa tiêu chí đánh giá",
                    form,
                    dto
            );
            dialog.setVisible(true);

            EvaluationCriteriaDTO result = dialog.getResult();
            if (result != null) {
                if (criteriaDAO.updateCriteria(result)) {
                    loadData();
                    JOptionPane.showMessageDialog(this, "Cập nhật tiêu chí đánh giá thành công!");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Cập nhật tiêu chí đánh giá thất bại!",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void handleDelete(int row) {
        String maTieuChi = String.valueOf(tableModel.getValueAt(row, 0));
        int option = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa tiêu chí đánh giá này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            if (criteriaDAO.deleteCriteria(maTieuChi)) {
                loadData();
                JOptionPane.showMessageDialog(this, "Xóa tiêu chí đánh giá thành công!");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Xóa tiêu chí đánh giá thất bại! Có thể tiêu chí đang được sử dụng.",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<EvaluationCriteriaDTO> list = criteriaDAO.getAllCriteria();

        for (EvaluationCriteriaDTO item : list) {
            tableModel.addRow(new Object[]{
                    item.getMaTieuChi(),
                    item.getTenTieuChi(),
                    item.getDiem(),
                    ""
            });
        }
    }
}
