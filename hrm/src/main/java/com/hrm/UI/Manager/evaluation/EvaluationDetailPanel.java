package com.hrm.UI.Manager.evaluation;

import com.hrm.DTO.Manager.TieuChiDanhGiaDTO;
import com.hrm.DAO.PhieuDanhGiaDAO;
import com.hrm.UI.Manager.evaluation.component.ScoreRadioPanel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EvaluationDetailPanel extends JPanel {
    private JLabel titleLabel;
    private JLabel statusLabel;
    private JPanel criteriaPanel;
    private JTextArea txtNhanXet;
    private JButton btnBack;
    private JButton btnSave;
    private JButton btnReset;
    
    private JComboBox<String> comboQuyetDinh;
    private JLabel lblLoaiQDValue;      // Tự động: Giữ nguyên / Tăng lương / Trừ lương
    private JTextField txtAnhHuongLuong;
    private boolean isUpdatingCombos = false;

    /** Quyết định → Loại quyết định: Thưởng→Tăng lương, Kỷ luật→Trừ lương, Không có→Giữ nguyên */
    private static final String[] QUYET_DINH_OPTIONS = new String[] { "Không có", "Thưởng", "Kỷ luật" };
    
    private JLabel lblTongDiem;
    private List<ScoreRadioPanel> scorePanels;
    private Runnable onBackListener;
    private SaveListener onSaveListener;
    private PhieuDanhGiaDAO phieuDAO;  // THÊM DAO
    
    private String currentMaNV;
    private String currentMaDot;
    private String currentHoTen;
    private List<TieuChiDanhGiaDTO> currentCriteria;

    public interface SaveListener {
        /** @param tiLeThayDoi % lương: dương=tăng, âm=trừ, 0=giữ nguyên */
        void onSave(Map<String, Integer> scores, String nhanXet, String quyetDinh, String loaiQD, BigDecimal tiLeThayDoi);
    }

    public EvaluationDetailPanel(Runnable backListener, SaveListener saveListener) {
        this.onBackListener = backListener;
        this.onSaveListener = saveListener;
        this.scorePanels = new ArrayList<>();
        this.phieuDAO = new PhieuDanhGiaDAO();  // KHỞI TẠO DAO
        
        setLayout(new BorderLayout(0, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        initComponents();
    }

    private void initComponents() {
        // === HEADER ===
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        btnBack = new JButton("← Quay lại");
        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnBack.setForeground(new Color(100, 100, 100));
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> {
            if (onBackListener != null) onBackListener.run();
        });
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        
        titleLabel = new JLabel("Chấm điểm - Nhân viên");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        
        statusLabel = new JLabel("Kỳ đánh giá");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(100, 100, 100));
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(statusLabel);
        
        headerPanel.add(btnBack, BorderLayout.WEST);
        headerPanel.add(titlePanel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);

        // === CRITERIA PANEL (Không scroll riêng) ===
        criteriaPanel = new JPanel();
        criteriaPanel.setLayout(new BoxLayout(criteriaPanel, BoxLayout.Y_AXIS));
        criteriaPanel.setBackground(Color.WHITE);
        criteriaPanel.setOpaque(false);
        
        // Tiêu chí panel với border
        JPanel tieuChiWrapPanel = new JPanel(new BorderLayout());
        tieuChiWrapPanel.setOpaque(false);
        tieuChiWrapPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Tiêu chí đánh giá",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14)
        ));
        // Footer tổng điểm
        JPanel tongDiemPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 8));
        tongDiemPanel.setOpaque(false);
        lblTongDiem = new JLabel("Tổng điểm: 0 / 0");
        lblTongDiem.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTongDiem.setForeground(new Color(99, 102, 241));
        tongDiemPanel.add(lblTongDiem);

        tieuChiWrapPanel.add(criteriaPanel, BorderLayout.CENTER);
        tieuChiWrapPanel.add(tongDiemPanel, BorderLayout.SOUTH);
        
        // === NHẬN XÉT PANEL (Không scroll riêng - vừa vặn) ===
        JPanel nhanXetPanel = new JPanel(new BorderLayout(5, 5));
        nhanXetPanel.setOpaque(false);
        nhanXetPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Nhận xét",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14)
        ));
        
        txtNhanXet = new JTextArea(2, 40);  // 2 dòng vừa đủ
        txtNhanXet.setLineWrap(true);
        txtNhanXet.setWrapStyleWord(true);
        txtNhanXet.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtNhanXet.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        nhanXetPanel.add(txtNhanXet, BorderLayout.CENTER);
        
        // === QUYẾT ĐỊNH PANEL ===
        JPanel quyetDinhPanel = new JPanel(new BorderLayout(0, 10));
        quyetDinhPanel.setOpaque(false);
        quyetDinhPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Quyết định / Thưởng / Phạt",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14)
        ));
        
        JPanel qdContentPanel = new JPanel();
        qdContentPanel.setLayout(new BoxLayout(qdContentPanel, BoxLayout.Y_AXIS));
        qdContentPanel.setOpaque(false);
        qdContentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel grid = new JPanel(new GridLayout(3, 2, 10, 10));
        grid.setOpaque(false);

        // 1. Quyết định (ComboBox: Không có / Thưởng / Kỷ luật)
        JLabel lblQuyetDinh = new JLabel("Quyết định:");
        lblQuyetDinh.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboQuyetDinh = new JComboBox<>(QUYET_DINH_OPTIONS);
        comboQuyetDinh.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboQuyetDinh.addActionListener(e -> {
            if (!isUpdatingCombos) updateLoaiQDAndAnhHuong();
        });

        // 2. Loại quyết định (tự động: Giữ nguyên / Tăng lương / Trừ lương)
        JLabel lblLoaiQD = new JLabel("Loại quyết định:");
        lblLoaiQD.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblLoaiQDValue = new JLabel("Giữ nguyên");
        lblLoaiQDValue.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblLoaiQDValue.setForeground(new Color(60, 60, 60));

        // 3. Ảnh hưởng lương (0-100%), hiển thị cho cả 3 loại
        JLabel lblAnhHuong = new JLabel("Ảnh hưởng lương (%):");
        lblAnhHuong.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtAnhHuongLuong = new JTextField(10);
        txtAnhHuongLuong.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtAnhHuongLuong.setToolTipText("Thưởng: 0-100 (%). Kỷ luật: -100 đến 0 (%)");

        grid.add(lblQuyetDinh);
        grid.add(comboQuyetDinh);
        grid.add(lblLoaiQD);
        grid.add(lblLoaiQDValue);
        grid.add(lblAnhHuong);
        grid.add(txtAnhHuongLuong);

        qdContentPanel.add(grid);

        quyetDinhPanel.add(qdContentPanel, BorderLayout.CENTER);
        
        // === TỔNG HỢP NỘI DUNG CHÍNH - 1 SCROLLPANE CHO TẤT CẢ ===
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.setOpaque(false);
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        mainContentPanel.add(tieuChiWrapPanel);
        mainContentPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        mainContentPanel.add(nhanXetPanel);
        mainContentPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        mainContentPanel.add(quyetDinhPanel);
        mainContentPanel.add(Box.createVerticalGlue());
        
        // 1 ScrollPane duy nhất cho toàn bộ
        JScrollPane mainScroll = new JScrollPane(mainContentPanel);
        mainScroll.getVerticalScrollBar().setUnitIncrement(30);  // Tăng scroll speed từ 20 -> 30
        mainScroll.setBorder(BorderFactory.createEmptyBorder());
        
        add(mainScroll, BorderLayout.CENTER);

        // === BUTTON PANEL ===
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setOpaque(false);
        
        btnReset = new JButton("Reset");
        btnReset.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnReset.setBackground(new Color(255, 193, 7));
        btnReset.setForeground(Color.BLACK);
        btnReset.setFocusPainted(false);
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnSave = new JButton("Lưu điểm");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setBackground(new Color(76, 175, 80));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // SỬA ACTION LISTENER CHO NÚT RESET
        btnReset.addActionListener(e -> resetScores());
        btnSave.addActionListener(e -> saveScores());
        
        buttonPanel.add(btnReset);
        buttonPanel.add(btnSave);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /** Quyết định → Loại quyết định */
    private String getLoaiQDFromQuyetDinh(String qd) {
        if ("Thưởng".equals(qd)) return "Tăng lương";
        if ("Kỷ luật".equals(qd)) return "Trừ lương";
        return "Giữ nguyên";
    }

    /** Cập nhật label Loại quyết định + enable/disable Ảnh hưởng lương + hint */
    private void updateLoaiQDAndAnhHuong() {
        String q = String.valueOf(comboQuyetDinh.getSelectedItem());
        lblLoaiQDValue.setText(getLoaiQDFromQuyetDinh(q));
        boolean enable = "Thưởng".equals(q) || "Kỷ luật".equals(q);
        txtAnhHuongLuong.setEnabled(enable);
        txtAnhHuongLuong.setEditable(enable);
        if ("Thưởng".equals(q)) {
            txtAnhHuongLuong.setToolTipText("Nhập số dương 0-100 (VD: 10 = +10%)");
        } else if ("Kỷ luật".equals(q)) {
            txtAnhHuongLuong.setToolTipText("Nhập số âm 0 đến -100 (VD: -5 = -5%). Không được nhập số dương.");
        } else {
            txtAnhHuongLuong.setText("0");
        }
    }

    public void setData(String maNV, String hoTen, String maDot, 
                       List<TieuChiDanhGiaDTO> criteria, 
                       Map<String, Integer> savedScores,
                       String nhanXet,
                       String quyetDinh,
                       String loaiQD,
                       BigDecimal tiLeThayDoi,
                       boolean locked) {
       
        this.currentMaNV = maNV;
        this.currentMaDot = maDot;
        this.currentHoTen = hoTen;
        this.currentCriteria = criteria;
        
        titleLabel.setText("Chấm điểm - " + hoTen + " (" + maNV + ")");
        statusLabel.setText("Kỳ đánh giá: " + maDot + (locked ? " (Đã lưu)" : ""));
        
        // Xóa các panel cũ
        criteriaPanel.removeAll();
        scorePanels.clear();
        
        // Tạo các panel tiêu chí mới
        for (TieuChiDanhGiaDTO tc : criteria) {
            int diem = (savedScores != null && savedScores.containsKey(tc.getMaTieuChi())) 
                      ? savedScores.get(tc.getMaTieuChi()) 
                      : 0;
            
            ScoreRadioPanel panel = new ScoreRadioPanel(
                tc.getMaTieuChi(),
                tc.getTenTieuChi(),
                diem,
                locked  // Truyền locked để set enable/disable ban đầu
            );
            
            // Khi điểm thay đổi thì tính lại tổng điểm và gợi ý quyết định
            if (!locked) {
                panel.setOnScoreChange(this::updateDecisionByScore);
            }
            
            scorePanels.add(panel);
            criteriaPanel.add(panel);
            criteriaPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        
        txtNhanXet.setText(nhanXet != null ? nhanXet : "");
        txtNhanXet.setEditable(!locked);
        
        // Nếu phiếu đã khóa thì giữ nguyên quyết định đã lưu
        if (locked) {
            String normalizedQD = quyetDinh;
            if ("Trừ lương".equals(normalizedQD)) normalizedQD = "Kỷ luật";
            if (normalizedQD == null || normalizedQD.isEmpty()) normalizedQD = "Không có";
            
            try {
                comboQuyetDinh.setSelectedItem(normalizedQD);
            } catch (Exception e) {
                comboQuyetDinh.setSelectedIndex(0);
            }
            updateLoaiQDAndAnhHuong();

            // Hiển thị giá trị: Thưởng=dương (10), Kỷ luật=âm (-5)
            BigDecimal tl = tiLeThayDoi != null ? tiLeThayDoi.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
            txtAnhHuongLuong.setText(tl.stripTrailingZeros().toPlainString());
            
        } else {
            txtAnhHuongLuong.setText("0");
            updateDecisionByScore();
            updateLoaiQDAndAnhHuong();
        }
        
        comboQuyetDinh.setEnabled(!locked);
        txtAnhHuongLuong.setEnabled(!locked && !"Không có".equals(String.valueOf(comboQuyetDinh.getSelectedItem())));
        txtAnhHuongLuong.setEditable(!locked && !"Không có".equals(String.valueOf(comboQuyetDinh.getSelectedItem())));
        
        // Set trạng thái nút
        if (locked) {
            btnSave.setEnabled(false);
            btnReset.setEnabled(true);   // Đã lưu thì cho reset
        } else {
            btnSave.setEnabled(true);
            btnReset.setEnabled(false);  // Chưa lưu thì không cần reset
        }
        
        // Cập nhật tổng điểm ngay khi load
        updateDecisionByScore();

        criteriaPanel.revalidate();
        criteriaPanel.repaint();
        this.revalidate();
        this.repaint();
    }

    // SỬA LẠI METHOD RESET - XÓA DATABASE VÀ CHO NHẬP LẠI
    private void resetScores() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Reset sẽ xóa phiếu đánh giá đã lưu và cho phép chấm lại. Bạn chắc chắn?",
            "Xác nhận reset",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm != JOptionPane.YES_OPTION) return;
        
        // GỌI DAO ĐỂ XÓA TRONG DATABASE
        boolean success = phieuDAO.resetEvaluation(currentMaNV, currentMaDot);
        
        if (success) {
            JOptionPane.showMessageDialog(this, "Đã reset thành công! Có thể chấm điểm lại.",
                                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            // Gọi lại setData() fresh — giống hệt lúc mở nhân viên chưa có đánh giá
            setData(currentMaNV, currentHoTen, currentMaDot,
                    currentCriteria, null, "", "Không có", "",
                    BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), false);
        } else {
            JOptionPane.showMessageDialog(this, "Reset thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveScores() {
        Map<String, Integer> scores = new HashMap<>();
        for (ScoreRadioPanel panel : scorePanels) {
            scores.put(panel.getMaTieuChi(), panel.getSelectedScore());
        }
        String quyetDinh = String.valueOf(comboQuyetDinh.getSelectedItem());
        String loaiQD = getLoaiQDFromQuyetDinh(quyetDinh);

        BigDecimal tiLe = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        if ("Thưởng".equals(quyetDinh) || "Kỷ luật".equals(quyetDinh)) {
            String raw = txtAnhHuongLuong.getText().trim().replace(',', '.');
            if (raw.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Nhập Ảnh hưởng lương (%). Thưởng: 0-100. Kỷ luật: 0 đến -100.",
                        "Thiếu dữ liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                double val = Double.parseDouble(raw);
                if ("Thưởng".equals(quyetDinh)) {
                    if (val < 0 || val > 100) {
                        JOptionPane.showMessageDialog(this,
                                "Thưởng: Ảnh hưởng lương phải từ 0 đến 100 (số dương).",
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    tiLe = BigDecimal.valueOf(val).setScale(2, RoundingMode.HALF_UP);
                } else {
                    // Kỷ luật: phải là số âm, không được nhập số dương > 0
                    if (val > 0) {
                        JOptionPane.showMessageDialog(this,
                                "Kỷ luật: Phải nhập số âm (VD: -5, -10). Không được nhập số dương.",
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (val < -100) {
                        JOptionPane.showMessageDialog(this,
                                "Kỷ luật: Ảnh hưởng lương từ 0 đến -100.",
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    tiLe = BigDecimal.valueOf(val).setScale(2, RoundingMode.HALF_UP);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Ảnh hưởng lương không hợp lệ. Thưởng: 0-100. Kỷ luật: -100 đến 0.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        if (onSaveListener != null) {
            onSaveListener.onSave(scores, txtNhanXet.getText(), quyetDinh, loaiQD, tiLe);
        }
    }
    
    private void updateDecisionByScore() {
        if (scorePanels.isEmpty()) {
            return;
        }
        
        int totalRaw = 0;
        for (ScoreRadioPanel panel : scorePanels) {
            totalRaw += panel.getSelectedScore();
        }
        
        int maxRaw = scorePanels.size() * 10; // mỗi tiêu chí 0-10
        if (maxRaw == 0) {
            return;
        }

        // Cập nhật nhãn tổng điểm
        if (lblTongDiem != null) {
            int percent = (int) (totalRaw * 100.0 / maxRaw);
            lblTongDiem.setText("Tổng điểm: " + totalRaw + " / " + maxRaw + "  (" + percent + "%)");
            if (percent >= 75) {
                lblTongDiem.setForeground(new Color(22, 163, 74));   // xanh lá
            } else if (percent >= 65) {
                lblTongDiem.setForeground(new Color(99, 102, 241));  // tím
            } else if (percent > 0) {
                lblTongDiem.setForeground(new Color(239, 68, 68));   // đỏ
            } else {
                lblTongDiem.setForeground(new Color(150, 150, 150)); // xám
            }
        }
        
        int percent = (int) (totalRaw * 100.0 / maxRaw); // lấy phần nguyên để khớp mốc
        
        isUpdatingCombos = true;
        try {
            // Khi chưa chấm gì (percent = 0) thì loại quyết định phải là "Không có"
            if (percent == 0) {
                comboQuyetDinh.setModel(new DefaultComboBoxModel<>(QUYET_DINH_OPTIONS));
                comboQuyetDinh.setSelectedItem("Không có");
                txtAnhHuongLuong.setText("0");
                updateLoaiQDAndAnhHuong();
                return;
            }

            if (percent >= 75) {
                comboQuyetDinh.setModel(new DefaultComboBoxModel<>(new String[] { "Thưởng" }));
                comboQuyetDinh.setSelectedItem("Thưởng");
            } else if (percent >= 65) {
                comboQuyetDinh.setModel(new DefaultComboBoxModel<>(new String[] { "Không có" }));
                comboQuyetDinh.setSelectedItem("Không có");
            } else {
                comboQuyetDinh.setModel(new DefaultComboBoxModel<>(new String[] { "Kỷ luật" }));
                comboQuyetDinh.setSelectedItem("Kỷ luật");
            }
            
            txtAnhHuongLuong.setText("0");
            updateLoaiQDAndAnhHuong();
        } finally {
            isUpdatingCombos = false;
        }
    }
}