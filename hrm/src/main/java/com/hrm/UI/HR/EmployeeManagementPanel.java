package com.hrm.UI.HR;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class EmployeeManagementPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private List<Object[]> masterData = new ArrayList<>();
    private JTextField searchField;
    private JComboBox<String> filterBox;
    private String currentKeyword = "";
    private String currentDept = "Tất cả phòng ban";

    public EmployeeManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(createHeader(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
    }

    // ================= HEADER =================
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Quản lý nhân viên");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JLabel sub = new JLabel("Toàn quyền CRUD");
        sub.setForeground(Color.GRAY);

        left.add(title);
        left.add(sub);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        searchField = new JTextField(20);
        final String SEARCH_PLACEHOLDER = "Tìm kiếm theo tên, mã NV, email...";
        searchField.setText("");
        filterBox = new JComboBox<>(new String[]{"Tất cả phòng ban", "IT", "Kinh doanh", "Kế toán"});
        JButton addBtn = new JButton("+ Thêm nhân viên");
        addBtn.setBackground(new Color(88, 63, 191));
        addBtn.setForeground(Color.WHITE);

        // Action mở form thêm nhân viên
        addBtn.addActionListener(e -> openAddEmployeeForm());

        // Live search (DocumentListener)
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private void update() {
                String text = searchField.getText().trim();
                if (SEARCH_PLACEHOLDER.equals(text)) text = "";
                currentKeyword = text.toLowerCase();
                currentDept = (String) filterBox.getSelectedItem();
                refreshTableFromMaster(currentKeyword, currentDept);
            }
            @Override public void insertUpdate(DocumentEvent e) { update(); }
            @Override public void removeUpdate(DocumentEvent e) { update(); }
            @Override public void changedUpdate(DocumentEvent e) { update(); }
        });

        // Action lọc phòng ban
        filterBox.addActionListener(e -> {
            currentDept = (String) filterBox.getSelectedItem();
            currentKeyword = searchField.getText().trim().toLowerCase();
            refreshTableFromMaster(currentKeyword, currentDept);
        });

        right.add(searchField);
        right.add(filterBox);
        right.add(addBtn);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);

        return header;
    }

    // ================= TABLE =================
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));

        String[] columns = {"MÃ NV", "HỌ VÀ TÊN", "EMAIL", "PHÒNG BAN", "CHỨC VỤ", "TRẠNG THÁI", "THAO TÁC"};
        Object[][] data = {
                {"NV001", "Nguyễn Văn A", "nva@company.com", "IT", "Developer", "Đang làm"},
                {"NV002", "Trần Thị B", "ttb@company.com", "Kinh doanh", "Sales Manager", "Đang làm"},
                {"NV003", "Lê Văn C", "lvc@company.com", "Kế toán", "Accountant", "Đang làm"},
                {"NV004", "Phạm Thị D", "ptd@company.com", "IT", "Designer", "Nghỉ việc"}
        };

        // load master data
        for (Object[] row : data) {
            masterData.add(row.clone());
        }

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // chỉ cột thao tác có thể chứa nút
            }
        };

        table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        // initial fill
        refreshTableFromMaster("", "Tất cả phòng ban");

        JScrollPane scroll = new JScrollPane(table);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void refreshTableFromMaster(String keyword, String dept) {
        String[] columns = {"MÃ NV", "HỌ VÀ TÊN", "EMAIL", "PHÒNG BAN", "CHỨC VỤ", "TRẠNG THÁI", "THAO TÁC"};
        DefaultTableModel newModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };

        for (Object[] r : masterData) {
            String id = String.valueOf(r[0]).toLowerCase();
            String name = String.valueOf(r[1]).toLowerCase();
            String email = String.valueOf(r[2]).toLowerCase();
            String department = String.valueOf(r[3]);

            boolean matchKeyword = (keyword == null || keyword.isEmpty()) || id.contains(keyword) || name.contains(keyword) || email.contains(keyword);
            boolean matchDept = (dept == null || dept.equals("Tất cả phòng ban")) || department.equals(dept);

            if (matchKeyword && matchDept) {
                Object[] row = new Object[7];
                System.arraycopy(r, 0, row, 0, Math.min(r.length, 6));
                row[6] = ""; // placeholder for action column
                newModel.addRow(row);
            }
        }

        this.model = newModel;
        table.setModel(newModel);
        table.getColumn("THAO TÁC").setCellRenderer(new ButtonRenderer());
        table.getColumn("THAO TÁC").setCellEditor(new ButtonEditor(new JCheckBox()));
        try {
            table.getColumn("THAO TÁC").setMinWidth(110);
            table.getColumn("THAO TÁC").setPreferredWidth(130);
        } catch (Exception ex) { }
    }

    // ================= FORM THÊM NHÂN VIÊN =================
    private void openAddEmployeeForm() {
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField deptField = new JTextField();
        JTextField posField = new JTextField();
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Đang làm", "Nghỉ việc"});

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.add(new JLabel("Mã NV:"));
        form.add(idField);
        form.add(new JLabel("Họ và tên:"));
        form.add(nameField);
        form.add(new JLabel("Email:"));
        form.add(emailField);
        form.add(new JLabel("Phòng ban:"));
        form.add(deptField);
        form.add(new JLabel("Chức vụ:"));
        form.add(posField);
        form.add(new JLabel("Trạng thái:"));
        form.add(statusBox);

        int result = JOptionPane.showConfirmDialog(null, form,
                "Thêm nhân viên mới", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            Object[] newRow = new Object[]{
                    idField.getText(),
                    nameField.getText(),
                    emailField.getText(),
                    deptField.getText(),
                    posField.getText(),
                    String.valueOf(statusBox.getSelectedItem())
            };
            masterData.add(newRow);
            refreshTableFromMaster(currentKeyword, currentDept);
        }
    }

    // ================= BUTTON RENDERER =================
    class ButtonRenderer extends JPanel implements TableCellRenderer {
        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        }

        private JButton makeIconButton(String text) {
            JButton b = new JButton(text);
            b.setMargin(new java.awt.Insets(0, 0, 0, 0));
            b.setPreferredSize(new java.awt.Dimension(28, 28));
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setContentAreaFilled(false);
            return b;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            removeAll();
            add(makeIconButton("👁️"));
            add(makeIconButton("✏️"));
            add(makeIconButton("🗑️"));
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return this;
        }
    }

    // ================= BUTTON EDITOR =================
    class ButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton viewBtn, editBtn, deleteBtn;
        private int currentRow = -1;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

            viewBtn = createEditorButton("👁️");
            editBtn = createEditorButton("✏️");
            deleteBtn = createEditorButton("🗑️");

            viewBtn.addActionListener(e -> {
                fireEditingStopped();
                showEmployeeDetailsByViewRow(currentRow);
            });

            editBtn.addActionListener(e -> {
                fireEditingStopped();
                openEditEmployeeFormByViewRow(currentRow);
            });

            deleteBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(null,
                        "Bạn có chắc muốn xóa nhân viên này?",
                        "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    deleteEmployeeByViewRow(currentRow);
                }
                fireEditingStopped();
            });

            panel.add(viewBtn);
            panel.add(editBtn);
            panel.add(deleteBtn);
        }

        private JButton createEditorButton(String text) {
            JButton b = new JButton(text);
            b.setMargin(new java.awt.Insets(0, 0, 0, 0));
            b.setPreferredSize(new java.awt.Dimension(28, 28));
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setContentAreaFilled(false);
            return b;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            this.currentRow = row;
            panel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }

    // Helpers that operate on masterData by finding matching IDs from view rows
    private void showEmployeeDetailsByViewRow(int viewRow) {
        if (viewRow < 0 || viewRow >= table.getRowCount()) return;
        String id = String.valueOf(table.getModel().getValueAt(viewRow, 0));
        int idx = findMasterIndexById(id);
        if (idx >= 0) showEmployeeDetails(idx);
    }

    private void openEditEmployeeFormByViewRow(int viewRow) {
        if (viewRow < 0 || viewRow >= table.getRowCount()) return;
        String id = String.valueOf(table.getModel().getValueAt(viewRow, 0));
        int idx = findMasterIndexById(id);
        if (idx >= 0) openEditEmployeeForm(idx);
    }

    private void deleteEmployeeByViewRow(int viewRow) {
        if (viewRow < 0 || viewRow >= table.getRowCount()) return;
        String id = String.valueOf(table.getModel().getValueAt(viewRow, 0));
        int idx = findMasterIndexById(id);
        if (idx >= 0) {
            masterData.remove(idx);
            refreshTableFromMaster(currentKeyword, currentDept);
        }
    }

    private int findMasterIndexById(String id) {
        for (int i = 0; i < masterData.size(); i++) {
            Object[] r = masterData.get(i);
            if (r.length > 0 && String.valueOf(r[0]).equals(id)) return i;
        }
        return -1;
    }

    // ================= VIEW / EDIT HELPERS =================
    private void showEmployeeDetails(int masterIndex) {
        if (masterIndex < 0 || masterIndex >= masterData.size()) return;
        Object[] r = masterData.get(masterIndex);
        String id = String.valueOf(r[0]);
        String name = String.valueOf(r[1]);
        String email = String.valueOf(r[2]);
        String dept = String.valueOf(r[3]);
        String pos = String.valueOf(r[4]);
        String status = String.valueOf(r[5]);

        String message = String.format("Mã NV: %s\nHọ và tên: %s\nEmail: %s\nPhòng ban: %s\nChức vụ: %s\nTrạng thái: %s",
                id, name, email, dept, pos, status);
        JOptionPane.showMessageDialog(null, message, "Chi tiết nhân viên", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openEditEmployeeForm(int masterIndex) {
        if (masterIndex < 0 || masterIndex >= masterData.size()) return;
        Object[] r = masterData.get(masterIndex);
        JTextField idField = new JTextField(String.valueOf(r[0]));
        JTextField nameField = new JTextField(String.valueOf(r[1]));
        JTextField emailField = new JTextField(String.valueOf(r[2]));
        JTextField deptField = new JTextField(String.valueOf(r[3]));
        JTextField posField = new JTextField(String.valueOf(r[4]));
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Đang làm", "Nghỉ việc"});
        statusBox.setSelectedItem(String.valueOf(r[5]));

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.add(new JLabel("Mã NV:"));
        form.add(idField);
        form.add(new JLabel("Họ và tên:"));
        form.add(nameField);
        form.add(new JLabel("Email:"));
        form.add(emailField);
        form.add(new JLabel("Phòng ban:"));
        form.add(deptField);
        form.add(new JLabel("Chức vụ:"));
        form.add(posField);
        form.add(new JLabel("Trạng thái:"));
        form.add(statusBox);

        int result = JOptionPane.showConfirmDialog(null, form,
                "Sửa nhân viên", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            r[0] = idField.getText();
            r[1] = nameField.getText();
            r[2] = emailField.getText();
            r[3] = deptField.getText();
            r[4] = posField.getText();
            r[5] = String.valueOf(statusBox.getSelectedItem());
            refreshTableFromMaster(currentKeyword, currentDept);
        }
    }

}