package com.hrm.UI.Manager.evaluation;

import com.hrm.DTO.Manager.TieuChiDanhGiaDTO;
import com.hrm.DAO.PhieuDanhGiaDAO;
import com.hrm.UI.Manager.evaluation.component.ScoreRadioPanel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
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
    
    private JTextField txtQuyetDinh;
    private JComboBox<String> comboLoaiQD;
    private boolean isUpdatingCombos = false;

    private static final String[] LOAI_QD_OPTIONS = new String[] { "Không có", "Thưởng", "Kỷ luật" };
    
    private List<ScoreRadioPanel> scorePanels;
    private Runnable onBackListener;
    private SaveListener onSaveListener;
    private PhieuDanhGiaDAO phieuDAO;  // THÊM DAO
    
    private String currentMaNV;
    private String currentMaDot;
    private String currentHoTen;
    private List<TieuChiDanhGiaDTO> currentCriteria;

    public interface SaveListener {
        void onSave(Map<String, Integer> scores, String nhanXet, String quyetDinh, String loaiQD);
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
        tieuChiWrapPanel.add(criteriaPanel, BorderLayout.CENTER);
        
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
        
        JPanel qdContentPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        qdContentPanel.setOpaque(false);
        qdContentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Quyết định
        JLabel lblQuyetDinh = new JLabel("Quyết định:");
        lblQuyetDinh.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtQuyetDinh = new JTextField();
        txtQuyetDinh.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtQuyetDinh.setColumns(30);
        
        // Loại quyết định
        JLabel lblLoaiQD = new JLabel("Loại quyết định:");
        lblLoaiQD.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboLoaiQD = new JComboBox<>(new String[]{
            "Không có", "Thưởng", "Kỷ luật"
        });
        comboLoaiQD.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        qdContentPanel.add(lblQuyetDinh);
        qdContentPanel.add(txtQuyetDinh);
        qdContentPanel.add(lblLoaiQD);
        qdContentPanel.add(comboLoaiQD);

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

    private void applyDecisionOptionsByLoai(String loaiQD) {
        // No-op: quyết định hiện là textbox để người dùng tự điền
    }
    
    private void syncLoaiWithQuyetDinh() {
        // No-op: quyết định hiện là textbox để người dùng tự điền
    }

    

    public void setData(String maNV, String hoTen, String maDot, 
                       List<TieuChiDanhGiaDTO> criteria, 
                       Map<String, Integer> savedScores,
                       String nhanXet,
                       String quyetDinh,
                       String loaiQD,
                       boolean locked) {
        System.out.println("🔍 DEBUG: EvaluationDetailPanel.setData() được gọi!");
        System.out.println("   maNV=" + maNV + ", quyetDinh=" + quyetDinh + ", loaiQD=" + loaiQD);
        System.out.println("   comboLoaiQD=" + comboLoaiQD);
        
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
            txtQuyetDinh.setText(quyetDinh != null ? quyetDinh : "");
            
            String normalizedLoaiQD = loaiQD;
            if ("Trừ lương".equals(normalizedLoaiQD)) {
                normalizedLoaiQD = "Kỷ luật";
            }
            if (normalizedLoaiQD == null) {
                normalizedLoaiQD = "Không có";
            }
            
            try {
                comboLoaiQD.setSelectedItem(normalizedLoaiQD);
            } catch (Exception e) {
                comboLoaiQD.setSelectedIndex(0);
            }
            
        } else {
            // Chưa khóa: tự động gợi ý quyết định theo tổng điểm
            txtQuyetDinh.setText("");
            updateDecisionByScore();
        }
        
        txtQuyetDinh.setEnabled(!locked);
        txtQuyetDinh.setEditable(!locked);
        comboLoaiQD.setEnabled(!locked);
        
        // Set trạng thái nút
        if (locked) {
            btnSave.setEnabled(false);
            btnReset.setEnabled(true);   // Đã lưu thì cho reset
        } else {
            btnSave.setEnabled(true);
            btnReset.setEnabled(false);  // Chưa lưu thì không cần reset
        }
        
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
                    currentCriteria, null, "", "", "Không có", false);
        } else {
            JOptionPane.showMessageDialog(this, "Reset thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveScores() {
        Map<String, Integer> scores = new HashMap<>();
        for (ScoreRadioPanel panel : scorePanels) {
            scores.put(panel.getMaTieuChi(), panel.getSelectedScore());
        }
        
        String loaiQD = String.valueOf(comboLoaiQD.getSelectedItem());
        String quyetDinh = txtQuyetDinh.getText();
        
        if (onSaveListener != null) {
            onSaveListener.onSave(scores, txtNhanXet.getText(), quyetDinh, loaiQD);
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
        
        int percent = (int) (totalRaw * 100.0 / maxRaw); // lấy phần nguyên để khớp mốc
        
        isUpdatingCombos = true;
        try {
            // Khi chưa chấm gì (percent = 0) thì loại quyết định phải là "Không có"
            if (percent == 0) {
                // Trạng thái đầu tiên: khôi phục lại list đầy đủ
                comboLoaiQD.setModel(new DefaultComboBoxModel<>(LOAI_QD_OPTIONS));
                comboLoaiQD.setSelectedItem("Không có");
                txtQuyetDinh.setText("");
                comboLoaiQD.revalidate();
                comboLoaiQD.repaint();
                return;
            }

            // Quyết định hiện là textbox tự điền -> chỉ tự động set "Loại quyết định"
            if (percent >= 75) {
                // 75-100: Thưởng
                comboLoaiQD.setModel(new DefaultComboBoxModel<>(new String[] { "Thưởng" }));
                comboLoaiQD.setSelectedItem("Thưởng");
            } else if (percent >= 65) {
                // 65-74: Giữ nguyên
                comboLoaiQD.setModel(new DefaultComboBoxModel<>(new String[] { "Không có" }));
                comboLoaiQD.setSelectedItem("Không có");
            } else {
                // <65: Phạt (nhắc nhở / cảnh cáo / kỷ luật)
                comboLoaiQD.setModel(new DefaultComboBoxModel<>(new String[] { "Kỷ luật" }));
                comboLoaiQD.setSelectedItem("Kỷ luật");
            }
            
            // Không auto-fill nội dung quyết định
            txtQuyetDinh.setText("");
            comboLoaiQD.revalidate();
            comboLoaiQD.repaint();
        } finally {
            isUpdatingCombos = false;
        }
    }
}