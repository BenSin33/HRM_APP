package com.hrm.UI.Manager.evaluation;

import com.hrm.UI.Manager.color.ColorScheme;
import com.hrm.Service.NhanVienService;
import com.hrm.DTO.Manager.TieuChiDanhGiaDTO;
import com.hrm.DAO.TieuChiDanhGiaDAO;
import com.hrm.DAO.PhieuDanhGiaDAO;
import com.hrm.DAO.NhanVienDAO;
import com.hrm.DTO.Manager.NhanVienDTO;
import com.hrm.DTO.UserDTO;
import com.hrm.utils.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class EvaluationPanel extends JPanel {
    private NhanVienService nhanVienService;
    private TieuChiDanhGiaDAO tieuChiDAO;
    private PhieuDanhGiaDAO phieuDAO;
    private NhanVienDAO nhanVienDAO;
    
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
        nhanVienDAO = new NhanVienDAO();
        
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
        // Lấy thông tin manager hiện tại
        UserDTO currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Lỗi: Không tìm thấy thông tin người dùng", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Lấy thông tin manager từ DB để có mã phòng ban
        NhanVienDTO manager = nhanVienDAO.findById(currentUser.getManv());
        if (manager == null) {
            JOptionPane.showMessageDialog(this, "Lỗi: Không tìm thấy thông tin manager", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String maphongban = manager.getMaphongban();
        if (maphongban == null || maphongban.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lỗi: Manager chưa được gán phòng ban", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Load dữ liệu theo phòng ban
        Object[][] data = nhanVienService.getTableDataForEvaluationByPhongBan(maphongban);
        
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
        String quyetDinh = "Giữ nguyên";
        String loaiQD = "Không có";
        
        if (isLocked) {
            savedScores = phieuDAO.getScoresByCriteria(maNV, currentMaDot);
            nhanXet = phieuDAO.getNhanXet(maNV, currentMaDot);
            quyetDinh = phieuDAO.getQuyetDinh(maNV, currentMaDot);
            loaiQD = phieuDAO.getLoaiQuyetDinh(maNV, currentMaDot);
            System.out.println("   Loaded from DB: quyetDinh=" + quyetDinh + ", loaiQD=" + loaiQD);
        }
        
        // Cập nhật panel chi tiết
        System.out.println("   Calling detailPanel.setData()...");
        detailPanel.setData(maNV, hoTen, currentMaDot, criteria, savedScores, nhanXet, quyetDinh, loaiQD, isLocked);
        
        // Chuyển sang panel chi tiết
        cardLayout.show(cardPanel, "DETAIL");
        System.out.println("   ✓ Switched to DETAIL panel");
    }

    private void backToList() {
        cardLayout.show(cardPanel, "LIST");
        loadData(); // Refresh dữ liệu
    }

    private void saveEvaluation(Map<String, Integer> scores, String nhanXet, String quyetDinh, String loaiQD) {
        boolean success = phieuDAO.upsertEvaluation(currentMaNV, currentMaDot, scores, nhanXet, quyetDinh, loaiQD);
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