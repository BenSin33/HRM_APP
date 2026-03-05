package com.hrm.UI.Manager.team;
import com.hrm.UI.Manager.color.ColorScheme;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class TeamTable extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;

    public TeamTable() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true));

        String[] columns = {"MÃ NV", "HỌ VÀ TÊN", "CHỨC VỤ", "LIÊN HỆ", "NGÀY VÀO LÀM", "TRẠNG THÁI", ""};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(70);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(245, 245, 255));
        table.setFocusable(false);

        // Table Header
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setPreferredSize(new Dimension(0, 45));
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
                label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                    BorderFactory.createEmptyBorder(0, 15, 0, 0)
                ));
                return label;
            }
        });

        // Set cell renderers
        table.getColumnModel().getColumn(0).setCellRenderer(new MaNVRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new TrangThaiRenderer());
        table.getColumnModel().getColumn(6).setCellRenderer(new ActionRenderer());

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(200);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        table.getColumnModel().getColumn(6).setPreferredWidth(50);

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
            return tableModel.getValueAt(row, 0).toString();
        }
        return null;
    }

    // Renderer classes
    private static class MaNVRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            label.setFont(new Font("Segoe UI", Font.BOLD, 14));
            label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(0, 15, 0, 0)
            ));
            label.setBackground(isSelected ? new Color(245, 245, 255) : Color.WHITE);
            return label;
        }
    }

    private static class TrangThaiRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 18));
            panel.setBackground(isSelected ? new Color(245, 245, 255) : Color.WHITE);
            panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

            JLabel badge = new JLabel(value != null ? value.toString() : "");
            badge.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            badge.setOpaque(true);
            badge.setBackground(new Color(220, 252, 231));
            badge.setForeground(new Color(22, 163, 74));
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