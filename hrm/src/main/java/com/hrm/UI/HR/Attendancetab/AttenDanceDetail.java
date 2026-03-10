package com.hrm.UI.HR.Attendancetab;

import com.hrm.DAO.HR.AttenDanceDao;
import com.hrm.DTO.HR.AttenDanceDTO.DetailHeaderDTO;
import com.hrm.DTO.HR.AttenDanceDTO.DailyRecordDTO;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;

/**
 * AttenDanceDetail – panel chi tiết chấm công của 1 nhân viên.
 *
 * Dữ liệu được load từ DB qua AttenDanceDao.getDetailRecords(manv, month, year).
 *
 * emp = {name, id, position, dept, workDays, late, absent}  (từ EmployeeRowDTO.toObjectArray())
 */
public class AttenDanceDetail extends JPanel {

    private static final Color GRAY900  = new Color( 17,  24,  39);
    private static final Color GRAY500  = new Color(107, 114, 128);
    private static final Color GRAY200  = new Color(229, 231, 235);
    private static final Color ROW_SEP  = new Color(243, 244, 246);

    private static final Color LATE_BG    = new Color(254, 243, 199); private static final Color LATE_FG    = new Color(161, 98,  7);
    private static final Color ABSENT_BG  = new Color(254, 226, 226); private static final Color ABSENT_FG  = new Color(185, 28, 28);
    private static final Color OK_BG      = new Color(220, 252, 231); private static final Color OK_FG      = new Color( 21,128, 61);
    private static final Color HOLIDAY_BG = new Color(243, 244, 246); private static final Color HOLIDAY_FG = new Color(107,114,128);
    private static final Color LEAVE_BG   = new Color(219, 234, 254); private static final Color LEAVE_FG   = new Color( 29, 78,216);

    private final AttenDanceDao dao = new AttenDanceDao();

    // Tham chiếu để update sau khi load DB
    private DefaultTableModel detailModel;
    private JLabel statWorkDays;
    private JLabel statLate;
    private JLabel statAbsent;

    // ─────────────────────────────────────────────────────────────
    /**
     * @param emp     Object[] = {hoTen, manv, chucVu, phongBan, workDays, lateDays, absentDays}
     * @param month   tháng đang xem (để load đúng dữ liệu từ DB)
     * @param year    năm đang xem
     * @param onBack  Runnable gọi khi bấm ◀
     */
    public AttenDanceDetail(Object[] emp, int month, int year, Runnable onBack) {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);

        JPanel card = new JPanel(new BorderLayout(0, 0));
        card.setBackground(Color.WHITE);
        card.putClientProperty("FlatLaf.style",
            "arc:16; background:#FFFFFF; border:1,1,1,1,#E5E7EB; shadow:sm");

        card.add(buildEmployeeHeader(emp, onBack), BorderLayout.NORTH);
        card.add(buildDetailTable(),               BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);

