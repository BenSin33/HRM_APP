package com.hrm.UI.HR.Evaluationtab;

import com.hrm.DAO.HR.EvaluationDAO;
import com.hrm.DAO.ContractDAO;
import com.hrm.DAO.PhieuDanhGiaDAO;
import com.hrm.DTO.HR.EvaluationDTO;
import com.hrm.DTO.HR.EvaluationPeriodDTO;
import com.hrm.DTO.UserDTO;
import com.hrm.Service.PermissionService;
import com.hrm.utils.SessionManager;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EvaluationTable extends JPanel {

    // ── Màu ──────────────────────────────────────────────────────
    private static final Color PURPLE   = new Color(124,  58, 237);
    private static final Color GRAY900  = new Color( 17,  24,  39);
    private static final Color GRAY500  = new Color(107, 114, 128);
    private static final Color GRAY200  = new Color(229, 231, 235);
    private static final Color ROW_SEP  = new Color(243, 244, 246);

    private static final Color XS_BG  = new Color(220, 252, 231); private static final Color XS_FG  = new Color( 21, 128,  61);
    private static final Color TOT_BG = new Color(219, 234, 254); private static final Color TOT_FG = new Color( 29,  78, 216);
    private static final Color TB_BG  = new Color(254, 243, 199); private static final Color TB_FG  = new Color(161,  98,   7);
    private static final Color KEM_BG = new Color(254, 226, 226); private static final Color KEM_FG = new Color(185,  28,  28);

    private static final Color PEND_BG = new Color(254, 243, 199); private static final Color PEND_FG = new Color(161, 98,  7);
    private static final Color DONE_BG = new Color(220, 252, 231); private static final Color DONE_FG = new Color( 21, 128, 61);

    private static final Color REWARD_FG  = new Color( 21, 128, 61);
    private static final Color PENALTY_FG = new Color(185,  28, 28);
    private static final Color NONE_FG    = new Color(156, 163, 175);

    private DefaultTableModel model;
    private JTable table;
    private JComboBox<String> periodBox;
    private List<EvaluationPeriodDTO> periods;
    private final EvaluationDAO dao = new EvaluationDAO();
    private final PermissionService permissionService = new PermissionService();
    private EvaluationSummary summary;

    public EvaluationTable() { this(null); }

    public EvaluationTable(EvaluationSummary summary) {
        this.summary = summary;
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);

        periods = dao.getAllPeriods();

        // ── Toolbar: ComboBox kỳ + nút Thêm phiếu ────────────────
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(Color.WHITE);
        toolbar.putClientProperty("FlatLaf.style",
                "arc:12; background:#FFFFFF; borderColor:#E5E7EB; borderWidth:1");
        toolbar.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        // Trái: ComboBox kỳ
        JPanel leftBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftBar.setOpaque(false);

        JLabel periodLabel = new JLabel("Kỳ đánh giá:");
        periodLabel.setForeground(GRAY900);
        periodLabel.setFont(periodLabel.getFont().deriveFont(Font.PLAIN, 13f));

        String[] labels = periods.stream()
                .map(EvaluationPeriodDTO::getLabel)
                .toArray(String[]::new);
        if (labels.length == 0) labels = new String[]{"Chưa có đợt đánh giá"};

        periodBox = new JComboBox<>(labels);
        periodBox.putClientProperty("FlatLaf.style", "arc:8; background:#FFFFFF; borderColor:#E5E7EB");
        periodBox.setPreferredSize(new Dimension(160, 32));
        periodBox.addActionListener(e -> loadTableData());

        leftBar.add(periodLabel);
        leftBar.add(periodBox);

        // Phải: Nút Thêm phiếu đánh giá (ẨNĐI - click hàng để duyệt)
        JButton addBtn = new JButton("＋  Thêm phiếu đánh giá");
        addBtn.setFont(addBtn.getFont().deriveFont(Font.BOLD, 12f));
        addBtn.setForeground(Color.WHITE);
        addBtn.setBackground(PURPLE);
        addBtn.setFocusPainted(false);
        addBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addBtn.putClientProperty("FlatLaf.style", "arc:8; background:#7C3AED; borderWidth:0");
        addBtn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        addBtn.setVisible(false);  // ← ẨNĐI - SỬ DỤNG CLICK HÀN THAY VÌ BUTTON
        addBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                addBtn.putClientProperty("FlatLaf.style", "arc:8; background:#6D28D9; borderWidth:0");
                addBtn.repaint();
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                addBtn.putClientProperty("FlatLaf.style", "arc:8; background:#7C3AED; borderWidth:0");
                addBtn.repaint();
            }
        });
        addBtn.addActionListener(e -> showAddEvaluationDialog());

        toolbar.add(leftBar,  BorderLayout.WEST);
        toolbar.add(addBtn,   BorderLayout.EAST);

        // ── Bảng ─────────────────────────────────────────────────
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.putClientProperty("FlatLaf.style",
                "arc:16; background:#FFFFFF; borderColor:#E5E7EB; borderWidth:1; shadow:sm");
        card.add(buildTablePanel(), BorderLayout.CENTER);

        JPanel wrapper = new JPanel(new BorderLayout(0, 12));
        wrapper.setOpaque(false);
        wrapper.add(toolbar, BorderLayout.NORTH);
        wrapper.add(card,    BorderLayout.CENTER);

        add(wrapper, BorderLayout.CENTER);
        loadTableData();
    }

    // ─────────────────────────────────────────────────────────────
    // RELOAD KHI TẠO ĐỢT MỚI
    // ─────────────────────────────────────────────────────────────
    public void reloadPeriods() {
        periods = dao.getAllPeriods();
        periodBox.removeAllItems();
        for (EvaluationPeriodDTO p : periods) periodBox.addItem(p.getLabel());
        if (periodBox.getItemCount() > 0) periodBox.setSelectedIndex(0);
        loadTableData();
    }

    // ─────────────────────────────────────────────────────────────
    // LOAD DATA
    // ─────────────────────────────────────────────────────────────
    private void loadTableData() {
        model.setRowCount(0);
        int idx = periodBox.getSelectedIndex();
        if (periods == null || periods.isEmpty() || idx < 0 || idx >= periods.size()) return;

        String maDot = periods.get(idx).getMaDot();
        List<EvaluationDTO> list = dao.getEvaluationsByPeriod(maDot);
        for (EvaluationDTO dto : list) model.addRow(dto.toTableRow());
        if (summary != null) summary.refreshStats(maDot);
    }

    private String getCurrentMaDot() {
        int idx = periodBox.getSelectedIndex();
        if (periods == null || periods.isEmpty() || idx < 0 || idx >= periods.size()) return "";
        return periods.get(idx).getMaDot();
    }

    private String getCurrentNguoiDanhGia() {
        int idx = periodBox.getSelectedIndex();
        if (periods == null || periods.isEmpty() || idx < 0 || idx >= periods.size()) return "";
        return periods.get(idx).getNguoiDanhGia() != null ? periods.get(idx).getNguoiDanhGia() : "";
    }

    // ─────────────────────────────────────────────────────────────
    // DIALOG THÊM PHIẾU ĐÁNH GIÁ - ĐẦY ĐỦ THUỘC TÍNH
    // ─────────────────────────────────────────────────────────────
    private void showAddEvaluationDialog() {
        String maDot = getCurrentMaDot();
        if (maDot.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn kỳ đánh giá trước!", "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Thêm phiếu đánh giá",
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(500, 620);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setLayout(new BorderLayout());

        // ── Form ─────────────────────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 8, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.insets = new Insets(4, 0, 4, 0);

        // ── Helper: thêm label + component ───────────────────────
        // 1. Nhân viên
        gbc.gridy = 0;
        form.add(makeLabel("Nhân viên: *"), gbc);
        gbc.gridy = 1;
        List<String[]> nhanViens = dao.getAllNhanVien();
        String[] nvItems = nhanViens.stream()
                .map(nv -> nv[0] + " - " + nv[1])
                .toArray(String[]::new);
        JComboBox<String> nvBox = makeCombo(nvItems);
        // Hiển thị thêm chức vụ / phòng ban khi chọn
        JLabel nvInfoLbl = new JLabel(" ");
        nvInfoLbl.setForeground(GRAY500);
        nvInfoLbl.setFont(nvInfoLbl.getFont().deriveFont(Font.PLAIN, 11f));
        nvBox.addActionListener(e -> {
            int i = nvBox.getSelectedIndex();
            if (i >= 0 && i < nhanViens.size()) {
                String[] nv = nhanViens.get(i);
                nvInfoLbl.setText(nv[2] + "  •  " + nv[3]);
            }
        });
        if (!nhanViens.isEmpty()) {
            String[] first = nhanViens.get(0);
            nvInfoLbl.setText(first[2] + "  •  " + first[3]);
        }
        form.add(nvBox, gbc);
        gbc.gridy = 2;
        form.add(nvInfoLbl, gbc);

        // 2. Tiêu chí đánh giá
        gbc.gridy = 3;
        form.add(makeLabel("Tiêu chí đánh giá: *"), gbc);
        gbc.gridy = 4;
        List<String[]> tieuChis = dao.getAllTieuChi();
        String[] tcItems = tieuChis.stream()
                .map(tc -> tc[0] + " - " + tc[1] + " (" + tc[2] + " điểm)")
                .toArray(String[]::new);
        JComboBox<String> tcBox = makeCombo(tcItems);
        form.add(tcBox, gbc);

        // 3. Tổng điểm
        gbc.gridy = 5;
        form.add(makeLabel("Tổng điểm (0–100): *"), gbc);
        gbc.gridy = 6;
        JSpinner diemSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        diemSpinner.setPreferredSize(new Dimension(440, 34));
        diemSpinner.putClientProperty("FlatLaf.style", "arc:8; borderColor:#E5E7EB");
        // Tự tính xếp loại khi đổi điểm
        JLabel xepLoaiPreview = new JLabel("Xếp loại: Kém");
        xepLoaiPreview.setForeground(KEM_FG);
        xepLoaiPreview.setFont(xepLoaiPreview.getFont().deriveFont(Font.BOLD, 12f));
        diemSpinner.addChangeListener(e -> {
            int d = (Integer) diemSpinner.getValue();
            String xl = EvaluationDAO.tinhXepLoai(d);
            xepLoaiPreview.setText("Xếp loại: " + xl);
            switch (xl) {
                case "Xuất sắc":   xepLoaiPreview.setForeground(XS_FG);  break;
                case "Tốt":        xepLoaiPreview.setForeground(TOT_FG); break;
                case "Trung bình": xepLoaiPreview.setForeground(TB_FG);  break;
                default:           xepLoaiPreview.setForeground(KEM_FG); break;
            }
        });
        form.add(diemSpinner, gbc);
        gbc.gridy = 7;
        form.add(xepLoaiPreview, gbc);

        // 4. Nhận xét
        gbc.gridy = 8;
        form.add(makeLabel("Nhận xét:"), gbc);
        gbc.gridy = 9;
        JTextArea nhanXetArea = new JTextArea(3, 20);
        nhanXetArea.setLineWrap(true);
        nhanXetArea.setWrapStyleWord(true);
        nhanXetArea.putClientProperty("FlatLaf.style", "arc:8; borderColor:#E5E7EB");
        JScrollPane nhanXetScroll = new JScrollPane(nhanXetArea);
        nhanXetScroll.setPreferredSize(new Dimension(440, 70));
        nhanXetScroll.putClientProperty("FlatLaf.style", "arc:8; borderColor:#E5E7EB");
        form.add(nhanXetScroll, gbc);

        // 5. Quyết định (QUYETDINH - text ngắn như "Khen thưởng", "Giữ nguyên"...)
        gbc.gridy = 10;
        form.add(makeLabel("Quyết định:"), gbc);
        gbc.gridy = 11;
        JComboBox<String> quyetDinhBox = makeCombo(new String[]{
            "Giữ nguyên", "Khen thưởng", "Tăng lương 10%", "Tăng lương 15%",
            "Thưởng quý", "Trừ lương tháng", "Cảnh cáo", "Không có"
        });
        form.add(quyetDinhBox, gbc);

        // 6. Loại quyết định (LOAIQUYETDINH)
        gbc.gridy = 12;
        form.add(makeLabel("Loại quyết định:"), gbc);
        gbc.gridy = 13;
        JComboBox<String> loaiQDBox = makeCombo(new String[]{
            "Không có", "Thưởng", "Tăng lương", "Trừ lương", "Kỷ luật"
        });
        form.add(loaiQDBox, gbc);

        // 7. Trạng thái duyệt (TRANGTHAI_DUYET)
        gbc.gridy = 14;
        form.add(makeLabel("Trạng thái duyệt:"), gbc);
        gbc.gridy = 15;
        JComboBox<String> trangThaiBox = makeCombo(new String[]{"Chờ duyệt", "Đã duyệt"});
        form.add(trangThaiBox, gbc);

        // 8. Ngày đánh giá (NGAYDANHGIA)
        gbc.gridy = 16;
        form.add(makeLabel("Ngày đánh giá:"), gbc);
        gbc.gridy = 17;
        JTextField ngayField = new JTextField(
                new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        ngayField.setPreferredSize(new Dimension(440, 34));
        ngayField.putClientProperty("FlatLaf.style", "arc:8; borderColor:#E5E7EB");
        JLabel ngayHintLbl = new JLabel("Định dạng: yyyy-MM-dd");
        ngayHintLbl.setForeground(GRAY500);
        ngayHintLbl.setFont(ngayHintLbl.getFont().deriveFont(Font.PLAIN, 11f));
        form.add(ngayField, gbc);
        gbc.gridy = 18;
        form.add(ngayHintLbl, gbc);

        JScrollPane formScroll = new JScrollPane(form);
        formScroll.setBorder(BorderFactory.createEmptyBorder());
        formScroll.getViewport().setBackground(Color.WHITE);

        // ── Buttons ───────────────────────────────────────────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, GRAY200));

        JButton cancelBtn = new JButton("Hủy");
        cancelBtn.setPreferredSize(new Dimension(90, 36));
        cancelBtn.setFocusPainted(false);
        cancelBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelBtn.putClientProperty("FlatLaf.style", "arc:8; background:#F3F4F6; borderColor:#E5E7EB");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton saveBtn = new JButton("Lưu phiếu");
        saveBtn.setPreferredSize(new Dimension(110, 36));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setBackground(PURPLE);
        saveBtn.setFocusPainted(false);
        saveBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        saveBtn.putClientProperty("FlatLaf.style", "arc:8; background:#7C3AED; borderWidth:0");

        saveBtn.addActionListener(e -> {
            // ── Lấy dữ liệu ──────────────────────────────────────
            int nvIdx = nvBox.getSelectedIndex();
            int tcIdx = tcBox.getSelectedIndex();
            if (nvIdx < 0 || nvIdx >= nhanViens.size()) {
                showError(dialog, "Vui lòng chọn nhân viên!");
                return;
            }
            if (tcIdx < 0 || tcIdx >= tieuChis.size()) {
                showError(dialog, "Vui lòng chọn tiêu chí đánh giá!");
                return;
            }

            String maNV     = nhanViens.get(nvIdx)[0];
            String maTieuChi = tieuChis.get(tcIdx)[0];
            int    tongDiem = (Integer) diemSpinner.getValue();
            String nhanXet  = nhanXetArea.getText().trim();
            String quyetDinh = (String) quyetDinhBox.getSelectedItem();
            String loaiQD    = (String) loaiQDBox.getSelectedItem();
            String trangThai = (String) trangThaiBox.getSelectedItem();
            String ngayStr   = ngayField.getText().trim();

            // Validate ngày
            java.sql.Date ngayDG = null;
            if (!ngayStr.isEmpty()) {
                try {
                    java.util.Date parsed = new SimpleDateFormat("yyyy-MM-dd").parse(ngayStr);
                    ngayDG = new java.sql.Date(parsed.getTime());
                } catch (Exception ex) {
                    showError(dialog, "Ngày đánh giá không hợp lệ! Định dạng: yyyy-MM-dd");
                    ngayField.requestFocus();
                    return;
                }
            }

            // Kiểm tra trùng phiếu
            if (dao.existsEvaluation(maNV, maDot)) {
                showError(dialog,
                    "Nhân viên này đã có phiếu đánh giá trong kỳ " + maDot + "!");
                return;
            }

            // ── Build DTO ─────────────────────────────────────────
            EvaluationDTO dto = new EvaluationDTO();
            dto.setMaPhieu(dao.generateMaPhieu());
            dto.setMaNV(maNV);
            dto.setMaDot(maDot);
            dto.setMaTieuChi(maTieuChi);
            dto.setTongDiem(tongDiem);
            dto.setXepLoai(EvaluationDAO.tinhXepLoai(tongDiem));
            dto.setNhanXet(nhanXet);
            dto.setQuyetDinh(quyetDinh);
            dto.setLoaiQuyetDinh(loaiQD);
            dto.setTrangThaiDuyet(trangThai);
            dto.setNgayDanhGia(ngayDG);

            boolean ok = dao.insertEvaluation(dto);
            if (ok) {
                JOptionPane.showMessageDialog(dialog,
                        "Thêm phiếu đánh giá thành công!", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadTableData(); // Refresh bảng
            } else {
                showError(dialog, "Lưu thất bại! Vui lòng thử lại.");
            }
        });

        dialog.getRootPane().setDefaultButton(saveBtn);
        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);

        dialog.add(formScroll, BorderLayout.CENTER);
        dialog.add(btnPanel,   BorderLayout.SOUTH);
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setVisible(true);
    }

    // ─────────────────────────────────────────────────────────────
    // UI HELPERS
    // ─────────────────────────────────────────────────────────────
    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN, 13f));
        lbl.setForeground(GRAY900);
        return lbl;
    }

    private JComboBox<String> makeCombo(String[] items) {
        JComboBox<String> box = new JComboBox<>(items);
        box.setPreferredSize(new Dimension(440, 34));
        box.putClientProperty("FlatLaf.style", "arc:8; borderColor:#E5E7EB");
        return box;
    }

    private void showError(JDialog parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    // ─────────────────────────────────────────────────────────────
    // TABLE
    // ─────────────────────────────────────────────────────────────
    private JScrollPane buildTablePanel() {
        String[] cols = {"MÃ NV", "NHÂN VIÊN", "NGƯỜI ĐÁNH GIÁ", "ĐIỂM SỐ", "XẾP LOẠI", "THƯỞNG/PHẠT", "TRẠNG THÁI", "THAO TÁC"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(80);
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

        int[] widths = {80, 180, 130, 110, 110, 160, 120, 100};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        table.getColumnModel().getColumn(0).setCellRenderer(new IdRenderer());
        table.getColumnModel().getColumn(1).setCellRenderer(new EmployeeRenderer());
        table.getColumnModel().getColumn(3).setCellRenderer(new ScoreRenderer());
        table.getColumnModel().getColumn(4).setCellRenderer(new RankBadgeRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new RewardRenderer());
        table.getColumnModel().getColumn(6).setCellRenderer(new StatusBadgeRenderer());
        table.getColumnModel().getColumn(7).setCellRenderer(new ActionRenderer());

        DefaultTableCellRenderer defRend = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, focus, row, col);
                setBackground(sel ? new Color(245, 243, 255) : Color.WHITE);
                setForeground(GRAY900);
                setFont(getFont().deriveFont(Font.PLAIN, 13f));
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
        scroll.getVerticalScrollBar().setUnitIncrement(30);  // Tăng scroll speed
        
        // === CLICK LISTENER - CLICK ICON APPROVE/REJECT ===
        List<EvaluationDTO> currentData = new java.util.ArrayList<>();
        ContractDAO contractDAO = new ContractDAO();
        PhieuDanhGiaDAO phieuDAO = new PhieuDanhGiaDAO();
        
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col != 7 || row < 0 || row >= table.getRowCount()) return;

                String maDot = getCurrentMaDot();
                List<EvaluationDTO> list = dao.getEvaluationsByPeriod(maDot);
                if (list == null || list.isEmpty()) return;

                String maNV = (String) model.getValueAt(row, 0);
                EvaluationDTO dto = list.stream()
                        .filter(d -> d.getMaNV().equals(maNV))
                        .findFirst()
                        .orElse(null);
                if (dto == null) return;

                String status = dto.getTrangThaiDuyet();
                if (!"Chờ duyệt".equals(status)) return;

                // Xác định click ✓ hay ✕ theo vị trí X trong cell
                Rectangle cellRect = table.getCellRect(row, col, false);
                boolean isApprove = (e.getX() - cellRect.x) < cellRect.width / 2;

                String action = isApprove ? "Đồng ý" : "Không";
                int confirm = JOptionPane.showConfirmDialog(
                        EvaluationTable.this,
                        action + " duyệt phiếu đánh giá của " + maNV + "?",
                        "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;

                String newStatus = isApprove ? "Đã duyệt" : "Từ chối";
                boolean ok = dao.updateEvaluationStatus(dto.getMaPhieu(), newStatus);
                if (ok) {
                    // Nếu approve, thử cập nhật lương nếu có thay đổi
                    if (isApprove) {
                        try {
                            String loaiQD = phieuDAO.getLoaiQuyetDinh(maNV, maDot);
                            BigDecimal tiLeThayDoi = phieuDAO.getTiLeThayDoi(maNV, maDot);
                            
                            // Nếu có loại quyết định là "Tăng lương" hoặc "Trừ lương", update lương
                            if (("Tăng lương".equals(loaiQD) || "Trừ lương".equals(loaiQD)) && tiLeThayDoi != null) {
                                boolean salaryOk = contractDAO.updateBaseSalaryByEvaluation(maNV, tiLeThayDoi, loaiQD);
                                if (salaryOk) {
                                    JOptionPane.showMessageDialog(EvaluationTable.this,
                                            "✓ Duyệt phiếu và cập nhật lương thành công!",
                                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(EvaluationTable.this,
                                            "⚠ Duyệt phiếu thành công, nhưng cập nhật lương thất bại.",
                                            "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                                }
                            } else {
                                JOptionPane.showMessageDialog(EvaluationTable.this,
                                        "✓ Duyệt phiếu thành công!",
                                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(EvaluationTable.this,
                                    "✓ Duyệt phiếu thành công, nhưng có lỗi khi cập nhật lương.",
                                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                    loadTableData();
                    if (summary != null) summary.refreshStats(maDot);
                } else {
                    JOptionPane.showMessageDialog(EvaluationTable.this,
                            "Cập nhật thất bại, vui lòng thử lại.",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        table.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                table.setCursor(col == 7
                        ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                        : Cursor.getDefaultCursor());
            }
        });
        
        return scroll;
    }

    // ─────────────────────────────────────────────────────────────
    // CELL RENDERERS
    // ─────────────────────────────────────────────────────────────

    class IdRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean focus, int row, int col) {
            super.getTableCellRendererComponent(t, v, sel, focus, row, col);
            setText("<html><b>" + (v != null ? v : "") + "</b></html>");
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
            String[] arr = (v instanceof String[]) ? (String[]) v : new String[]{"", "", ""};
            JPanel cell = new JPanel();
            cell.setOpaque(true);
            cell.setBackground(sel ? new Color(245, 243, 255) : Color.WHITE);
            cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));
            cell.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, ROW_SEP),
                BorderFactory.createEmptyBorder(12, 8, 12, 8)));

            JLabel name = new JLabel(arr.length > 0 ? arr[0] : "");
            name.setFont(name.getFont().deriveFont(Font.BOLD, 13f));
            name.setForeground(GRAY900);

            JLabel pos = new JLabel(arr.length > 1 ? arr[1] : "");
            pos.setFont(pos.getFont().deriveFont(Font.PLAIN, 12f));
            pos.setForeground(GRAY500);

            JLabel team = new JLabel(arr.length > 2 ? arr[2] : "");
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
            Object[] arr = (v instanceof Object[]) ? (Object[]) v : new Object[]{0, ""};
            int score = (arr.length > 0 && arr[0] instanceof Integer) ? (Integer) arr[0] : 0;
            String xepLoai = (arr.length > 1 && arr[1] != null) ? arr[1].toString() : "";
            Color scoreColor = "Kém".equals(xepLoai) ? new Color(239, 68, 68) : PURPLE;

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

            inner.add(big); inner.add(small);
            cell.add(inner);
            return cell;
        }
    }

    class RankBadgeRenderer implements TableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean focus, int row, int col) {
            String rank = (v != null) ? v.toString() : "";
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
            String[] arr = (v instanceof String[]) ? (String[]) v : new String[]{"Không có", ""};
            String reward = (arr.length > 0 && arr[0] != null) ? arr[0] : "Không có";

            JPanel cell = new JPanel(new GridBagLayout());
            cell.setBackground(sel ? new Color(245, 243, 255) : Color.WHITE);
            cell.setBorder(new MatteBorder(0, 0, 1, 0, ROW_SEP));

            if ("Không có".equals(reward) || reward.isEmpty()) {
                JLabel lbl = new JLabel("Không có");
                lbl.setForeground(NONE_FG);
                lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN, 13f));
                cell.add(lbl);
                return cell;
            }

            boolean isReward = reward.equals("Tăng lương") || reward.equals("Thưởng");
            Color c = isReward ? REWARD_FG : PENALTY_FG;

            JPanel inner = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
            inner.setOpaque(false);

            JLabel icon = new JLabel(isReward ? makeSmallRibbon(c) : makeSmallWarn(c));
            icon.setVerticalAlignment(SwingConstants.CENTER);

            JLabel text = new JLabel(reward);
            text.setFont(text.getFont().deriveFont(Font.BOLD, 13f));
            text.setForeground(c);
            text.setVerticalAlignment(SwingConstants.CENTER);

            inner.add(icon); inner.add(text);
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
                    g2.drawOval(x+2,  y+1,  12, 10);
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
            String status = (v != null) ? v.toString() : "-";
            JPanel cell = new JPanel(new GridBagLayout());
            cell.setBackground(sel ? new Color(245, 243, 255) : Color.WHITE);
            cell.setBorder(new MatteBorder(0, 0, 1, 0, ROW_SEP));

            if ("-".equals(status) || status.isEmpty()) {
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

    /**
     * ActionRenderer – icon ✓ và ✕ căn giữa hoàn toàn.
     * Hiển thị buttons khi status là "Chờ duyệt"
     */
    class ActionRenderer implements TableCellRenderer {
        private final Color GREEN_IC = new Color( 22, 163,  74);
        private final Color RED_IC   = new Color(220,  38,  38);

        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean focus, int row, int col) {
            String status = v == null ? "" : v.toString();

            // Outer cell: GridBagLayout = căn giữa DỌC tự động
            JPanel cell = new JPanel(new GridBagLayout());
            cell.setBackground(sel ? new Color(245, 243, 255) : Color.WHITE);
            cell.setBorder(new MatteBorder(0, 0, 1, 0, ROW_SEP));

            if ("Chờ duyệt".equals(status)) {
                // Inner row: FlowLayout CENTER = căn giữa NGANG
                JPanel iconRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
                iconRow.setOpaque(false);
                iconRow.add(makeIconBtn(GREEN_IC, true));   // ✓
                iconRow.add(makeIconBtn(RED_IC,   false));  // ✕
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
                        // Dấu ✓
                        g2.drawPolyline(
                            new int[]{w/2-5, w/2,   w/2+6},
                            new int[]{h/2,   h/2+5, h/2-4}, 3);
                    } else {
                        // Dấu ✕
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
