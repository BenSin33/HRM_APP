package com.hrm.UI.HR.Attendancetab;

import com.hrm.DAO.HR.AttenDanceDao;
import com.hrm.DTO.HR.AttenDanceDTO.EmployeeRowDTO;
import com.hrm.DTO.UserDTO;
import com.hrm.Service.PermissionService;
import com.hrm.UI.HR.Attendancetab.AttenDanceFilterDialog.FilterCriteria;
import com.hrm.utils.AttendanceExcelExporter;
import com.hrm.utils.SessionManager;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * AttenDanceTable – bảng danh sách nhân viên chấm công.
 *
 * THÊM MỚI:
 *  1. Nút "🔽 Lọc" → mở AttenDanceFilterDialog (lọc theo phòng ban, tình trạng, ngày công)
 *  2. Nút "📊 Xuất Excel" → gọi AttendanceExcelExporter (xuất dữ liệu đang hiển thị)
 *  3. filterTable() kết hợp cả text search VÀ FilterCriteria
 *  4. Hiển thị chip "Đang lọc" khi có filter đang áp dụng
 */
public class AttenDanceTable extends JPanel {

    private JTable            table;
    private DefaultTableModel model;
    private JTextField        searchField;
    private JLabel            filterChip;  // badge "Đang lọc" khi có filter

    // Dữ liệu gốc từ DB (chưa qua filter)
    private List<EmployeeRowDTO> currentData;

    // Filter criteria hiện tại
    private FilterCriteria activeFilter = new FilterCriteria();

    // Tháng/năm đang xem (dùng khi xuất Excel)
    private int currentMonth;
    private int currentYear;

    private final AttenDanceDao      dao = new AttenDanceDao();
    private final PermissionService  permissionService = new PermissionService();
    private final Consumer<Object[]> onDetailClick;
    
    // Permission tracking
    private boolean canViewAttendance = false;
    private boolean canEditAttendance = false;

    // ─── Màu ─────────────────────────────────────────────────────
    private static final Color PURPLE   = new Color(124, 58, 237);
    private static final Color ORANGE   = new Color(249, 115,  22);
    private static final Color BLUE     = new Color( 59, 130, 246);
    private static final Color RED      = new Color(239,  68,  68);
    private static final Color GREEN    = new Color( 22, 163,  74);
    private static final Color GRAY500  = new Color(107, 114, 128);
    private static final Color GRAY900  = new Color( 17,  24,  39);
    private static final Color BORDER   = new Color(229, 231, 235);
    private static final Color ROW_SEP  = new Color(243, 244, 246);