        // Load dữ liệu DB
        String manv = (String) emp[1];
        loadFromDb(manv, month, year);
    }

    /**
     * Constructor tương thích ngược (không có month/year → dùng tháng hiện tại).
     */
    public AttenDanceDetail(Object[] emp, Runnable onBack) {
        this(emp,
             java.time.LocalDate.now().getMonthValue(),
             java.time.LocalDate.now().getYear(),
             onBack);
    }

    // ─────────────────────────────────────────────────────────────
    // LOAD DB
    // ─────────────────────────────────────────────────────────────
    private void loadFromDb(String manv, int month, int year) {
        SwingWorker<DetailHeaderDTO, Void> worker = new SwingWorker<>() {
            @Override
            protected DetailHeaderDTO doInBackground() {
                return dao.getDetailRecords(manv, month, year);
            }

            @Override
            protected void done() {
                try {
                    DetailHeaderDTO dto = get();
                    // Cập nhật stat header
                    statWorkDays.setText(String.valueOf(dto.totalWorkDays));
                    statLate.setText(String.valueOf(dto.totalLate));
                    statAbsent.setText(String.valueOf(dto.totalAbsent));

                    // Cập nhật bảng ngày
                    detailModel.setRowCount(0);
                    for (DailyRecordDTO rec : dto.records) {
                        detailModel.addRow(rec.toTableRow());
                    }
                } catch (Exception e) {
                    System.err.println("[AttenDanceDetail] loadFromDb: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    // ─────────────────────────────────────────────────────────────
    // HEADER
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

        JButton backBtn = new JButton("◀");
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setForeground(GRAY500);
        backBtn.setFont(backBtn.getFont().deriveFont(Font.PLAIN, 16f));
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> onBack.run());

        String initials   = getInitials(name);
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

        JPanel leftInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftInfo.setOpaque(false);
        leftInfo.add(backBtn);
        leftInfo.add(avatar);
        leftInfo.add(namePanel);

        // ── Stats – giữ reference để update sau khi load DB ──────
        statWorkDays = makeStatLabel(String.valueOf(workDays));
        statLate     = makeStatLabel(String.valueOf(late));
        statAbsent   = makeStatLabel(String.valueOf(absent));

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 24, 0));
        statsPanel.setOpaque(false);
        statsPanel.add(wrapStat(statWorkDays, "Ngày công",  GRAY900));
        statsPanel.add(wrapStat(statLate,     "Đi muộn",    new Color(217, 119, 6)));
        statsPanel.add(wrapStat(statAbsent,   "Vắng",       new Color(220, 38, 38)));

        header.add(leftInfo,   BorderLayout.WEST);
        header.add(statsPanel, BorderLayout.EAST);
        return header;
    }

    private JLabel makeStatLabel(String value) {
        JLabel lbl = new JLabel(value);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 18f));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    private JPanel wrapStat(JLabel valueLbl, String label, Color valueColor) {
        valueLbl.setForeground(valueColor);
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JLabel lbl = new JLabel(label);
        lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN, 11f));
        lbl.setForeground(GRAY500);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(valueLbl);
        p.add(Box.createVerticalStrut(2));
        p.add(lbl);
        return p;
    }

    // ─────────────────────────────────────────────────────────────
    // BẢNG CHI TIẾT
    // ─────────────────────────────────────────────────────────────
    private JScrollPane buildDetailTable() {
        String[] cols = {"NGÀY", "THỨ", "CHECK IN", "CHECK OUT", "CÔNG (GIỜ)", "TRẠNG THÁI"};

        detailModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(detailModel);
        table.setRowHeight(52);
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

        int[] widths = {120, 80, 120, 120, 120, 150};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, focus, row, col);
                setHorizontalAlignment(CENTER);
                setBackground(sel ? new Color(245,243,255) : Color.WHITE);
                setBorder(new MatteBorder(0,0,1,0,ROW_SEP));
                setForeground(GRAY500);
                return this;
            }
        };

        DefaultTableCellRenderer timeRenderer = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, focus, row, col);
                setHorizontalAlignment(CENTER);
                setBackground(sel ? new Color(245,243,255) : Color.WHITE);
                setBorder(new MatteBorder(0,0,1,0,ROW_SEP));
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

        TableCellRenderer statusRenderer = (t, v, sel, focus, row, col) -> {
            String status = v == null ? "" : v.toString();
            JPanel cell = new JPanel(new GridBagLayout());
            cell.setBackground(sel ? new Color(245,243,255) : Color.WHITE);
            cell.setBorder(new MatteBorder(0,0,1,0,ROW_SEP));

            JLabel badge = new JLabel(status, SwingConstants.CENTER) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),20,20);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            badge.setOpaque(false);
            badge.setFont(badge.getFont().deriveFont(Font.PLAIN, 12f));
            badge.setBorder(BorderFactory.createEmptyBorder(4,12,4,12));

            switch (status) {
                case "Đúng giờ"  -> { badge.setBackground(OK_BG);      badge.setForeground(OK_FG); }
                case "Đi muộn"   -> { badge.setBackground(LATE_BG);    badge.setForeground(LATE_FG); }
                case "Vắng mặt"  -> { badge.setBackground(ABSENT_BG);  badge.setForeground(ABSENT_FG); }
                case "Nghỉ phép" -> { badge.setBackground(LEAVE_BG);   badge.setForeground(LEAVE_FG); }
                default          -> { badge.setBackground(HOLIDAY_BG); badge.setForeground(HOLIDAY_FG); }
            }
            cell.add(badge);
            return cell;
        };

        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(timeRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(timeRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(statusRenderer);

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
                ? "" + p[0].charAt(0) + p[p.length-1].charAt(0)
                : name.substring(0, Math.min(2, name.length()));
    }

    private static final Color[] AVATAR_COLORS = {
        new Color( 99,102,241), new Color(20,184,166),
        new Color(249,115, 22), new Color(239, 68, 68),
        new Color( 16,185,129), new Color(139, 92,246),
        new Color(236, 72,153)
    };

    private Color pickColor(String id) {
        return AVATAR_COLORS[Math.abs(id.hashCode()) % AVATAR_COLORS.length];
    }
}