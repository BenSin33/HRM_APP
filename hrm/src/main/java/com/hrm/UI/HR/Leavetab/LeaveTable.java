package com.hrm.UI.HR.Leavetab;

import com.hrm.DAO.HR.LeaveDao;
import com.hrm.DTO.HR.LeaveDTO.LeaveRowDTO;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * LeaveTable – bảng danh sách đơn nghỉ phép.
 *
 * Dữ liệu load từ DB qua LeaveDao.getAllLeaveRows(filter).
 * Sau khi duyệt / từ chối → ghi DB + gọi onDataChanged để Summary refresh.
 */
public class LeaveTable extends JPanel {

    // ── Màu ──────────────────────────────────────────────────────
    private static final Color PURPLE      = new Color(124,  58, 237);
    private static final Color GRAY900     = new Color( 17,  24,  39);
    private static final Color GRAY500     = new Color(107, 114, 128);
    private static final Color GRAY200     = new Color(229, 231, 235);
    private static final Color ROW_SEP     = new Color(243, 244, 246);

    private static final Color PENDING_BG  = new Color(254, 243, 199); private static final Color PENDING_FG  = new Color(161,  98,   7);
    private static final Color APPROVED_BG = new Color(220, 252, 231); private static final Color APPROVED_FG = new Color( 21, 128,  61);
    private static final Color REJECTED_BG = new Color(254, 226, 226); private static final Color REJECTED_FG = new Color(185,  28,  28);

    private DefaultTableModel model;
    private JTable table;
    private String activeFilter = "all";

    // Tab buttons – giữ reference để update style & label
    private JButton btnAll, btnPending, btnApproved, btnRejected;

    private final LeaveDao dao = new LeaveDao();
    private List<LeaveRowDTO> currentData;

    /**
     * Callback báo LeaveManagement refresh Summary sau khi duyệt/từ chối.
     * Truyền vào từ LeaveManagement.
     */
    private final Runnable onDataChanged;

    // ─────────────────────────────────────────────────────────────
    public LeaveTable(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);

        JPanel card = new JPanel(new BorderLayout(0, 0));
        card.setBackground(Color.WHITE);
        card.putClientProperty("FlatLaf.style",
            "arc:16; background:#FFFFFF; border:1,1,1,1,#E5E7EB; shadow:sm");

        card.add(buildFilterTabs(), BorderLayout.NORTH);
        card.add(buildTablePanel(), BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);