    // ─────────────────────────────────────────────────────────────
    public AttenDanceTable(Consumer<Object[]> onDetailClick) {
        this.onDetailClick = onDetailClick;
        setLayout(new BorderLayout());
        setOpaque(false);

        // Kiểm tra quyền xem và sửa
        checkPermissions();

        // Card vẽ tay (fix borderColor/shadow)
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0,0,0,14));
                g2.fillRoundRect(2,3,getWidth()-1,getHeight()-1,16,16);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0,0,getWidth()-2,getHeight()-2,16,16);
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0,0,getWidth()-3,getHeight()-3,16,16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.add(buildToolbar(),    BorderLayout.NORTH);
        card.add(buildTablePanel(), BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);

        java.time.LocalDate now = java.time.LocalDate.now();
        currentMonth = now.getMonthValue();
        currentYear  = now.getYear();
        refreshData(currentMonth, currentYear);
    }

    /**
     * Kiểm tra quyền xem và sửa chấm công
     */
    private void checkPermissions() {
        UserDTO currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            // CN03_ATTENANCE là mã chức năng quản lý chấm công trong DB
            canViewAttendance = permissionService.canView(currentUser, "CN03_ATTENANCE");
            canEditAttendance = permissionService.canEdit(currentUser, "CN03_ATTENANCE");
        }
        if (!canViewAttendance) {
            System.out.println("⚠ User không có quyền xem chấm công");
        }
        if (!canEditAttendance) {
            System.out.println("⚠ User không có quyền chỉnh sửa chấm công");
        }
    }

    public AttenDanceTable() { this(null); }

    // ─────────────────────────────────────────────────────────────
    // PUBLIC API
    // ─────────────────────────────────────────────────────────────
    public void refreshData(int month, int year) {
        this.currentMonth = month;
        this.currentYear  = year;

        new SwingWorker<List<EmployeeRowDTO>, Void>() {
            @Override protected List<EmployeeRowDTO> doInBackground() {
                return dao.getEmployeeRows(month, year);
            }
            @Override protected void done() {
                try {
                    currentData = get();
                    applyFilters(); // áp dụng cả search + filter criteria
                } catch (Exception e) {
                    System.err.println("[AttenDanceTable] refreshData: " + e.getMessage());
                }
            }
        }.execute();
    }

    // ─────────────────────────────────────────────────────────────
    // TOOLBAR
    // ─────────────────────────────────────────────────────────────
    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new BorderLayout(12, 0));
        bar.setOpaque(false);
        bar.setBorder(new CompoundBorder(
                new MatteBorder(0,0,1,0,BORDER),
                BorderFactory.createEmptyBorder(12,16,12,16)));

        // ── Search ───────────────────────────────────────────────
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftPanel.setOpaque(false);

        searchField = new JTextField();
        searchField.putClientProperty("FlatLaf.style",
                "arc:20; background:#F9FAFB; focusWidth:1; innerFocusWidth:0");
        searchField.setPreferredSize(new Dimension(260, 36));

        final String placeholder = "  🔍  Tìm theo tên, mã NV, phòng ban...";
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
            public void insertUpdate (javax.swing.event.DocumentEvent e) { applyFilters(); }
            public void removeUpdate (javax.swing.event.DocumentEvent e) { applyFilters(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilters(); }
        });

        // Chip hiển thị khi đang có filter
        filterChip = new JLabel("● Đang lọc") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(237,233,254));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        filterChip.setForeground(PURPLE);
        filterChip.setFont(filterChip.getFont().deriveFont(Font.BOLD, 12f));
        filterChip.setBorder(BorderFactory.createEmptyBorder(4,10,4,10));
        filterChip.setOpaque(false);
        filterChip.setVisible(false);
        filterChip.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        filterChip.setToolTipText("Bấm để xóa bộ lọc");
        filterChip.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                activeFilter = new FilterCriteria();
                filterChip.setVisible(false);
                applyFilters();
            }
        });

        leftPanel.add(searchField);
        leftPanel.add(filterChip);

        // ── Buttons phải ─────────────────────────────────────────
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setOpaque(false);

        JButton filterBtn = makeOutlineButton("🔽  Bộ lọc", GRAY500, BORDER);
        filterBtn.addActionListener(e -> openFilterDialog());

        JButton exportBtn = makeOutlineButton("📊  Xuất Excel", GREEN, new Color(187,247,208));
        exportBtn.setForeground(GREEN);
        exportBtn.addActionListener(e -> doExportExcel());

        rightPanel.add(filterBtn);
        rightPanel.add(exportBtn);

        bar.add(leftPanel,  BorderLayout.WEST);
        bar.add(rightPanel, BorderLayout.EAST);
        return bar;
    }

    // ─────────────────────────────────────────────────────────────
    // FILTER DIALOG
    // ─────────────────────────────────────────────────────────────
    private void openFilterDialog() {
        // Lấy danh sách phòng ban từ currentData
        List<String> phongBans = new ArrayList<>();
        if (currentData != null) {
            currentData.stream()
                    .map(d -> d.phongBan)
                    .filter(p -> p != null && !p.isBlank())
                    .distinct()
                    .sorted()
                    .forEach(phongBans::add);
        }

        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        new AttenDanceFilterDialog(parent, phongBans, activeFilter, fc -> {
            activeFilter = fc;
            filterChip.setVisible(!fc.isEmpty());
            applyFilters();
        }).setVisible(true);
    }

    // ─────────────────────────────────────────────────────────────
    // EXPORT EXCEL
    // ─────────────────────────────────────────────────────────────
    private void doExportExcel() {
        // Xuất dữ liệu ĐANG HIỂN THỊ (đã qua filter), không phải toàn bộ
        List<EmployeeRowDTO> toExport = getFilteredData();
        AttendanceExcelExporter.export(this, toExport, currentMonth, currentYear);
    }

    // ─────────────────────────────────────────────────────────────
    // TABLE
    // ─────────────────────────────────────────────────────────────
    private JScrollPane buildTablePanel() {
        String[] cols = {"NHÂN VIÊN","PHÒNG BAN","NGÀY CÔNG","ĐI MUỘN","NGHỈ PHÉP","KHÔNG PHÉP","HÀNH ĐỘNG"};

        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(72);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0,0));
        table.setBackground(Color.WHITE);
        table.setFocusable(false);

        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.WHITE);
        header.setForeground(GRAY500);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 12f));
        header.setBorder(new MatteBorder(0,0,1,0,BORDER));
        header.setReorderingAllowed(false);

        int[] widths = {200,110,90,80,90,100,90};
        for (int i=0; i<widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        table.getColumnModel().getColumn(0).setCellRenderer(new EmployeeCellRenderer());
        table.getColumnModel().getColumn(3).setCellRenderer(new ColoredNumberRenderer(ORANGE));
        table.getColumnModel().getColumn(4).setCellRenderer(new ColoredNumberRenderer(BLUE));
        table.getColumnModel().getColumn(5).setCellRenderer(new ColoredNumberRenderer(RED));
        table.getColumnModel().getColumn(6).setCellRenderer(new ActionCellRenderer());

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t,v,sel,focus,row,col);
                setBackground(Color.WHITE); setForeground(GRAY900);
                setBorder(new MatteBorder(0,0,1,0,ROW_SEP));
                setHorizontalAlignment(CENTER);
                return this;
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                // Chỉ cho phép click detail nếu có quyền QUYEN_SUA
                if (!canEditAttendance) {
                    JOptionPane.showMessageDialog(AttenDanceTable.this,
                        "Bạn không có quyền chỉnh sửa check-in/check-out.\nVui lòng liên hệ quản trị viên để cấp quyền.",
                        "Quyền hạn không đủ", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col == 6 && row >= 0 && onDetailClick != null && currentData != null) {
                    String[] nameId = (String[]) model.getValueAt(row, 0);
                    String manv = nameId[1];
                    for (EmployeeRowDTO dto : currentData) {
                        if (dto.manv.equals(manv)) {
                            onDetailClick.accept(dto.toObjectArray());
                            break;
                        }
                    }
                }
            }
        });

        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                table.setCursor(table.columnAtPoint(e.getPoint()) == 6
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
    // FILTER LOGIC
    // ─────────────────────────────────────────────────────────────

    /** Áp dụng cả text search và FilterCriteria, rebuild model */
    private void applyFilters() {
        List<EmployeeRowDTO> filtered = getFilteredData();
        rebuildModel(filtered);
    }

    /** Trả về danh sách sau khi áp dụng tất cả filter */
    private List<EmployeeRowDTO> getFilteredData() {
        if (currentData == null) return new ArrayList<>();

        String q = searchField.getText().toLowerCase().trim();
        boolean isPlaceholder = q.contains("tìm theo");
        if (isPlaceholder) q = "";

        final String query = q;

        return currentData.stream()
                // ── Text search ──────────────────────────────────
                .filter(dto -> query.isEmpty()
                        || dto.hoTen.toLowerCase().contains(query)
                        || dto.manv.toLowerCase().contains(query)
                        || (dto.phongBan != null && dto.phongBan.toLowerCase().contains(query))
                        || (dto.chucVu   != null && dto.chucVu.toLowerCase().contains(query)))

                // ── Phòng ban ─────────────────────────────────────
                .filter(dto -> "Tất cả".equals(activeFilter.phongBan)
                        || activeFilter.phongBan.equals(dto.phongBan))

                // ── Tình trạng ────────────────────────────────────
                .filter(dto -> {
                    switch (activeFilter.tinhTrang) {
                        case "Có đi muộn":
                            return dto.lateDays > 0;
                        case "Có vắng mặt":
                            return dto.absentDays > 0;
                        case "Đầy đủ (không muộn, không vắng)":
                            return dto.lateDays == 0 && dto.absentDays == 0;
                        default:
                            return true;
                    }
                })

                // ── Ngày công ─────────────────────────────────────
                .filter(dto -> dto.workDays >= activeFilter.minWorkDays
                            && dto.workDays <= activeFilter.maxWorkDays)

                .collect(Collectors.toList());
    }

    private void rebuildModel(List<EmployeeRowDTO> data) {
        model.setRowCount(0);
        if (data == null) return;
        for (EmployeeRowDTO dto : data) {
            model.addRow(new Object[]{
                new String[]{dto.hoTen, dto.manv},
                dto.phongBan,
                dto.workDays,
                dto.lateDays,
                dto.leaveDays,
                dto.absentDays,
                "Chi tiết"
            });
        }
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
        btn.setBorder(new CompoundBorder(new LineBorder(border, 1, true),
                BorderFactory.createEmptyBorder(5,14,5,14)));
        btn.setPreferredSize(new Dimension(130, 36));
        return btn;
    }

    // ─────────────────────────────────────────────────────────────
    // CELL RENDERERS
    // ─────────────────────────────────────────────────────────────

    static class EmployeeCellRenderer implements TableCellRenderer {
        private static final Color[] COLORS = {
            new Color( 99,102,241), new Color( 20,184,166),
            new Color(249,115, 22), new Color(239, 68, 68),
            new Color( 16,185,129), new Color(139, 92,246),
            new Color(236, 72,153)
        };
        @Override public Component getTableCellRendererComponent(JTable t, Object value,
                boolean sel, boolean focus, int row, int col) {
            String[] arr = (String[]) value;
            String name  = arr[0], id = arr[1];
            String[] parts = name.trim().split("\\s+");
            String initials = parts.length >= 2
                    ? "" + parts[0].charAt(0) + parts[parts.length-1].charAt(0)
                    : name.substring(0, Math.min(2, name.length()));
            Color avatarColor = COLORS[row % COLORS.length];

            JPanel cell = new JPanel(new BorderLayout(12,0));
            cell.setBackground(Color.WHITE);
            cell.setBorder(new CompoundBorder(
                    new MatteBorder(0,0,1,0,new Color(243,244,246)),
                    BorderFactory.createEmptyBorder(8,16,8,0)));

            JLabel avatar = new JLabel(initials.toUpperCase(), SwingConstants.CENTER) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2=(Graphics2D)g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
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
            text.add(nameLabel); text.add(Box.createVerticalStrut(2)); text.add(idLabel);

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
            super.getTableCellRendererComponent(t,v,sel,focus,row,col);
            setForeground(color); setFont(getFont().deriveFont(Font.BOLD, 14f));
            setBackground(Color.WHITE);
            setBorder(new MatteBorder(0,0,1,0,new Color(243,244,246)));
            return this;
        }
    }

    static class ActionCellRenderer extends DefaultTableCellRenderer {
        ActionCellRenderer() { setHorizontalAlignment(CENTER); }
        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean focus, int row, int col) {
            super.getTableCellRendererComponent(t,v,sel,focus,row,col);
            setText("<html><u>Chi tiết</u></html>");
            setForeground(new Color(124,58,237));
            setFont(getFont().deriveFont(Font.BOLD, 13f));
            setBackground(Color.WHITE);
            setBorder(new MatteBorder(0,0,1,0,new Color(243,244,246)));
            return this;
        }
    }
}