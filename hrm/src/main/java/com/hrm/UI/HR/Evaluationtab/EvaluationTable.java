package com.hrm.UI.HR.Evaluationtab;

import com.hrm.DAO.HR.EvaluationDAO;
import com.hrm.DTO.HR.EvaluationDTO;
import com.hrm.DTO.HR.EvaluationPeriodDTO;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class EvaluationTable extends JPanel {

    // ── Màu ──────────────────────────────────────────────────────
    private static final Color PURPLE   = new Color(124,  58, 237);
    private static final Color GRAY900  = new Color( 17,  24,  39);
    private static final Color GRAY500  = new Color(107, 114, 128);
    private static final Color GRAY200  = new Color(229, 231, 235);
    private static final Color ROW_SEP  = new Color(243, 244, 246);

    // Xếp loại badges
    private static final Color XS_BG  = new Color(220, 252, 231); private static final Color XS_FG  = new Color(21, 128, 61);
    private static final Color TOT_BG = new Color(219, 234, 254); private static final Color TOT_FG = new Color(29,  78, 216);
    private static final Color TB_BG  = new Color(254, 243, 199); private static final Color TB_FG  = new Color(161, 98,   7);
    private static final Color KEM_BG = new Color(254, 226, 226); private static final Color KEM_FG = new Color(185, 28,  28);

    // Trạng thái badges
    private static final Color PEND_BG = new Color(254, 243, 199); private static final Color PEND_FG = new Color(161, 98,  7);
    private static final Color DONE_BG = new Color(220, 252, 231); private static final Color DONE_FG = new Color(21, 128, 61);

    // Thưởng/Phạt màu chữ
    private static final Color REWARD_FG  = new Color(21, 128, 61);
    private static final Color PENALTY_FG = new Color(185, 28, 28);
    private static final Color NONE_FG    = new Color(156, 163, 175);

    private DefaultTableModel model;
    private JComboBox<String> periodBox;
    private List<EvaluationPeriodDTO> periods;
    private final EvaluationDAO dao = new EvaluationDAO();

    // Ref tới Summary để refresh stats khi đổi kỳ
    private EvaluationSummary summary;

    public EvaluationTable() {
        this(null);
    }

    public EvaluationTable(EvaluationSummary summary) {
        this.summary = summary;
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);

        // ── Load danh sách đợt từ DB ──────────────────────────────
        periods = dao.getAllPeriods();

        // ── Kỳ đánh giá selector ─────────────────────────────────
        JPanel periodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        periodPanel.setBackground(Color.WHITE);
        periodPanel.putClientProperty("FlatLaf.style",
                "arc:12; background:#FFFFFF; borderColor:#E5E7EB; borderWidth:1");
        periodPanel.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JLabel periodLabel = new JLabel("Kỳ đánh giá:");
        periodLabel.setForeground(GRAY900);
        periodLabel.setFont(periodLabel.getFont().deriveFont(Font.PLAIN, 13f));

        // Build label list từ DB
        String[] labels = periods.stream()
                .map(EvaluationPeriodDTO::getLabel)
                .toArray(String[]::new);

        // Fallback nếu DB chưa có data
        if (labels.length == 0) {
            labels = new String[]{"Chưa có đợt đánh giá"};
        }

        periodBox = new JComboBox<>(labels);
        periodBox.putClientProperty("FlatLaf.style",
                "arc:8; background:#FFFFFF; borderColor:#E5E7EB");
        periodBox.setPreferredSize(new Dimension(130, 32));

        // Khi đổi kỳ → load lại bảng
        periodBox.addActionListener(e -> loadTableData());

        periodPanel.add(periodLabel);
        periodPanel.add(periodBox);

        // ── Bảng card ─────────────────────────────────────────────
        JPanel card = new JPanel(new BorderLayout(0, 0));
        card.setBackground(Color.WHITE);
        card.putClientProperty("FlatLaf.style",
                "arc:16; background:#FFFFFF; borderColor:#E5E7EB; borderWidth:1; shadow:sm");
        card.add(buildTablePanel(), BorderLayout.CENTER);

        JPanel wrapper = new JPanel(new BorderLayout(0, 12));
        wrapper.setOpaque(false);
        wrapper.add(periodPanel, BorderLayout.NORTH);
        wrapper.add(card,        BorderLayout.CENTER);

        add(wrapper, BorderLayout.CENTER);

        // Load data lần đầu
        loadTableData();
    }

    // ─────────────────────────────────────────────────────────────
    // RELOAD KHI TẠO ĐỢT MỚI
    // ─────────────────────────────────────────────────────────────
    public void reloadPeriods() {
        periods = dao.getAllPeriods();
        periodBox.removeAllItems();
        for (EvaluationPeriodDTO p : periods) {
            periodBox.addItem(p.getLabel());
        }
        if (periodBox.getItemCount() > 0) {
            periodBox.setSelectedIndex(0);
        }
        loadTableData();
    }

    // ─────────────────────────────────────────────────────────────
    // LOAD DATA TỪ DB
    // ─────────────────────────────────────────────────────────────
    private void loadTableData() {
        model.setRowCount(0); // Xóa data cũ

        int idx = periodBox.getSelectedIndex();
        if (periods == null || periods.isEmpty() || idx < 0 || idx >= periods.size()) return;

        String maDot = periods.get(idx).getMaDot();
        List<EvaluationDTO> list = dao.getEvaluationsByPeriod(maDot);

        for (EvaluationDTO dto : list) {
            model.addRow(dto.toTableRow());
        }

        // Cập nhật summary cards theo kỳ mới
        if (summary != null) summary.refreshStats(maDot);
    }

    // ─────────────────────────────────────────────────────────────
    // TABLE
    // ─────────────────────────────────────────────────────────────
    private JScrollPane buildTablePanel() {
        String[] cols = {"MÃ NV", "NHÂN VIÊN", "NGƯỜI ĐÁNH GIÁ", "ĐIỂM SỐ", "XẾP LOẠI", "THƯỞNG/PHẠT", "TRẠNG THÁI"};

        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(model);
        table.setRowHeight(80);
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

        // Widths
        int[] widths = {80, 180, 130, 110, 110, 160, 120};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Renderers
        table.getColumnModel().getColumn(0).setCellRenderer(new IdRenderer());
        table.getColumnModel().getColumn(1).setCellRenderer(new EmployeeRenderer());
        table.getColumnModel().getColumn(3).setCellRenderer(new ScoreRenderer());
        table.getColumnModel().getColumn(4).setCellRenderer(new RankBadgeRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new RewardRenderer());
        table.getColumnModel().getColumn(6).setCellRenderer(new StatusBadgeRenderer());

        // Default renderer
        DefaultTableCellRenderer defRend = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, focus, row, col);
                setBackground(sel ? new Color(245, 243, 255) : Color.WHITE);
                setForeground(GRAY900);
                setVerticalAlignment(SwingConstants.CENTER);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(new CompoundBorder(
                    new MatteBorder(0, 0, 1, 0, ROW_SEP),
                    BorderFactory.createEmptyBorder(0, 8, 0, 8)));
                return this;
            }
        };
        table.setDefaultRenderer(Object.class, defRend);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    // ─────────────────────────────────────────────────────────────
    // CELL RENDERERS
    // ─────────────────────────────────────────────────────────────

    class IdRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean focus, int row, int col) {
            super.getTableCellRendererComponent(t, v, sel, focus, row, col);
            setText("<html><b>" + v + "</b></html>");
            setForeground(GRAY900);
            setBackground(sel ? new Color(245, 243, 255) : Color.WHITE);
            setVerticalAlignment(SwingConstants.CENTER);
            setHorizontalAlignment(SwingConstants.CENTER);
            setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, ROW_SEP),
                BorderFactory.createEmptyBorder(0, 8, 0, 8)));
            return this;
        }
    }

    class EmployeeRenderer implements TableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean focus, int row, int col) {
            String[] arr = (String[]) v;

            JPanel cell = new JPanel();
            cell.setOpaque(true);
            cell.setBackground(sel ? new Color(245, 243, 255) : Color.WHITE);
            cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));
            cell.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, ROW_SEP),
                BorderFactory.createEmptyBorder(12, 8, 12, 8)));

            JLabel name = new JLabel(arr[0]);
            name.setFont(name.getFont().deriveFont(Font.BOLD, 13f));
            name.setForeground(GRAY900);

            JLabel pos = new JLabel(arr[1]);
            pos.setFont(pos.getFont().deriveFont(Font.PLAIN, 12f));
            pos.setForeground(GRAY500);

            JLabel team = new JLabel(arr[2]);
            team.setFont(team.getFont().deriveFont(Font.PLAIN, 11f));
            team.setForeground(new Color(156, 163, 175));

            cell.add(name);
            cell.add(Box.createVerticalStrut(2));
            cell.add(pos);
            cell.add(Box.createVerticalStrut(1));
            cell.add(team);
            return cell;
        }
    }

    class ScoreRenderer implements TableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean focus, int row, int col) {
            Object[] arr = (Object[]) v;
            int score = (Integer) arr[0];

            Color scoreColor = "Kém".equals(arr[1].toString())
                    ? new Color(239, 68, 68) : PURPLE;

            JPanel cell = new JPanel(new GridBagLayout());
            cell.setBackground(sel ? new Color(245, 243, 255) : Color.WHITE);
            cell.setBorder(new MatteBorder(0, 0, 1, 0, ROW_SEP));

            JPanel inner = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            inner.setOpaque(false);

            JLabel big = new JLabel(String.valueOf(score));
            big.setFont(big.getFont().deriveFont(Font.BOLD, 26f));
            big.setForeground(scoreColor);
            big.setVerticalAlignment(SwingConstants.BOTTOM);

            JLabel small = new JLabel(" /100");
            small.setFont(small.getFont().deriveFont(Font.PLAIN, 12f));
            small.setForeground(GRAY500);
            small.setVerticalAlignment(SwingConstants.BOTTOM);
            small.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0));

            inner.add(big);
            inner.add(small);
            cell.add(inner);
            return cell;
        }
    }

    class RankBadgeRenderer implements TableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean focus, int row, int col) {
            String rank = v == null ? "" : v.toString();

            JPanel cell = new JPanel(new GridBagLayout());
            cell.setBackground(sel ? new Color(245, 243, 255) : Color.WHITE);
            cell.setBorder(new MatteBorder(0, 0, 1, 0, ROW_SEP));

            Color bg, fg;
            switch (rank) {
                case "Xuất sắc":   bg = XS_BG;  fg = XS_FG;  break;
                case "Tốt":        bg = TOT_BG; fg = TOT_FG; break;
                case "Trung bình": bg = TB_BG;  fg = TB_FG;  break;
                default:           bg = KEM_BG; fg = KEM_FG; break;
            }

            JLabel badge = new JLabel(rank, SwingConstants.CENTER) {
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
            badge.setBorder(BorderFactory.createEmptyBorder(4, 14, 4, 14));

            cell.add(badge);
            return cell;
        }
    }

    class RewardRenderer implements TableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean focus, int row, int col) {
            String[] arr = (String[]) v;
            String reward = arr[0];

            JPanel cell = new JPanel(new GridBagLayout());
            cell.setBackground(sel ? new Color(245, 243, 255) : Color.WHITE);
            cell.setBorder(new MatteBorder(0, 0, 1, 0, ROW_SEP));

            if ("Không có".equals(reward)) {
                JLabel lbl = new JLabel("Không có");
                lbl.setForeground(NONE_FG);
                lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN, 13f));
                cell.add(lbl);
                return cell;
            }

            boolean isReward = reward.equals("Tăng lương") || reward.equals("Thưởng");
            Color iconColor = isReward ? REWARD_FG : PENALTY_FG;
            Color textColor = isReward ? REWARD_FG : PENALTY_FG;

            JPanel inner = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
            inner.setOpaque(false);

            JLabel icon = new JLabel(isReward ? makeSmallRibbon(iconColor) : makeSmallWarn(iconColor));
            icon.setVerticalAlignment(SwingConstants.CENTER);

            JLabel text = new JLabel(reward);
            text.setFont(text.getFont().deriveFont(Font.BOLD, 13f));
            text.setForeground(textColor);
            text.setVerticalAlignment(SwingConstants.CENTER);

            inner.add(icon);
            inner.add(text);
            cell.add(inner);
            return cell;
        }

        private Icon makeSmallRibbon(Color c) {
            return new Icon() {
                public void paintIcon(Component comp, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(c);
                    g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawOval(x+2, y+1, 12, 10);
                    g2.drawLine(x+5,  y+10, x+3,  y+16);
                    g2.drawLine(x+11, y+10, x+13, y+16);
                    g2.drawLine(x+5,  y+10, x+8,  y+13);
                    g2.drawLine(x+11, y+10, x+8,  y+13);
                    g2.dispose();
                }
                public int getIconWidth()  { return 18; }
                public int getIconHeight() { return 18; }
            };
        }

        private Icon makeSmallWarn(Color c) {
            return new Icon() {
                public void paintIcon(Component comp, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(c);
                    g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawPolygon(new int[]{x+8, x+1, x+15}, new int[]{y+1, y+15, y+15}, 3);
                    g2.drawLine(x+8, y+6, x+8, y+11);
                    g2.fillOval(x+6, y+13, 3, 3);
                    g2.dispose();
                }
                public int getIconWidth()  { return 18; }
                public int getIconHeight() { return 18; }
            };
        }
    }

    class StatusBadgeRenderer implements TableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean focus, int row, int col) {
            String status = v == null ? "" : v.toString();

            JPanel cell = new JPanel(new GridBagLayout());
            cell.setBackground(sel ? new Color(245, 243, 255) : Color.WHITE);
            cell.setBorder(new MatteBorder(0, 0, 1, 0, ROW_SEP));

            if ("-".equals(status)) {
                JLabel dash = new JLabel("-");
                dash.setForeground(NONE_FG);
                cell.add(dash);
                return cell;
            }

            Color bg = "Đã duyệt".equals(status) ? DONE_BG : PEND_BG;
            Color fg = "Đã duyệt".equals(status) ? DONE_FG : PEND_FG;

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
}