        // Load lần đầu
        refreshData();
    }

    /** Constructor không callback – tương thích ngược */
    public LeaveTable() { this(null); }

    // ─────────────────────────────────────────────────────────────
    // PUBLIC API
    // ─────────────────────────────────────────────────────────────
    /**
     * Load lại bảng từ DB theo activeFilter hiện tại.
     * Gọi từ bên ngoài (LeaveManagement) sau khi dữ liệu thay đổi.
     */
    public void refreshData() {
        new SwingWorker<List<LeaveRowDTO>, Void>() {
            @Override protected List<LeaveRowDTO> doInBackground() {
                return dao.getAllLeaveRows(activeFilter);
            }
            @Override protected void done() {
                try {
                    currentData = get();
                    rebuildModel(currentData);
                    refreshTabLabels();
                } catch (Exception e) {
                    System.err.println("[LeaveTable] refreshData: " + e.getMessage());
                }
            }
        }.execute();
    }

    // ─────────────────────────────────────────────────────────────
    // FILTER TABS
    // ─────────────────────────────────────────────────────────────
    private JPanel buildFilterTabs() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 12));
        bar.setBackground(Color.WHITE);
        bar.setBorder(new MatteBorder(0, 0, 1, 0, GRAY200));

        btnAll      = makeTabButton("Tất cả",   "all",      true);
        btnPending  = makeTabButton("Chờ duyệt","pending",  false);
        btnApproved = makeTabButton("Đã duyệt", "approved", false);
        btnRejected = makeTabButton("Từ chối",  "rejected", false);

        JButton[] tabs = {btnAll, btnPending, btnApproved, btnRejected};
        for (JButton tab : tabs) {
            tab.addActionListener(e -> {
                activeFilter = tab.getActionCommand();
                for (JButton t : tabs) applyTabStyle(t, t.getActionCommand().equals(activeFilter));
                refreshData();
            });
            bar.add(tab);
        }
        return bar;
    }

    /** Cập nhật số đếm trên label mỗi tab dựa trên toàn bộ data */
    private void refreshTabLabels() {
        new SwingWorker<List<LeaveRowDTO>, Void>() {
            @Override protected List<LeaveRowDTO> doInBackground() {
                return dao.getAllLeaveRows("all");
            }
            @Override protected void done() {
                try {
                    List<LeaveRowDTO> all = get();
                    long pending  = all.stream().filter(d -> "Chờ duyệt".equals(d.trangThai)).count();
                    long approved = all.stream().filter(d -> "Đã duyệt".equals(d.trangThai)).count();
                    long rejected = all.stream().filter(d -> "Từ chối".equals(d.trangThai)).count();
                    btnAll.setText("Tất cả");
                    btnPending.setText("Chờ duyệt (" + pending + ")");
                    btnApproved.setText("Đã duyệt (" + approved + ")");
                    btnRejected.setText("Từ chối (" + rejected + ")");
                } catch (Exception ignored) {}
            }
        }.execute();
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
            btn.putClientProperty("FlatLaf.style", "arc:20; background:#7C3AED; borderWidth:0");
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(GRAY500);
            btn.putClientProperty("FlatLaf.style",
                "arc:20; background:#FFFFFF; border:1,1,1,1,#E5E7EB");
        }
        btn.repaint();
    }

    // ─────────────────────────────────────────────────────────────
    // TABLE
    // ─────────────────────────────────────────────────────────────
    private JScrollPane buildTablePanel() {
        String[] cols = {"MÃ ĐƠN","NHÂN VIÊN","LOẠI NGHỈ","TỪ NGÀY","ĐẾN NGÀY","SỐ NGÀY","LÝ DO","TRẠNG THÁI","THAO TÁC"};

        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

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

        int[] widths = {100, 150, 130, 100, 100, 80, 200, 110, 100};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        table.getColumnModel().getColumn(0).setCellRenderer(new LeaveIdRenderer());
        table.getColumnModel().getColumn(1).setCellRenderer(new EmployeeRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new BoldCenterRenderer());
        table.getColumnModel().getColumn(7).setCellRenderer(new StatusBadgeRenderer());
        table.getColumnModel().getColumn(8).setCellRenderer(new ActionRenderer());

        DefaultTableCellRenderer defaultRend = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, focus, row, col);
                setBackground(sel ? new Color(245,243,255) : Color.WHITE);
                setForeground(GRAY900);
                setVerticalAlignment(SwingConstants.CENTER);
                setBorder(new CompoundBorder(
                    new MatteBorder(0, 0, 1, 0, ROW_SEP),
                    BorderFactory.createEmptyBorder(0, 4, 0, 4)));
                return this;
            }
        };
        table.setDefaultRenderer(Object.class, defaultRend);

        // ── Click duyệt / từ chối ─────────────────────────────────
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col != 8 || row < 0 || currentData == null) return;

                String status = (String) model.getValueAt(row, 7);
                if (!"Chờ duyệt".equals(status)) return;

                // Lấy mã đơn từ model
                String maDon = (String) model.getValueAt(row, 0);
                LeaveRowDTO dto = currentData.stream()
                        .filter(d -> d.maNghiPhep.equals(maDon))
                        .findFirst().orElse(null);
                if (dto == null) return;

                // Xác định click ✓ hay ✕ theo vị trí X trong cell
                Rectangle cellRect  = table.getCellRect(row, col, false);
                boolean   isApprove = (e.getX() - cellRect.x) < cellRect.width / 2;

                String action = isApprove ? "Duyệt" : "Từ chối";
                int confirm = JOptionPane.showConfirmDialog(
                        LeaveTable.this,
                        action + " đơn " + maDon + " của " + dto.hoTen + "?",
                        "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;

                // Ghi DB
                boolean ok = dao.updateStatus(maDon, isApprove, "HR Admin");
                if (ok) {
                    refreshData();
                    if (onDataChanged != null) onDataChanged.run();
                } else {
                    JOptionPane.showMessageDialog(LeaveTable.this,
                            "Cập nhật thất bại, vui lòng thử lại.",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
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
    // MODEL HELPERS
    // ─────────────────────────────────────────────────────────────
    private void rebuildModel(List<LeaveRowDTO> data) {
        model.setRowCount(0);
        if (data == null) return;
        for (LeaveRowDTO dto : data) model.addRow(dto.toTableRow());
    }

    // ─────────────────────────────────────────────────────────────
    // CELL RENDERERS
    // ─────────────────────────────────────────────────────────────

    /** Mã đơn in đậm, căn giữa dọc */
    class LeaveIdRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean focus, int row, int col) {
            super.getTableCellRendererComponent(t, v, sel, focus, row, col);
            setText("<html><b>" + v + "</b></html>");
            setForeground(GRAY900);
            setBackground(sel ? new Color(245,243,255) : Color.WHITE);
            setVerticalAlignment(SwingConstants.CENTER);
            setBorder(new CompoundBorder(
                new MatteBorder(0,0,1,0,ROW_SEP),
                BorderFactory.createEmptyBorder(0,16,0,4)));
            return this;
        }
    }

    /** Nhân viên: tên đậm + phòng ban xám – GridBagLayout căn giữa dọc */
    class EmployeeRenderer implements TableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean focus, int row, int col) {
            String[] arr = (String[]) v;

            JPanel cell = new JPanel(new GridBagLayout());
            cell.setOpaque(true);
            cell.setBackground(sel ? new Color(245,243,255) : Color.WHITE);
            cell.setBorder(new CompoundBorder(
                new MatteBorder(0,0,1,0,ROW_SEP),
                BorderFactory.createEmptyBorder(0,4,0,4)));

            JPanel inner = new JPanel();
            inner.setOpaque(false);
            inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

            JLabel name = new JLabel(arr[0]);
            name.setFont(name.getFont().deriveFont(Font.BOLD, 13f));
            name.setForeground(GRAY900);

            JLabel dept = new JLabel(arr[1]);
            dept.setFont(dept.getFont().deriveFont(Font.PLAIN, 11f));
            dept.setForeground(GRAY500);

            inner.add(name);
            inner.add(Box.createVerticalStrut(3));
            inner.add(dept);
            cell.add(inner);
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
            setVerticalAlignment(SwingConstants.CENTER);
            setBorder(new MatteBorder(0,0,1,0,ROW_SEP));
            return this;
        }
    }

    /** Badge trạng thái bo tròn – GridBagLayout căn giữa dọc */
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

    /**
     * ActionRenderer – icon ✓ và ✕ căn giữa hoàn toàn.
     *
     * FIX căn giữa:
     *   cell ngoài  → GridBagLayout  (căn giữa theo chiều DỌC)
     *   iconRow bên trong → FlowLayout CENTER (căn giữa theo chiều NGANG)
     *   Kết quả: icon nằm đúng trung tâm ô dù row height bao nhiêu.
     */
    class ActionRenderer implements TableCellRenderer {
        private final Color GREEN_IC = new Color( 22, 163,  74);
        private final Color RED_IC   = new Color(220,  38,  38);

        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean focus, int row, int col) {
            String status = v == null ? "" : v.toString();

            // Outer cell: GridBagLayout = căn giữa DỌC tự động
            JPanel cell = new JPanel(new GridBagLayout());
            cell.setBackground(sel ? new Color(245,243,255) : Color.WHITE);
            cell.setBorder(new MatteBorder(0,0,1,0,ROW_SEP));

            if ("Chờ duyệt".equals(status)) {
                // Inner row: FlowLayout CENTER = căn giữa NGANG
                JPanel iconRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
                iconRow.setOpaque(false);
                iconRow.add(makeIconBtn(GREEN_IC, true));   // ✓
                iconRow.add(makeIconBtn(RED_IC,   false));  // ✕
                // GridBagLayout không có constraint → đặt iconRow đúng trung tâm
                cell.add(iconRow);
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
                    // Vòng tròn bao ngoài
                    g2.drawOval(1, 1, w-2, h-2);
                    if (isCheck) {
                        // Dấu ✓ – tọa độ tính từ tâm vòng tròn
                        g2.drawPolyline(
                            new int[]{w/2-5, w/2,   w/2+6},
                            new int[]{h/2,   h/2+5, h/2-4}, 3);
                    } else {
                        // Dấu ✕ – tọa độ tính từ tâm vòng tròn
                        g2.drawLine(w/2-4, h/2-4, w/2+4, h/2+4);
                        g2.drawLine(w/2+4, h/2-4, w/2-4, h/2+4);
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