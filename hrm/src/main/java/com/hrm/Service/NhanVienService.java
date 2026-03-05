package com.hrm.Service;
import com.hrm.DTO.Manager.NhanVienDTO;
import com.hrm.DAO.NhanVienDAO;
import java.util.List;

public class NhanVienService {
    private NhanVienDAO dao = new NhanVienDAO();

    // ==================== DASHBOARD ====================

    public int countAll() {
        List<NhanVienDTO> list = dao.getAll();
        return list != null ? list.size() : 0;
    }

    public int countDonChoDuyet() {
        List<NhanVienDTO> list = dao.getAll();
        if (list == null) return 0;
        return (int) list.stream()
            .filter(nv -> "CHO_DUYET".equals(nv.getTrangthai()))
            .count();
    }

    public int countNghiHomNay() {
        List<NhanVienDTO> list = dao.getAll();
        if (list == null) return 0;
        return (int) list.stream()
            .filter(nv -> "NGHI".equals(nv.getTrangthai()))
            .count();
    }

    public String getHieuSuatTrungBinh() {
        List<NhanVienDTO> list = dao.getAll();
        if (list == null || list.isEmpty()) return "0%";
        double avg = list.stream()
            .mapToInt(NhanVienDTO::getSongayphep)
            .average()
            .orElse(0);
        return (int) avg + "%";
    }

    public int countDonChoXuLy() {
        return dao.countDonChoXuLy();
    }

    public int countDotDanhGia() {
        List<NhanVienDTO> list = dao.getAll();
        if (list == null) return 0;
        return (int) list.stream()
            .filter(nv -> "CHO_DANH_GIA".equals(nv.getTrangthai()))
            .count();
    }

    // ==================== TEAM PANEL ====================

    public int countDangHoatDong() {
        List<NhanVienDTO> list = dao.getAll();
        if (list == null) return 0;
        return (int) list.stream()
            .filter(nv -> "Đang làm".equalsIgnoreCase(nv.getTrangthai()))
            .count();
    }

    public int countSenior() {
        List<NhanVienDTO> list = dao.getAll();
        if (list == null) return 0;
        return (int) list.stream()
            .filter(nv -> nv.getMachucvu() != null
                       && nv.getMachucvu().toLowerCase().contains("senior"))
            .count();
    }

    public int countJunior() {
        List<NhanVienDTO> list = dao.getAll();
        if (list == null) return 0;
        return (int) list.stream()
            .filter(nv -> nv.getMachucvu() != null
                       && nv.getMachucvu().toLowerCase().contains("junior"))
            .count();
    }

    // Data cho JTable TeamPanel
    // {maNV, hoTen, chucVu, lienHe, ngayVaoLam, trangThai, ""}
    public Object[][] getTableDataForTeam() {
        List<NhanVienDTO> list = dao.getAll();
        if (list == null || list.isEmpty()) return new Object[0][7];

        Object[][] data = new Object[list.size()][7];
        for (int i = 0; i < list.size(); i++) {
            NhanVienDTO nv = list.get(i);
            data[i][0] = nv.getManv();
            data[i][1] = nv.getHoten();
            data[i][2] = nv.getMachucvu();
            data[i][3] = (nv.getEmail() != null ? nv.getEmail() : "")
                       + (nv.getDienthoai() != null ? "\n" + nv.getDienthoai() : "");
            data[i][4] = nv.getNgayvaolam() != null ? nv.getNgayvaolam().toString() : "";
            data[i][5] = nv.getTrangthai();
            data[i][6] = "";
        }
        return data;
    }

    // ==================== SCHEDULE PANEL ====================

    // Data cho bảng lịch làm việc
    // {ten|chucVu, ca_t2, ca_t3, ca_t4, ca_t5, ca_t6, ca_t7, ca_cn}
    public Object[][] getTableDataForSchedule() {
        List<NhanVienDTO> list = dao.getAll();
        if (list == null || list.isEmpty()) return new Object[0][8];

        Object[][] data = new Object[list.size()][8];
        for (int i = 0; i < list.size(); i++) {
            NhanVienDTO nv = list.get(i);
            data[i][0] = nv.getHoten() + "|" + nv.getMachucvu();
            // Ca làm thực tế lấy từ bảng lichlamviec, tạm để trống
            for (int j = 1; j <= 7; j++) {
                data[i][j] = "";
            }
        }
        return data;
    }

    // ==================== LEAVE APPROVAL PANEL ====================

    // Data cho LeaveApprovalPanel
    // {ten, maNV, trangThai, loaiNghi, soNgay, tuNgay, denNgay, lyDo, ngayGui}
    public Object[][] getTableDataForLeave() {
        // Thực tế query từ bảng nghi_phep
        // Tạm trả về rỗng vì chưa có bảng nghi_phep
        return new Object[0][9];
    }

    // ==================== EVALUATION PANEL ====================

    // Data cho EvaluationPanel
    // {ten, maNV, chucVu, phongBan, trangThai}
    public Object[][] getTableDataForEvaluation() {
        List<NhanVienDTO> list = dao.getAll();
        if (list == null || list.isEmpty()) return new Object[0][5];

        Object[][] data = new Object[list.size()][5];
        for (int i = 0; i < list.size(); i++) {
            NhanVienDTO nv = list.get(i);
            data[i][0] = nv.getHoten();
            data[i][1] = nv.getManv();
            data[i][2] = nv.getMachucvu();
            data[i][3] = nv.getMaphongban();
            data[i][4] = "Chưa đánh giá"; // thực tế lấy từ bảng danhgia
        }
        return data;
    }

    public int countDaHoanThanhDanhGia() {
        return 0; // thực tế query từ bảng danhgia
    }
}
