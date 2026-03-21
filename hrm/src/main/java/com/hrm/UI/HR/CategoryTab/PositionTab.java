<<<<<<< HEAD
package com.hrm.UI.HR.CategoryTab;

import com.formdev.flatlaf.FlatClientProperties;
import com.hrm.DAO.PositionDAO;
import com.hrm.DTO.PositionDTO;
import com.hrm.UI.component.CRUDDialog;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Tab quản lý Chức vụ
 */
public class PositionTab extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private PositionDAO positionDAO;
    private CategoryActionRenderer actionRenderer;
    private int hoveredRow = -1;
    private List<PositionDTO> allPositions;
    private JTextField searchField;

    public PositionTab() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        putClientProperty(FlatClientProperties.STYLE, "background: #f8f9fa");
        
        positionDAO = new PositionDAO();
        allPositions = new ArrayList<>();

        add(createButtonPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);

        loadData();
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.putClientProperty(FlatClientProperties.STYLE, "background: #ffffff");
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton btnAdd = new JButton("Thêm mới");
        btnAdd.putClientProperty(FlatClientProperties.STYLE, "background: #4CAF50; foreground: #ffffff");
        btnAdd.addActionListener(e -> handleAdd());

        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.putClientProperty(FlatClientProperties.STYLE, "background: #2196F3; foreground: #ffffff");
        btnRefresh.addActionListener(e -> loadData());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnRefresh);
        
        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(true);
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        searchPanel.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        
        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchField.setText("Tìm kiếm...");
        searchField.setForeground(new Color(180, 180, 180));
        searchField.setPreferredSize(new Dimension(200, 28));
        
        searchField.addFocusListener(new java.awt.event.FocusListener() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals("Tìm kiếm...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Tìm kiếm...");
                    searchField.setForeground(new Color(180, 180, 180));
                }
            }
        });
        
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { applySearch(); }
            @Override
            public void removeUpdate(DocumentEvent e) { applySearch(); }
            @Override
            public void changedUpdate(DocumentEvent e) { applySearch(); }
        });
        
        searchPanel.add(searchField, BorderLayout.CENTER);
        
        panel.add(buttonPanel, BorderLayout.WEST);
        panel.add(searchPanel, BorderLayout.CENTER);

        return panel;
    }

    private JScrollPane createTablePanel() {
        String[] columns = {"Mã chức vụ", "Tên vị trí", "Phụ cấp chức vụ", "Thao tác"};
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
        PositionEditForm form = new PositionEditForm();
        form.setMaChucVu(positionDAO.generateNextMaChucVu());

        CRUDDialog<PositionDTO> dialog = new CRUDDialog<>(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                "Thêm chức vụ",
                form,
                null
        );
        dialog.setVisible(true);

        PositionDTO result = dialog.getResult();
        if (result != null) {
            if (result.getMaChucVu() == null || result.getMaChucVu().trim().isEmpty()) {
                result.setMaChucVu(positionDAO.generateNextMaChucVu());
            }
            if (positionDAO.addPosition(result)) {
                loadData();
                JOptionPane.showMessageDialog(this, "Thêm chức vụ thành công!");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Thêm chức vụ thất bại!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleEdit(int row) {
        String maChucVu = String.valueOf(tableModel.getValueAt(row, 0));
        PositionDTO dto = positionDAO.getPositionById(maChucVu);

        if (dto != null) {
            PositionEditForm form = new PositionEditForm();
            CRUDDialog<PositionDTO> dialog = new CRUDDialog<>(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    "Chỉnh sửa chức vụ",
                    form,
                    dto
            );
            dialog.setVisible(true);

            PositionDTO result = dialog.getResult();
            if (result != null) {
                if (positionDAO.updatePosition(result)) {
                    loadData();
                    JOptionPane.showMessageDialog(this, "Cập nhật chức vụ thành công!");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Cập nhật chức vụ thất bại!",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void handleDelete(int row) {
        String maChucVu = String.valueOf(tableModel.getValueAt(row, 0));
        int option = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa chức vụ này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            if (positionDAO.deletePosition(maChucVu)) {
                loadData();
                JOptionPane.showMessageDialog(this, "Xóa chức vụ thành công!");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Xóa chức vụ thất bại! Có thể chức vụ đang được sử dụng.",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        allPositions = positionDAO.getAllPositions();

        for (PositionDTO position : allPositions) {
            tableModel.addRow(new Object[]{
                    position.getMaChucVu(),
                    position.getTenViTri(),
                    String.format("%,.0f VND", position.getPhuCapChucVu() != null ? position.getPhuCapChucVu().doubleValue() : 0d),
                    ""
            });
        }
    }

    private void applySearch() {
        String searchText = searchField.getText();
        if (searchText.equals("Tìm kiếm...") || searchText.trim().isEmpty()) {
            loadData();
            return;
        }

        tableModel.setRowCount(0);
        String lowerKeyword = searchText.toLowerCase();

        for (PositionDTO position : allPositions) {
            if ((position.getMaChucVu() != null && position.getMaChucVu().toLowerCase().contains(lowerKeyword)) ||
                (position.getTenViTri() != null && position.getTenViTri().toLowerCase().contains(lowerKeyword))) {
                tableModel.addRow(new Object[]{
                        position.getMaChucVu(),
                        position.getTenViTri(),
                        String.format("%,.0f VND", position.getPhuCapChucVu() != null ? position.getPhuCapChucVu().doubleValue() : 0d),
                        ""
                });
            }
        }
    }
}
=======
package com.hrm.UI.HR.CategoryTab;

import com.formdev.flatlaf.FlatClientProperties;
import com.hrm.DAO.PositionDAO;
import com.hrm.DTO.PositionDTO;
import com.hrm.UI.component.CRUDDialog;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Tab quản lý Chức vụ
 */
public class PositionTab extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private PositionDAO positionDAO;
    private CategoryActionRenderer actionRenderer;
    private int hoveredRow = -1;
    private List<PositionDTO> allPositions;
    private JTextField searchField;

    public PositionTab() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        putClientProperty(FlatClientProperties.STYLE, "background: #f8f9fa");
        
        positionDAO = new PositionDAO();
        allPositions = new ArrayList<>();

        add(createButtonPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);

        loadData();
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.putClientProperty(FlatClientProperties.STYLE, "background: #ffffff");
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton btnAdd = new JButton("Thêm mới");
        btnAdd.putClientProperty(FlatClientProperties.STYLE, "background: #4CAF50; foreground: #ffffff");
        btnAdd.addActionListener(e -> handleAdd());

        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.putClientProperty(FlatClientProperties.STYLE, "background: #2196F3; foreground: #ffffff");
        btnRefresh.addActionListener(e -> loadData());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnRefresh);
        
        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(true);
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        searchPanel.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        
        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchField.setText("Tìm kiếm...");
        searchField.setForeground(new Color(180, 180, 180));
        searchField.setPreferredSize(new Dimension(200, 28));
        
        searchField.addFocusListener(new java.awt.event.FocusListener() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals("Tìm kiếm...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Tìm kiếm...");
                    searchField.setForeground(new Color(180, 180, 180));
                }
            }
        });
        
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { applySearch(); }
            @Override
            public void removeUpdate(DocumentEvent e) { applySearch(); }
            @Override
            public void changedUpdate(DocumentEvent e) { applySearch(); }
        });
        
        searchPanel.add(searchField, BorderLayout.CENTER);
        
        panel.add(buttonPanel, BorderLayout.WEST);
        panel.add(searchPanel, BorderLayout.CENTER);

        return panel;
    }

    private JScrollPane createTablePanel() {
        String[] columns = {"Mã chức vụ", "Tên vị trí", "Phụ cấp chức vụ", "Thao tác"};
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
        PositionEditForm form = new PositionEditForm();
        form.setMaChucVu(positionDAO.generateNextMaChucVu());

        CRUDDialog<PositionDTO> dialog = new CRUDDialog<>(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                "Thêm chức vụ",
                form,
                null
        );
        dialog.setVisible(true);

        PositionDTO result = dialog.getResult();
        if (result != null) {
            if (result.getMaChucVu() == null || result.getMaChucVu().trim().isEmpty()) {
                result.setMaChucVu(positionDAO.generateNextMaChucVu());
            }
            if (positionDAO.addPosition(result)) {
                loadData();
                JOptionPane.showMessageDialog(this, "Thêm chức vụ thành công!");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Thêm chức vụ thất bại!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleEdit(int row) {
        String maChucVu = String.valueOf(tableModel.getValueAt(row, 0));
        PositionDTO dto = positionDAO.getPositionById(maChucVu);

        if (dto != null) {
            PositionEditForm form = new PositionEditForm();
            CRUDDialog<PositionDTO> dialog = new CRUDDialog<>(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    "Chỉnh sửa chức vụ",
                    form,
                    dto
            );
            dialog.setVisible(true);

            PositionDTO result = dialog.getResult();
            if (result != null) {
                if (positionDAO.updatePosition(result)) {
                    loadData();
                    JOptionPane.showMessageDialog(this, "Cập nhật chức vụ thành công!");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Cập nhật chức vụ thất bại!",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void handleDelete(int row) {
        String maChucVu = String.valueOf(tableModel.getValueAt(row, 0));
        int option = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa chức vụ này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            if (positionDAO.deletePosition(maChucVu)) {
                loadData();
                JOptionPane.showMessageDialog(this, "Xóa chức vụ thành công!");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Xóa chức vụ thất bại! Có thể chức vụ đang được sử dụng.",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        allPositions = positionDAO.getAllPositions();

        for (PositionDTO position : allPositions) {
            tableModel.addRow(new Object[]{
                    position.getMaChucVu(),
                    position.getTenViTri(),
                    String.format("%,.0f VND", position.getPhuCapChucVu() != null ? position.getPhuCapChucVu().doubleValue() : 0d),
                    ""
            });
        }
    }

    private void applySearch() {
        String searchText = searchField.getText();
        if (searchText.equals("Tìm kiếm...") || searchText.trim().isEmpty()) {
            loadData();
            return;
        }

        tableModel.setRowCount(0);
        String lowerKeyword = searchText.toLowerCase();

        for (PositionDTO position : allPositions) {
            if ((position.getMaChucVu() != null && position.getMaChucVu().toLowerCase().contains(lowerKeyword)) ||
                (position.getTenViTri() != null && position.getTenViTri().toLowerCase().contains(lowerKeyword))) {
                tableModel.addRow(new Object[]{
                        position.getMaChucVu(),
                        position.getTenViTri(),
                        String.format("%,.0f VND", position.getPhuCapChucVu() != null ? position.getPhuCapChucVu().doubleValue() : 0d),
                        ""
                });
            }
        }
    }
}
>>>>>>> 1cfb281dfcc017364337a4c66ee412a5e8d7de17
