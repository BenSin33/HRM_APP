package com.hrm.UI.HR.Attendancetab;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;

/**
 * Panel chi tiết chấm công của 1 nhân viên.
 * Hiển thị: header nhân viên (avatar, tên, stats) + bảng ngày/check-in/check-out/trạng thái.
 *
 * Cách dùng:
 *   AttenDanceDetail detail = new AttenDanceDetail(employeeData, onBack);
 *
 * employeeData là mảng Object[] = {name, id, position, dept, workDays, late, absent}
 * onBack là Runnable gọi lại khi bấm nút ◀ để quay về bảng tổng.
 */
public class AttenDanceDetail extends JPanel {

    // ── Màu ──────────────────────────────────────────────────────
    private static final Color GRAY900  = new Color( 17,  24,  39);
    private static final Color GRAY500  = new Color(107, 114, 128);
    private static final Color GRAY200  = new Color(229, 231, 235);
    private static final Color ROW_SEP  = new Color(243, 244, 246);

    // Trạng thái badge
    private static final Color LATE_BG     = new Color(254, 243, 199);
    private static final Color LATE_FG     = new Color(161,  98,   7);
    private static final Color ABSENT_BG   = new Color(254, 226, 226);
    private static final Color ABSENT_FG   = new Color(185,  28,  28);
    private static final Color OK_BG       = new Color(220, 252, 231);
    private static final Color OK_FG       = new Color( 21, 128,  61);
    private static final Color HOLIDAY_BG  = new Color(243, 244, 246);
    private static final Color HOLIDAY_FG  = new Color(107, 114, 128);
    private static final Color LEAVE_BG    = new Color(219, 234, 254);
    private static final Color LEAVE_FG    = new Color( 29,  78, 216);

    // ─────────────────────────────────────────────────────────────
    public AttenDanceDetail(Object[] emp, Runnable onBack) {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);

        // Card trắng bao toàn bộ
        JPanel card = new JPanel(new BorderLayout(0, 0));
        card.setBackground(Color.WHITE);
        card.putClientProperty("FlatLaf.style",
                "arc:16; background:#FFFFFF; borderColor:#E5E7EB; borderWidth:1; shadow:sm");

