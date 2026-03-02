package com.hrm.UI.HR.Leavetab;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class LeaveTable extends JPanel {

    // ── Màu ──────────────────────────────────────────────────────
    private static final Color PURPLE      = new Color(124,  58, 237);
    private static final Color PURPLE_LIGHT= new Color(237, 233, 254);
    private static final Color GRAY900     = new Color( 17,  24,  39);
    private static final Color GRAY500     = new Color(107, 114, 128);
    private static final Color GRAY200     = new Color(229, 231, 235);
    private static final Color ROW_SEP     = new Color(243, 244, 246);

    // Badge màu
    private static final Color PENDING_BG  = new Color(254, 243, 199);
    private static final Color PENDING_FG  = new Color(161,  98,   7);
    private static final Color APPROVED_BG = new Color(220, 252, 231);
    private static final Color APPROVED_FG = new Color( 21, 128,  61);
    private static final Color REJECTED_BG = new Color(254, 226, 226);
    private static final Color REJECTED_FG = new Color(185,  28,  28);

    private DefaultTableModel model;
    private JTable table;
    private String activeFilter = "all"; // all | pending | approved | rejected

    // Dữ liệu mẫu: {id, name, dept, type, from, to, days, reason, status}
    private final Object[][] fullData = {
        {"LEAVE001", "Nguyễn Văn A", "IT",          "Nghỉ phép năm",   "1/2/2025",  "3/2/2025",  3, "Du lịch cùng gia đình",        "Chờ duyệt"},
        {"LEAVE002", "Trần Thị B",   "Kinh doanh",  "Nghỉ ốm",         "25/1/2025", "26/1/2025", 2, "Bệnh cảm cúm",                  "Đã duyệt"},
        {"LEAVE003", "Lê Văn C",     "Kế toán",     "Nghỉ việc riêng", "10/2/2025", "10/2/2025", 1, "Giải quyết công việc cá nhân",  "Chờ duyệt"},
    };

    public LeaveTable() {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);

        JPanel card = new JPanel(new BorderLayout(0, 0));
        card.setBackground(Color.WHITE);
        card.putClientProperty("FlatLaf.style",
                "arc:16; background:#FFFFFF; borderColor:#E5E7EB; borderWidth:1; shadow:sm");

        card.add(buildFilterTabs(), BorderLayout.NORTH);
        card.add(buildTablePanel(), BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);
    }

    // ─────────────────────────────────────────────────────────────
    // FILTER TABS: Tất cả | Chờ duyệt (2) | Đã duyệt | Từ chối
    // ─────────────────────────────────────────────────────────────
    private JPanel buildFilterTabs() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 12));
        bar.setBackground(Color.WHITE);
        bar.setBorder(new MatteBorder(0, 0, 1, 0, GRAY200));

        long pending  = countByStatus("Chờ duyệt");
        long approved = countByStatus("Đã duyệt");
        long rejected = countByStatus("Từ chối");

        JButton btnAll      = makeTabButton("Tất cả",                  "all",      true);
        JButton btnPending  = makeTabButton("Chờ duyệt (" + pending + ")", "pending",  false);
        JButton btnApproved = makeTabButton("Đã duyệt",                "approved", false);
        JButton btnRejected = makeTabButton("Từ chối",                 "rejected", false);

        JButton[] tabs = {btnAll, btnPending, btnApproved, btnRejected};

        for (JButton tab : tabs) {
            tab.addActionListener(e -> {
                activeFilter = tab.getActionCommand();
                // update style
                for (JButton t : tabs) {
                    boolean active = t.getActionCommand().equals(activeFilter);
                    applyTabStyle(t, active);
                }
                filterTable();
            });
            bar.add(tab);
        }

        return bar;
    }

    private JButton makeTabButton(String text, String cmd, boolean active) {
        JButton btn = new JButton(text);
        btn.setActionCommand(cmd);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(btn.getFont().deriveFont(Font.PLAIN, 13f));
        applyTabStyle(btn, active);
        return btn;
    }

    private void applyTabStyle(JButton btn, boolean active) {
        if (active) {
            btn.setBackground(PURPLE);
            btn.setForeground(Color.WHITE);
            btn.putClientProperty("FlatLaf.style",
                    "arc:20; background:#7C3AED; borderWidth:0");
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(GRAY500);
            btn.putClientProperty("FlatLaf.style",
                    "arc:20; background:#FFFFFF; borderColor:#E5E7EB; borderWidth:1");
        }
        btn.repaint();
    }

    // ─────────────────────────────────────────────────────────────
    // TABLE
    // ─────────────────────────────────────────────────────────────
    private JScrollPane buildTablePanel() {
        String[] cols = {"MÃ ĐƠN", "NHÂN VIÊN", "LOẠI NGHỈ", "TỪ NGÀY", "ĐẾN NGÀY", "SỐ NGÀY", "LÝ DO", "TRẠNG THÁI", "THAO TÁC"};

        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        populateModel(fullData);

        table = new JTable(model);
        table.setRowHeight(64);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(Color.WHITE);
        table.setFocusable(false);
        table.setSelectionBackground(new Color(245, 243, 255));

        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.WHITE);
        header.setForeground(GRAY500);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 11f));
        header.setBorder(new MatteBorder(1, 0, 1, 0, GRAY200));
        header.setReorderingAllowed(false);

        // Column widths
        int[] widths = {100, 150, 130, 100, 100, 80, 200, 110, 100};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Renderers
        table.getColumnModel().getColumn(0).setCellRenderer(new LeaveIdRenderer());
        table.getColumnModel().getColumn(1).setCellRenderer(new EmployeeRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new BoldCenterRenderer());
        table.getColumnModel().getColumn(7).setCellRenderer(new StatusBadgeRenderer());
        table.getColumnModel().getColumn(8).setCellRenderer(new ActionRenderer());

        // Default renderer cho các cột còn lại
        DefaultTableCellRenderer defaultRend = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, focus, row, col);
                setBackground(sel ? new Color(245,243,255) : Color.WHITE);
                setForeground(GRAY900);
                setBorder(new MatteBorder(0, 0, 1, 0, ROW_SEP));
                setHorizontalAlignment(LEFT);
                setBorder(new CompoundBorder(
                    new MatteBorder(0, 0, 1, 0, ROW_SEP),
                    BorderFactory.createEmptyBorder(0, 4, 0, 4)));
                return this;
            }
        };
        table.setDefaultRenderer(Object.class, defaultRend);

        // Click approve/reject
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col != 8 || row < 0) return;

                String status = (String) model.getValueAt(row, 7);
                if (!"Chờ duyệt".equals(status)) return;

                // Tính vị trí nút ✓ và ✕ trong cell
                Rectangle cellRect = table.getCellRect(row, col, false);
                int relX = e.getX() - cellRect.x;
                boolean isApprove = relX < cellRect.width / 2;

                String newStatus = isApprove ? "Đã duyệt" : "Từ chối";
                String msg = isApprove
                    ? "Duyệt đơn " + model.getValueAt(row, 0) + "?"
                    : "Từ chối đơn " + model.getValueAt(row, 0) + "?";

                int confirm = JOptionPane.showConfirmDialog(
                    LeaveTable.this, msg, "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    // Cập nhật trong fullData
                    String id = (String) model.getValueAt(row, 0);
                    for (Object[] d : fullData) {
                        if (d[0].equals(id)) { d[8] = newStatus; break; }
                    }
                    model.setValueAt(newStatus, row, 7);
                    table.repaint();
                }
            }
            @Override public void mouseMoved(MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                table.setCursor(col == 8
                    ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                    : Cursor.getDefaultCursor());
            }
        });
        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                table.setCursor(col == 8
                    ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                    : Cursor.getDefaultCursor());
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    // ─────────────────────────────────────────────────────────────
    // FILTER
    // ─────────────────────────────────────────────────────────────
    private void filterTable() {
        model.setRowCount(0);
        for (Object[] row : fullData) {
            String status = (String) row[8];
            boolean show = switch (activeFilter) {
                case "pending"  -> "Chờ duyệt".equals(status);
                case "approved" -> "Đã duyệt".equals(status);
                case "rejected" -> "Từ chối".equals(status);
                default         -> true;
            };
            if (show) model.addRow(toRow(row));
        }
    }

    private void populateModel(Object[][] data) {
        for (Object[] row : data) model.addRow(toRow(row));
    }

    private Object[] toRow(Object[] d) {
        return new Object[]{d[0], new String[]{(String)d[1],(String)d[2]},
                d[3], d[4], d[5], d[6], d[7], d[8], d[8]};
    }

    private long countByStatus(String status) {
        long count = 0;
        for (Object[] d : fullData) if (d[8].equals(status)) count++;
        return count;
    }

    // ─────────────────────────────────────────────────────────────
    // CELL RENDERERS
    // ─────────────────────────────────────────────────────────────

    /** Mã đơn in đậm */
    class LeaveIdRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean focus, int row, int col) {
            super.getTableCellRendererComponent(t, v, sel, focus, row, col);
            setText("<html><b>" + v + "</b></html>");
            setForeground(GRAY900);
            setBackground(sel ? new Color(245,243,255) : Color.WHITE);
            setBorder(new CompoundBorder(
                new MatteBorder(0,0,1,0,ROW_SEP),
                BorderFactory.createEmptyBorder(0,16,0,4)));
            return this;
        }
    }

    /** Nhân viên: tên đậm + phòng ban nhỏ xám */
    class EmployeeRenderer implements TableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean focus, int row, int col) {
            String[] arr = (String[]) v;
            JPanel cell = new JPanel();
            cell.setOpaque(true);
            cell.setBackground(sel ? new Color(245,243,255) : Color.WHITE);
            cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));
            cell.setBorder(new CompoundBorder(
                new MatteBorder(0,0,1,0,ROW_SEP),
                BorderFactory.createEmptyBorder(10,4,10,4)));

            JLabel name = new JLabel(arr[0]);
            name.setFont(name.getFont().deriveFont(Font.BOLD, 13f));
            name.setForeground(GRAY900);

            JLabel dept = new JLabel(arr[1]);
            dept.setFont(dept.getFont().deriveFont(Font.PLAIN, 11f));
            dept.setForeground(GRAY500);

            cell.add(name);
            cell.add(Box.createVerticalStrut(3));
            cell.add(dept);
            return cell;
        }
    }

    /** Số ngày: in đậm + căn giữa */
    class BoldCenterRenderer extends DefaultTableCellRenderer {
        BoldCenterRenderer() { setHorizontalAlignment(CENTER); }
        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean focus, int row, int col) {
            super.getTableCellRendererComponent(t, v, sel, focus, row, col);
            setText("<html><b>" + v + "</b></html>");
            setForeground(GRAY900);
            setBackground(sel ? new Color(245,243,255) : Color.WHITE);
            setBorder(new MatteBorder(0,0,1,0,ROW_SEP));
            return this;
        }
    }

    /** Badge trạng thái: Chờ duyệt / Đã duyệt / Từ chối */
    class StatusBadgeRenderer implements TableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean focus, int row, int col) {
            String status = v == null ? "" : v.toString();

            JPanel cell = new JPanel(new GridBagLayout());
            cell.setBackground(sel ? new Color(245,243,255) : Color.WHITE);
            cell.setBorder(new MatteBorder(0,0,1,0,ROW_SEP));

            Color bg, fg;
            switch (status) {
                case "Đã duyệt" -> { bg = APPROVED_BG; fg = APPROVED_FG; }
                case "Từ chối"  -> { bg = REJECTED_BG; fg = REJECTED_FG; }
                default          -> { bg = PENDING_BG;  fg = PENDING_FG;  }
            }

            JLabel badge = new JLabel(status, SwingConstants.CENTER) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            badge.setOpaque(false);
            badge.setBackground(bg);
            badge.setForeground(fg);
            badge.setFont(badge.getFont().deriveFont(Font.PLAIN, 12f));
            badge.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));

            cell.add(badge);
            return cell;
        }
    }

    /** Nút thao tác ✓ ✕ (chỉ hiện khi Chờ duyệt) */
    class ActionRenderer implements TableCellRenderer {
        private final Color GREEN_IC = new Color( 22, 163,  74);
        private final Color RED_IC   = new Color(220,  38,  38);

        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean focus, int row, int col) {
            String status = v == null ? "" : v.toString();

            JPanel cell = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
            cell.setBackground(sel ? new Color(245,243,255) : Color.WHITE);
            cell.setBorder(new MatteBorder(0,0,1,0,ROW_SEP));

            if ("Chờ duyệt".equals(status)) {
                cell.add(makeIconBtn(GREEN_IC, true));
                cell.add(makeIconBtn(RED_IC,   false));
            }
            return cell;
        }

        private JLabel makeIconBtn(Color color, boolean isCheck) {
            JLabel lbl = new JLabel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(color);
                    g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    int w = getWidth(), h = getHeight();
                    g2.drawOval(1, 1, w-2, h-2);
                    if (isCheck) {
                        g2.drawPolyline(
                            new int[]{w/2-5, w/2, w/2+7},
                            new int[]{h/2,   h/2+5, h/2-5}, 3);
                    } else {
                        g2.drawLine(w/2-5, h/2-5, w/2+5, h/2+5);
                        g2.drawLine(w/2+5, h/2-5, w/2-5, h/2+5);
                    }
                    g2.dispose();
                }
            };
            lbl.setPreferredSize(new Dimension(26, 26));
            lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return lbl;
        }
    }
}