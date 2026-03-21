package com.hrm.UI.Manager.team;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TeamTable extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private EmployeeClickListener employeeClickListener;

    public interface EmployeeClickListener {
        void onEmployeeClicked(String maNV, String hoTen);
    }

    public TeamTable() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true));

        String[] columns = {"MÃ NV", "HỌ VÀ TÊN", "CHỨC VỤ", "LIÊN HỆ", "NGÀY VÀO LÀM", "TRẠNG THÁI", "THAO TÁC"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(74);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(245, 245, 255));
        table.setFocusable(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // ===== HEADER =====
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setPreferredSize(new Dimension(0, 48));
        tableHeader.setReorderingAllowed(false);
        tableHeader.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                          boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value != null ? value.toString() : "");
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                label.setForeground(new Color(150, 150, 150));
                label.setBackground(Color.WHITE);
                label.setOpaque(true);

                // Canh giữa cho cột trạng thái và thao tác, còn lại canh trái
                if (column == 5 || column == 6) {
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    label.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                            BorderFactory.createEmptyBorder(0, 0, 0, 0)
                    ));
                } else {
                    label.setHorizontalAlignment(SwingConstants.LEFT);
                    label.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                            BorderFactory.createEmptyBorder(0, 15, 0, 0)
                    ));
                }
                return label;
            }
        });

        // ===== RENDERERS =====
        table.getColumnModel().getColumn(0).setCellRenderer(new MaNVRenderer());
        table.getColumnModel().getColumn(3).setCellRenderer(new LienHeRenderer());
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultLeftRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new TrangThaiRenderer());
        table.getColumnModel().getColumn(6).setCellRenderer(new ActionRenderer());

        // ===== COLUMN WIDTHS (đẹp, giống hình) =====
        TableColumnModel cm = table.getColumnModel();
        cm.getColumn(0).setPreferredWidth(80);   // Mã NV
        cm.getColumn(1).setPreferredWidth(190);  // Họ và tên
        cm.getColumn(2).setPreferredWidth(90);   // Chức vụ
        cm.getColumn(3).setPreferredWidth(260);  // Liên hệ
        cm.getColumn(4).setPreferredWidth(120);  // Ngày vào làm
        cm.getColumn(5).setPreferredWidth(120);  // Trạng thái
        cm.getColumn(6).setPreferredWidth(80);   // Thao tác

        // ===== CLICK HANDLING =====
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row < 0 || col < 0) return;

                // Click icon con mắt -> mở dialog chi tiết đúng hàng
                if (col == 6 && SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
                    showEmployeeDetails(row);
                    return;
                }

                // Double-click hàng (tuỳ bạn dùng chỗ khác)
                if (employeeClickListener != null && e.getClickCount() >= 2) {
                    String maNV = safeToString(tableModel.getValueAt(row, 0));
                    String hoTen = safeToString(tableModel.getValueAt(row, 1));
                    employeeClickListener.onEmployeeClicked(maNV, hoTen);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void setData(Object[][] data) {
        tableModel.setRowCount(0);
        for (Object[] row : data) {
            tableModel.addRow(row);
        }
    }

    public String getSelectedEmployeeId() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            return safeToString(tableModel.getValueAt(row, 0));
        }
        return null;
    }

    public void setEmployeeClickListener(EmployeeClickListener listener) {
        this.employeeClickListener = listener;
    }

    private void showEmployeeDetails(int row) {
        String maNV = safeToString(tableModel.getValueAt(row, 0));
        String hoTen = safeToString(tableModel.getValueAt(row, 1));
        String chucVu = safeToString(tableModel.getValueAt(row, 2));
        String lienHe = safeToString(tableModel.getValueAt(row, 3));
        String ngayVaoLam = safeToString(tableModel.getValueAt(row, 4));
        String trangThai = safeToString(tableModel.getValueAt(row, 5));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel title = new JLabel("Thông tin nhân viên");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(new Color(60, 60, 60));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;

        addRow(panel, gbc, 1, "Mã NV", maNV);
        addRow(panel, gbc, 2, "Họ và tên", hoTen);
        addRow(panel, gbc, 3, "Chức vụ", chucVu);
        addRow(panel, gbc, 4, "Liên hệ", lienHe);
        addRow(panel, gbc, 5, "Ngày vào làm", ngayVaoLam);
        addRow(panel, gbc, 6, "Trạng thái", trangThai);

        Window w = SwingUtilities.getWindowAncestor(this);
        JOptionPane.showMessageDialog(
                w,
                panel,
                "Chi tiết nhân viên",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int y, String label, String value) {
        JLabel l = new JLabel(label + ":");
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(new Color(90, 90, 90));

        JLabel v = new JLabel(toHtmlMultiline(value));
        v.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        v.setForeground(new Color(60, 60, 60));

        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        panel.add(l, gbc);

        gbc.gridx = 1; gbc.gridy = y; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(v, gbc);
    }

    private String safeToString(Object o) {
        return o == null ? "" : String.valueOf(o);
    }

    private String toHtmlMultiline(String s) {
        if (s == null) return "";
        String esc = s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        esc = esc.replace("\r\n", "\n").replace("\r", "\n").replace("\n", "<br>");
        return "<html>" + esc + "</html>";
    }

    // ===== Renderer classes =====

    private static class DefaultLeftRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                    BorderFactory.createEmptyBorder(0, 15, 0, 0)
            ));
            label.setBackground(isSelected ? new Color(245, 245, 255) : Color.WHITE);
            return label;
        }
    }

    private static class MaNVRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setFont(new Font("Segoe UI", Font.BOLD, 13));
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                    BorderFactory.createEmptyBorder(0, 15, 0, 0)
            ));
            label.setBackground(isSelected ? new Color(245, 245, 255) : Color.WHITE);
            return label;
        }
    }

    private static class LienHeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel();
            label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            label.setForeground(new Color(90, 90, 90));
            label.setOpaque(true);
            label.setBackground(isSelected ? new Color(245, 245, 255) : Color.WHITE);
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                    BorderFactory.createEmptyBorder(6, 15, 6, 0)
            ));

            String s = value == null ? "" : value.toString();
            s = s.replace("\r\n", "\n").replace("\r", "\n").replace("\n", "<br>");
            label.setText("<html>" + s + "</html>");
            return label;
        }
    }

    private static class TrangThaiRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 22));
            panel.setBackground(isSelected ? new Color(245, 245, 255) : Color.WHITE);
            panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

            String text = value != null ? value.toString() : "";
            JLabel badge = new JLabel(text);
            badge.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            badge.setOpaque(true);

            // Màu theo trạng thái (nếu không khớp thì dùng xanh)
            if (text.toLowerCase().contains("đang")) {
                badge.setBackground(new Color(220, 252, 231));
                badge.setForeground(new Color(22, 163, 74));
            } else if (text.toLowerCase().contains("nghỉ")) {
                badge.setBackground(new Color(243, 244, 246));
                badge.setForeground(new Color(107, 114, 128));
            } else {
                badge.setBackground(new Color(220, 252, 231));
                badge.setForeground(new Color(22, 163, 74));
            }

            badge.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
            panel.add(badge);
            return panel;
        }
    }

    private static class ActionRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 22));
            panel.setBackground(isSelected ? new Color(245, 245, 255) : Color.WHITE);
            panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

            JLabel icon = new JLabel("👁");
            icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
            icon.setForeground(new Color(168, 85, 247));
            panel.add(icon);

            return panel;
        }
    }
}