        card.add(buildEmployeeHeader(emp, onBack), BorderLayout.NORTH);
        card.add(buildDetailTable(emp),            BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);
    }

    // ─────────────────────────────────────────────────────────────
    // HEADER: ◀  [Avatar] Tên / ID - Vị trí       11 Ngày | 4 Muộn | 3 Vắng
    // ─────────────────────────────────────────────────────────────
    private JPanel buildEmployeeHeader(Object[] emp, Runnable onBack) {
        String name     = (String)  emp[0];
        String id       = (String)  emp[1];
        String position = (String)  emp[2];
        int workDays    = (Integer) emp[4];
        int late        = (Integer) emp[5];
        int absent      = (Integer) emp[6];

        JPanel header = new JPanel(new BorderLayout(0, 0));
        header.setBackground(Color.WHITE);
        header.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, GRAY200),
                BorderFactory.createEmptyBorder(16, 20, 16, 24)));

        // ── Nút back ◀ ───────────────────────────────────────────
        JButton backBtn = new JButton("◀");
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setForeground(GRAY500);
        backBtn.setFont(backBtn.getFont().deriveFont(Font.PLAIN, 16f));
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> onBack.run());

        // ── Avatar ───────────────────────────────────────────────
        String initials = getInitials(name);
        Color avatarColor = pickColor(id);
        JLabel avatar = new JLabel(initials, SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(avatarColor);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setForeground(Color.WHITE);
        avatar.setFont(avatar.getFont().deriveFont(Font.BOLD, 16f));
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(48, 48));
        avatar.setMinimumSize(new Dimension(48, 48));
        avatar.setHorizontalAlignment(SwingConstants.CENTER);

        // ── Tên + ID ─────────────────────────────────────────────
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 16f));
        nameLabel.setForeground(GRAY900);

        JLabel idLabel = new JLabel(id + " · " + position);
        idLabel.setForeground(GRAY500);
        idLabel.setFont(idLabel.getFont().deriveFont(Font.PLAIN, 12f));

        JPanel namePanel = new JPanel();
        namePanel.setOpaque(false);
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.add(nameLabel);
        namePanel.add(Box.createVerticalStrut(3));
        namePanel.add(idLabel);

        // ── Avatar + tên ─────────────────────────────────────────
        JPanel leftInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftInfo.setOpaque(false);
        leftInfo.add(backBtn);
        leftInfo.add(avatar);
        leftInfo.add(namePanel);

        // ── Stats bên phải ───────────────────────────────────────
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 24, 0));
        statsPanel.setOpaque(false);
        statsPanel.add(makeStat(String.valueOf(workDays), "Ngày công",  GRAY900));
        statsPanel.add(makeStat(String.valueOf(late),     "Đi muộn",    new Color(217, 119, 6)));
        statsPanel.add(makeStat(String.valueOf(absent),   "Vắng",       new Color(220, 38, 38)));

        header.add(leftInfo,   BorderLayout.WEST);
        header.add(statsPanel, BorderLayout.EAST);
        return header;
    }

    private JPanel makeStat(String value, String label, Color valueColor) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JLabel val = new JLabel(value);
        val.setFont(val.getFont().deriveFont(Font.BOLD, 18f));
        val.setForeground(valueColor);
        val.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN, 11f));
        lbl.setForeground(GRAY500);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(val);
        p.add(Box.createVerticalStrut(2));
        p.add(lbl);
        return p;
    }

    // ─────────────────────────────────────────────────────────────
    // BẢNG NGÀY CHI TIẾT
    // ─────────────────────────────────────────────────────────────
    private JScrollPane buildDetailTable(Object[] emp) {
        String[] cols = {"NGÀY", "THỨ", "CHECK IN", "CHECK OUT", "CÔNG (GIỜ)", "TRẠNG THÁI"};

        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        // ── Sample data cho tháng 2/2026 ─────────────────────────
        // {ngay, thu, checkIn, checkOut, cong, trangThai}
        // trangThai: "Đúng giờ" | "Đi muộn" | "Vắng mặt" | "Ngày nghỉ" | "Nghỉ phép"
        Object[][] rows = {
            {"31/1/2026", "Bảy",  "--:--", "--:--", "-",   "Ngày nghỉ"},
            {"1/2/2026",  "CN",   "08:45", "17:30", "8.5", "Đi muộn"},
            {"2/2/2026",  "Hai",  "--:--", "--:--", "-",   "Vắng mặt"},
            {"3/2/2026",  "Ba",   "08:45", "17:30", "8.5", "Đi muộn"},
            {"4/2/2026",  "Tư",   "08:00", "17:30", "8.5", "Đúng giờ"},
            {"5/2/2026",  "Năm",  "08:00", "17:30", "8.5", "Đúng giờ"},
            {"6/2/2026",  "Sáu",  "--:--", "--:--", "-",   "Ngày nghỉ"},
            {"7/2/2026",  "Bảy",  "--:--", "--:--", "-",   "Ngày nghỉ"},
            {"8/2/2026",  "CN",   "08:45", "17:30", "8.5", "Đi muộn"},
            {"9/2/2026",  "Hai",  "08:00", "17:30", "8.5", "Đúng giờ"},
            {"10/2/2026", "Ba",   "08:00", "17:30", "8.5", "Đúng giờ"},
            {"11/2/2026", "Tư",   "--:--", "--:--", "-",   "Nghỉ phép"},
            {"12/2/2026", "Năm",  "08:00", "17:30", "8.5", "Đúng giờ"},
            {"13/2/2026", "Sáu",  "08:00", "17:30", "8.5", "Đúng giờ"},
            {"14/2/2026", "Bảy",  "--:--", "--:--", "-",   "Ngày nghỉ"},
            {"15/2/2026", "CN",   "--:--", "--:--", "-",   "Ngày nghỉ"},
            {"16/2/2026", "Hai",  "08:05", "17:30", "8.5", "Đi muộn"},
            {"17/2/2026", "Ba",   "08:00", "17:30", "8.5", "Đúng giờ"},
            {"18/2/2026", "Tư",   "08:00", "17:30", "8.5", "Đúng giờ"},
            {"19/2/2026", "Năm",  "--:--", "--:--", "-",   "Vắng mặt"},
            {"20/2/2026", "Sáu",  "08:00", "17:30", "8.5", "Đúng giờ"},
            {"21/2/2026", "Bảy",  "--:--", "--:--", "-",   "Ngày nghỉ"},
            {"22/2/2026", "CN",   "--:--", "--:--", "-",   "Ngày nghỉ"},
            {"23/2/2026", "Hai",  "08:00", "17:30", "8.5", "Đúng giờ"},
            {"24/2/2026", "Ba",   "08:00", "17:30", "8.5", "Đúng giờ"},
            {"25/2/2026", "Tư",   "08:00", "17:30", "8.5", "Đúng giờ"},
            {"26/2/2026", "Năm",  "08:00", "17:30", "8.5", "Đúng giờ"},
            {"27/2/2026", "Sáu",  "08:00", "17:30", "8.5", "Đúng giờ"},
            {"28/2/2026", "Bảy",  "--:--", "--:--", "-",   "Ngày nghỉ"},
        };

        for (Object[] r : rows) model.addRow(r);

        JTable table = new JTable(model);
        table.setRowHeight(52);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(Color.WHITE);
        table.setFocusable(false);
        table.setSelectionBackground(new Color(245, 243, 255));

        // Header
        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.WHITE);
        header.setForeground(GRAY500);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 11f));
        header.setBorder(new MatteBorder(1, 0, 1, 0, GRAY200));
        header.setReorderingAllowed(false);

        // Column widths
        int[] widths = {120, 80, 120, 120, 120, 150};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Default renderer (date, thu, cong)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, focus, row, col);
                setHorizontalAlignment(CENTER);
                setBackground(sel ? new Color(245, 243, 255) : Color.WHITE);
                setBorder(new MatteBorder(0, 0, 1, 0, ROW_SEP));
                setForeground(GRAY500);
                return this;
            }
        };

        // Check-in / Check-out renderer (bold khi có giờ)
        DefaultTableCellRenderer timeRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, focus, row, col);
                setHorizontalAlignment(CENTER);
                setBackground(sel ? new Color(245, 243, 255) : Color.WHITE);
                setBorder(new MatteBorder(0, 0, 1, 0, ROW_SEP));
                String txt = v == null ? "" : v.toString();
                if (!txt.contains("-")) {
                    setFont(getFont().deriveFont(Font.BOLD, 14f));
                    setForeground(GRAY900);
                } else {
                    setForeground(GRAY500);
                }
                return this;
            }
        };

        // Status badge renderer
        TableCellRenderer statusRenderer = new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean focus, int row, int col) {
                String status = v == null ? "" : v.toString();
                JPanel cell = new JPanel(new GridBagLayout());
                cell.setBackground(sel ? new Color(245, 243, 255) : Color.WHITE);
                cell.setBorder(new MatteBorder(0, 0, 1, 0, ROW_SEP));

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
                badge.setFont(badge.getFont().deriveFont(Font.PLAIN, 12f));
                badge.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));

                switch (status) {
                    case "Đúng giờ": { 
                        badge.setBackground(OK_BG);      
                        badge.setForeground(OK_FG); 
                        break;
                    }
                    case "Đi muộn": { 
                        badge.setBackground(LATE_BG);    
                        badge.setForeground(LATE_FG); 
                        break;
                    }
                    case "Vắng mặt": { 
                        badge.setBackground(ABSENT_BG);  
                        badge.setForeground(ABSENT_FG); 
                        break;
                    }
                    case "Nghỉ phép": { 
                        badge.setBackground(LEAVE_BG);   
                        badge.setForeground(LEAVE_FG); 
                        break;
                    }
                    default: { 
                        badge.setBackground(HOLIDAY_BG); 
                        badge.setForeground(HOLIDAY_FG); 
                        break;
                    }
                }
                cell.add(badge);
                return cell;
            }
        };

        // Apply renderers
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Ngày
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); // Thứ
        table.getColumnModel().getColumn(2).setCellRenderer(timeRenderer);   // Check In
        table.getColumnModel().getColumn(3).setCellRenderer(timeRenderer);   // Check Out
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Công
        table.getColumnModel().getColumn(5).setCellRenderer(statusRenderer); // Trạng thái

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    // ─────────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────────
    private String getInitials(String name) {
        String[] p = name.trim().split("\\s+");
        return p.length >= 2
                ? "" + p[0].charAt(0) + p[p.length - 1].charAt(0)
                : name.substring(0, Math.min(2, name.length()));
    }

    private static final Color[] AVATAR_COLORS = {
        new Color( 99, 102, 241), new Color( 20, 184, 166),
        new Color(249, 115,  22), new Color(239,  68,  68),
        new Color( 16, 185, 129), new Color(139,  92, 246),
        new Color(236,  72, 153)
    };

    private Color pickColor(String id) {
        int hash = Math.abs(id.hashCode());
        return AVATAR_COLORS[hash % AVATAR_COLORS.length];
    }
}