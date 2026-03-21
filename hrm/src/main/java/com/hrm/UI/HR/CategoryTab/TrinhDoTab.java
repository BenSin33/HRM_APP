package com.hrm.UI.HR.CategoryTab;

import com.formdev.flatlaf.FlatClientProperties;
import com.hrm.DAO.TrinhDoDAO;
import com.hrm.DTO.TrinhDoDTO;
import com.hrm.UI.component.CRUDDialog;
import com.hrm.Service.PermissionService;
import com.hrm.DTO.UserDTO;
import com.hrm.utils.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;

/**
 * Tab quản lý trình độ
 */
public class TrinhDoTab extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private TrinhDoDAO trinhDoDAO;
    private PermissionService permissionService;
    private CategoryActionRenderer actionRenderer;
    private int hoveredRow = -1;

    public TrinhDoTab() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        putClientProperty(FlatClientProperties.STYLE, "background: #f8f9fa");

        trinhDoDAO = new TrinhDoDAO();
        permissionService = new PermissionService();

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
        String[] columns = {"Mã trình độ", "Tên trình độ", "Hệ số", "Thao tác"};
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
        UserDTO currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null || !permissionService.canAdd(currentUser, "CN09_CATEGORY")) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền thêm trình độ", "Từ chối truy cập", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        TrinhDoEditForm form = new TrinhDoEditForm();
        form.setMaTrinhDo(trinhDoDAO.generateNextMaTrinhDo());

        CRUDDialog<TrinhDoDTO> dialog = new CRUDDialog<>(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                "Thêm trình độ",
                form,
                null
        );
        dialog.setVisible(true);

        TrinhDoDTO result = dialog.getResult();
        if (result != null) {
            if (result.getMaTrinhDo() == null || result.getMaTrinhDo().trim().isEmpty()) {
                result.setMaTrinhDo(trinhDoDAO.generateNextMaTrinhDo());
            }
            if (trinhDoDAO.addTrinhDo(result)) {
                loadData();
                JOptionPane.showMessageDialog(this, "Thêm trình độ thành công!");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Thêm trình độ thất bại!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleEdit(int row) {
        UserDTO currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null || !permissionService.canEdit(currentUser, "CN09_CATEGORY")) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền sửa trình độ", "Từ chối truy cập", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String maTrinhDo = String.valueOf(tableModel.getValueAt(row, 0));
        TrinhDoDTO dto = trinhDoDAO.getTrinhDoById(maTrinhDo);

        if (dto != null) {
            TrinhDoEditForm form = new TrinhDoEditForm();
            CRUDDialog<TrinhDoDTO> dialog = new CRUDDialog<>(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    "Chỉnh sửa trình độ",
                    form,
                    dto
            );
            dialog.setVisible(true);

            TrinhDoDTO result = dialog.getResult();
            if (result != null) {
                if (trinhDoDAO.updateTrinhDo(result)) {
                    loadData();
                    JOptionPane.showMessageDialog(this, "Cập nhật trình độ thành công!");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Cập nhật trình độ thất bại!",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void handleDelete(int row) {
        UserDTO currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null || !permissionService.canDelete(currentUser, "CN09_CATEGORY")) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền xóa trình độ", "Từ chối truy cập", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String maTrinhDo = String.valueOf(tableModel.getValueAt(row, 0));
        int option = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa trình độ này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            if (trinhDoDAO.deleteTrinhDo(maTrinhDo)) {
                loadData();
                JOptionPane.showMessageDialog(this, "Xóa trình độ thành công!");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Xóa trình độ thất bại! Có thể trình độ đang được sử dụng.",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<TrinhDoDTO> list = trinhDoDAO.getAllTrinhDo();

        for (TrinhDoDTO item : list) {
            tableModel.addRow(new Object[]{
                    item.getMaTrinhDo(),
                    item.getTrinhDo(),
                    item.getHeSoTrinhDo() != null ? item.getHeSoTrinhDo().toPlainString() : "0",
                    ""
            });
        }
    }
}
