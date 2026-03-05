package com.hrm.UI.Manager.ScheduleTab;
import com.hrm.UI.Manager.color.ColorScheme;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;

public class ScheduleTable extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;

    public ScheduleTable() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true));

        String[] columns = {"Nhân viên", "T2", "T3", "T4", "T5", "T6", "T7", "CN"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(90);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(new Color(248, 248, 248));
        table.setSelectionBackground(new Color(245, 245, 255));
        table.setFocusable(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Header
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setPreferredSize(new Dimension(0, 55));
        tableHeader.setReorderingAllowed(false);
        tableHeader.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value != null ? value.toString() : "", SwingConstants.CENTER);
                label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                label.setForeground(new Color(100, 100, 100));
                label.setBackground(Color.WHITE);
                label.setOpaque(true);
                label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(230, 230, 230)));
                return label;
            }
        });

        // Renderers
        table.getColumnModel().getColumn(0).setCellRenderer(new NhanVienRenderer());
        table.getColumnModel().getColumn(0).setPreferredWidth(180);

        CaLamRenderer caLamRenderer = new CaLamRenderer();
        for (int i = 1; i <= 7; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(caLamRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(248, 248, 248));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, BorderLayout.CENTER);
    }
    public void loadTableData(Object[][] data) {
        tableModel.setRowCount(0);
        for (Object[] row : data) {
            tableModel.addRow(row);
        }
    }

    public void setData(Object[][] data) {
        tableModel.setRowCount(0);
        for (Object[] row : data) {
            tableModel.addRow(row);
        }
    }

    public void updateWeekHeaders(LocalDate currentMonday) {
        String[] days = {"T2", "T3", "T4", "T5", "T6", "T7", "CN"};
        for (int i = 0; i < 7; i++) {
            LocalDate date = currentMonday.plusDays(i);
            String header = "<html><center>" + days[i] + "<br><b>"
                    + date.getDayOfMonth() + "/" + date.getMonthValue()
                    + "</b></center></html>";
            table.getColumnModel().getColumn(i + 1).setHeaderValue(header);
        }
        table.getTableHeader().repaint();
    }
}
