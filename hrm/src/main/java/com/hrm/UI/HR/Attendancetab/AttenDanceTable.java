package com.hrm.UI.HR.Attendancetab;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;

/**
 * Bảng danh sách nhân viên chấm công.
 * Nhận Consumer<Object[]> onDetailClick – gọi khi user bấm "Chi tiết".
 */
public class AttenDanceTable extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;

    // Toàn bộ dữ liệu để filter
    private final Object[][] fullData = {
        {"Nguyễn Văn A",  "NV001", "Senior Dev",    "Kỹ thuật",   10, 5, 2},
        {"Trần Thị B",    "NV002", "HR Manager",    "Nhân sự",    12, 4, 1},
        {"Lê Văn C",      "NV003", "Sales Lead",    "Kinh doanh", 13, 5, 1},
        {"Phạm Thị D",    "NV004", "Accountant",    "Tài chính",  11, 2, 0},
        {"Hoàng Văn E",   "NV005", "Backend Dev",   "Kỹ thuật",   14, 1, 0},
        {"Ngô Thị F",     "NV006", "Recruiter",     "Nhân sự",     9, 6, 3},
        {"Đặng Văn G",    "NV007", "Sales Rep",     "Kinh doanh", 12, 3, 1},
    };

    private final Consumer<Object[]> onDetailClick;

    // ─── Màu ─────────────────────────────────────────────────────
    private static final Color ORANGE  = new Color(249, 115,  22);
    private static final Color BLUE    = new Color( 59, 130, 246);
    private static final Color RED     = new Color(239,  68,  68);
    private static final Color PURPLE  = new Color(124,  58, 237);
    private static final Color GRAY500 = new Color(107, 114, 128);
    private static final Color GRAY900 = new Color( 17,  24,  39);
    private static final Color BORDER  = new Color(229, 231, 235);
    private static final Color ROW_SEP = new Color(243, 244, 246);

    // ─────────────────────────────────────────────────────────────
    public AttenDanceTable(Consumer<Object[]> onDetailClick) {
        this.onDetailClick = onDetailClick;
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.putClientProperty("FlatLaf.style",
                "arc:16; background:#FFFFFF; borderColor:#E5E7EB; borderWidth:1; shadow:sm");

        card.add(buildToolbar(),    BorderLayout.NORTH);
        card.add(buildTablePanel(), BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);
    }

    /** Constructor không callback – tương thích ngược */
    public AttenDanceTable() {
        this(null);
    }

    // ─────────────────────────────────────────────────────────────
    // TOOLBAR
    // ─────────────────────────────────────────────────────────────
    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new BorderLayout(12, 0));
        bar.setOpaque(false);
        bar.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));

        searchField = new JTextField();
        searchField.putClientProperty("FlatLaf.style",
                "arc:20; background:#F9FAFB; borderColor:#E5E7EB");
        searchField.setPreferredSize(new Dimension(320, 36));

        String placeholder = "  🔍  Tìm kiếm nhân viên...";
        searchField.setText(placeholder);
        searchField.setForeground(GRAY500);
        searchField.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (searchField.getText().equals(placeholder)) {
                    searchField.setText(""); searchField.setForeground(GRAY900);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (searchField.getText().isBlank()) {
                    searchField.setText(placeholder); searchField.setForeground(GRAY500);
                }
            }
        });

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { filterTable(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });

        JButton filterBtn = makeOutlineButton("🔽  Lọc", GRAY500, BORDER);
        JButton exportBtn = makeOutlineButton("📊  Xuất Excel", new Color(22, 163, 74), new Color(187, 247, 208));
        exportBtn.setForeground(new Color(22, 163, 74));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(filterBtn);
        right.add(exportBtn);

        bar.add(searchField, BorderLayout.WEST);
        bar.add(right,       BorderLayout.EAST);
        return bar;
    }

    // ─────────────────────────────────────────────────────────────
    // TABLE
    // ─────────────────────────────────────────────────────────────
    private JScrollPane buildTablePanel() {
        String[] cols = {"NHÂN VIÊN", "PHÒNG BAN", "NGÀY CÔNG", "ĐI MUỘN", "NGHỈ PHÉP", "KHÔNG PHÉP", "HÀNH ĐỘNG"};

        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        populateModel(fullData);

        table = new JTable(model);
        table.setRowHeight(72);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(Color.WHITE);
        table.setFocusable(false);
        table.setCursor(Cursor.getDefaultCursor());

        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.WHITE);
        header.setForeground(GRAY500);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 12f));
        header.setBorder(new MatteBorder(0, 0, 1, 0, BORDER));
        header.setReorderingAllowed(false);

        int[] widths = {200, 110, 90, 80, 90, 100, 90};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        table.getColumnModel().getColumn(0).setCellRenderer(new EmployeeCellRenderer());
        table.getColumnModel().getColumn(3).setCellRenderer(new ColoredNumberRenderer(ORANGE));
        table.getColumnModel().getColumn(4).setCellRenderer(new ColoredNumberRenderer(BLUE));
        table.getColumnModel().getColumn(5).setCellRenderer(new ColoredNumberRenderer(RED));
        table.getColumnModel().getColumn(6).setCellRenderer(new ActionCellRenderer());

        // Default renderer cho cột không custom
        DefaultTableCellRenderer centerRend = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, focus, row, col);
                setBackground(Color.WHITE); setForeground(GRAY900);
                setBorder(new MatteBorder(0, 0, 1, 0, ROW_SEP));
                setHorizontalAlignment(CENTER);
                return this;
            }
        };
        table.setDefaultRenderer(Object.class, centerRend);

        // Click vào "Chi tiết"
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col == 6 && row >= 0 && onDetailClick != null) {
                    // Tìm row trong fullData theo model row
                    String[] nameId = (String[]) model.getValueAt(row, 0);
                    for (Object[] d : fullData) {
                        if (d[0].equals(nameId[0])) {
                            onDetailClick.accept(d);
                            break;
                        }
                    }
                }
            }
            @Override public void mouseMoved(MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                table.setCursor(col == 6
                        ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                        : Cursor.getDefaultCursor());
            }
        });
        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                table.setCursor(col == 6
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
        String q = searchField.getText().toLowerCase().trim();
        model.setRowCount(0);
        for (Object[] row : fullData) {
            if (q.isEmpty()
                    || ((String)row[0]).toLowerCase().contains(q)
                    || ((String)row[1]).toLowerCase().contains(q)
                    || ((String)row[3]).toLowerCase().contains(q)) {
                populateModelRow(row);
            }
        }
    }

    private void populateModel(Object[][] data) {
        for (Object[] row : data) populateModelRow(row);
    }

    private void populateModelRow(Object[] d) {
        model.addRow(new Object[]{
            new String[]{(String)d[0], (String)d[1]},   // name + id
            d[3], d[4], d[5], d[6], 0 /*nghỉ phép mock*/, "Chi tiết"
        });
    }

    // ─────────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────────
    private JButton makeOutlineButton(String text, Color fg, Color border) {
        JButton btn = new JButton(text);
        btn.setForeground(fg); btn.setBackground(Color.WHITE);
        btn.setFont(btn.getFont().deriveFont(Font.PLAIN, 13f));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("FlatLaf.style",
                "arc:8; background:#FFFFFF; borderColor:" + toHex(border) + "; borderWidth:1");
        btn.setPreferredSize(new Dimension(130, 36));
        return btn;
    }

    private String toHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    // ─────────────────────────────────────────────────────────────
    // CELL RENDERERS
    // ─────────────────────────────────────────────────────────────
    static class EmployeeCellRenderer implements TableCellRenderer {
        private static final Color[] COLORS = {
            new Color( 99, 102, 241), new Color( 20, 184, 166),
            new Color(249, 115,  22), new Color(239,  68,  68),
            new Color( 16, 185, 129), new Color(139,  92, 246),
            new Color(236,  72, 153)
        };
        @Override public Component getTableCellRendererComponent(JTable t, Object value,
                boolean sel, boolean focus, int row, int col) {
            String[] arr = (String[]) value;
            String name = arr[0], id = arr[1];
            String[] parts = name.trim().split("\\s+");
            String initials = parts.length >= 2
                    ? "" + parts[0].charAt(0) + parts[parts.length-1].charAt(0)
                    : name.substring(0, Math.min(2, name.length()));
            Color avatarColor = COLORS[row % COLORS.length];

            JPanel cell = new JPanel(new BorderLayout(12, 0));
            cell.setBackground(Color.WHITE);
            cell.setBorder(new CompoundBorder(
                    new MatteBorder(0, 0, 1, 0, new Color(243,244,246)),
                    BorderFactory.createEmptyBorder(8, 16, 8, 0)));

            JLabel avatar = new JLabel(initials.toUpperCase(), SwingConstants.CENTER) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D)g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(avatarColor); g2.fillOval(0,0,getWidth(),getHeight());
                    g2.dispose(); super.paintComponent(g);
                }
            };
            avatar.setForeground(Color.WHITE);
            avatar.setFont(avatar.getFont().deriveFont(Font.BOLD, 13f));
            avatar.setOpaque(false);
            avatar.setPreferredSize(new Dimension(40,40));
            avatar.setMinimumSize(new Dimension(40,40));

            JPanel avatarWrap = new JPanel(new GridBagLayout());
            avatarWrap.setOpaque(false);
            avatarWrap.add(avatar);

            JLabel nameLabel = new JLabel(name);
            nameLabel.setForeground(new Color(17,24,39));
            nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 13f));

            JLabel idLabel = new JLabel(id);
            idLabel.setForeground(new Color(156,163,175));
            idLabel.setFont(idLabel.getFont().deriveFont(Font.PLAIN, 11f));

            JPanel text = new JPanel();
            text.setOpaque(false);
            text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
            text.add(nameLabel);
            text.add(Box.createVerticalStrut(2));
            text.add(idLabel);

            cell.add(avatarWrap, BorderLayout.WEST);
            cell.add(text,       BorderLayout.CENTER);
            return cell;
        }
    }

    static class ColoredNumberRenderer extends DefaultTableCellRenderer {
        private final Color color;
        ColoredNumberRenderer(Color c) { this.color = c; setHorizontalAlignment(CENTER); }
        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean focus, int row, int col) {
            super.getTableCellRendererComponent(t, v, sel, focus, row, col);
            setForeground(color); setFont(getFont().deriveFont(Font.BOLD, 14f));
            setBackground(Color.WHITE);
            setBorder(new MatteBorder(0, 0, 1, 0, new Color(243,244,246)));
            return this;
        }
    }

    static class ActionCellRenderer extends DefaultTableCellRenderer {
        ActionCellRenderer() { setHorizontalAlignment(CENTER); }
        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean focus, int row, int col) {
            super.getTableCellRendererComponent(t, v, sel, focus, row, col);
            setText("<html><u>Chi tiết</u></html>");
            setForeground(new Color(124, 58, 237));
            setFont(getFont().deriveFont(Font.BOLD, 13f));
            setBackground(Color.WHITE);
            setBorder(new MatteBorder(0, 0, 1, 0, new Color(243,244,246)));
            return this;
        }
    }
}