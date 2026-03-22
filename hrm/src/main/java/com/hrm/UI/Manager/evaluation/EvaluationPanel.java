package com.hrm.UI.Manager.evaluation;

import com.hrm.UI.Manager.color.ColorScheme;
import com.hrm.Service.NhanVienService;
import com.hrm.DTO.Manager.TieuChiDanhGiaDTO;
import com.hrm.DAO.TieuChiDanhGiaDAO;
import com.hrm.DAO.PhieuDanhGiaDAO;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

public class EvaluationPanel extends JPanel {
    private NhanVienService nhanVienService;
    private TieuChiDanhGiaDAO tieuChiDAO;
    private PhieuDanhGiaDAO phieuDAO;
    
    private CardLayout cardLayout;
    private JPanel cardPanel;
    
    private EvaluationHeader header;
    private EvaluationStats stats;
    private EvaluationList listPanel;           // Panel danh sách
    private EvaluationDetailPanel detailPanel;   // Panel chi tiết mới
    
    private String currentMaNV;
    private String currentHoTen;
    private String currentMaDot;

    public EvaluationPanel() {
        setLayout(new BorderLayout());
        setBackground(ColorScheme.MAIN_BG);

        // Khởi tạo Service và DAO
        nhanVienService = new NhanVienService();
        tieuChiDAO = new TieuChiDanhGiaDAO();
        phieuDAO = new PhieuDanhGiaDAO();
        
        currentMaDot = TieuChiDanhGiaDialog.defaultMaDotNow();

        // Header (luôn hiển thị)
        header = new EvaluationHeader();
        add(header, BorderLayout.NORTH);

        // Content Area với CardLayout
        JPanel contentArea = new JPanel(new BorderLayout(0, 20));
        contentArea.setBackground(ColorScheme.MAIN_BG);
        contentArea.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        // Stats (luôn hiển thị phía trên)
        stats = new EvaluationStats();
        contentArea.add(stats, BorderLayout.NORTH);

        // CardLayout cho 2 panel
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);
        
        // Panel danh sách
        listPanel = new EvaluationList();
        listPanel.setEmployeeClickListener(this::showDetailPanel);
        
        // Panel chi tiết (sẽ tạo sau)
        detailPanel = new EvaluationDetailPanel(this::backToList, this::saveEvaluation);
        
        cardPanel.add(listPanel, "LIST");
        cardPanel.add(detailPanel, "DETAIL");
        
        contentArea.add(cardPanel, BorderLayout.CENTER);
        add(contentArea, BorderLayout.CENTER);
        
        // Load dữ liệu ban đầu
        loadData();
    }

    private void loadData() {
        Object[][] data = nhanVienService.getTableDataForEvaluation();
        
        listPanel.setQuarter(currentMaDot);
        listPanel.setData(data);

        int tongNV = data.length;
        int daHoan = listPanel.getDaHoanThanhCount();
        int chuaDG = listPanel.getChuaDanhGiaCount();

        stats.updateStats(tongNV, daHoan, chuaDG);
    }

    private void showDetailPanel(String maNV, String hoTen) {
        System.out.println("🔍 DEBUG: EvaluationPanel.showDetailPanel() called!");
        System.out.println("   maNV=" + maNV + ", hoTen=" + hoTen);
        
        this.currentMaNV = maNV;
        this.currentHoTen = hoTen;
        
        // Lấy danh sách tiêu chí
        List<TieuChiDanhGiaDTO> criteria = tieuChiDAO.getAll();
        
        // Kiểm tra đã có đánh giá chưa
        boolean isLocked = phieuDAO.hasEvaluation(maNV, currentMaDot);
        Map<String, Integer> savedScores = null;
        String nhanXet = "";
        String quyetDinh = "Không có";  // ComboBox: Không có/Thưởng/Kỷ luật
        String loaiQD = "";             // TextField: chi tiết của loại quyết định
        BigDecimal tiLeThayDoi = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        
        if (isLocked) {
            savedScores = phieuDAO.getScoresByCriteria(maNV, currentMaDot);
            nhanXet = phieuDAO.getNhanXet(maNV, currentMaDot);
            quyetDinh = phieuDAO.getQuyetDinh(maNV, currentMaDot);
            loaiQD = phieuDAO.getLoaiQuyetDinh(maNV, currentMaDot);
            tiLeThayDoi = phieuDAO.getTiLeThayDoi(maNV, currentMaDot);
            System.out.println("   Loaded from DB: quyetDinh=" + quyetDinh + ", loaiQD=" + loaiQD + ", tiLe=" + tiLeThayDoi);
        }
        
        // Cập nhật panel chi tiết
        System.out.println("   Calling detailPanel.setData()...");
        detailPanel.setData(maNV, hoTen, currentMaDot, criteria, savedScores, nhanXet, quyetDinh, loaiQD, tiLeThayDoi, isLocked);
        
        // Chuyển sang panel chi tiết
        cardLayout.show(cardPanel, "DETAIL");
        System.out.println("   ✓ Switched to DETAIL panel");
    }

    private void backToList() {
        cardLayout.show(cardPanel, "LIST");
        loadData(); // Refresh dữ liệu
    }

    private void saveEvaluation(Map<String, Integer> scores, String nhanXet, String quyetDinh, String loaiQD, BigDecimal tiLeThayDoi) {
        boolean success = phieuDAO.upsertEvaluation(currentMaNV, currentMaDot, scores, nhanXet, quyetDinh, loaiQD, tiLeThayDoi);
        if (success) {
            JOptionPane.showMessageDialog(this, "Lưu điểm thành công!");
            // Ở lại panel, reload lại để hiển thị trạng thái đã lưu (locked)
            showDetailPanel(currentMaNV, currentHoTen);
        } else {
            JOptionPane.showMessageDialog(this, "Lưu điểm thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refresh() {
        loadData();
    }
}