package com.hrm.Service;
import com.hrm.DTO.Manager.NhanVienDTO;
import com.hrm.DTO.Manager.ScheduleDTO;
import com.hrm.DAO.PhieuDanhGiaDAO;
import com.hrm.DAO.NhanVienDAO;
import com.hrm.DAO.ScheduleDAO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class NhanVienService {
    private NhanVienDAO dao = new NhanVienDAO();
    private ScheduleDAO scheduleDAO = new ScheduleDAO();
    private PhieuDanhGiaDAO phieuDanhGiaDAO = new PhieuDanhGiaDAO();

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
    // Format mỗi cell ca: "CODE|HOURS" (e.g., "S|06:00-14:00" hoặc "OFF")
    public Object[][] getTableDataForSchedule() {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.minusDays(today.getDayOfWeek().getValue() - 1);
        return getTableDataForSchedule(monday);
    }

    // Data cho bảng lịch làm việc theo tuần (weekStart là Thứ 2)
    // {ten|chucVu|maNV, ca_t2..cn}
    public Object[][] getTableDataForSchedule(LocalDate monday) {
        List<NhanVienDTO> list = dao.getAll();
        if (list == null || list.isEmpty()) return new Object[0][8];

        Object[][] data = new Object[list.size()][8];
        for (int i = 0; i < list.size(); i++) {
            NhanVienDTO nv = list.get(i);
            data[i][0] = nv.getHoten() + "|" + nv.getMachucvu() + "|" + nv.getManv(); // Cột tên nhân viên

            // Lấy lịch làm việc cho 7 ngày trong tuần
            for (int j = 0; j < 7; j++) {
                LocalDate day = monday.plusDays(j);
                ScheduleDTO schedule = scheduleDAO.getScheduleByEmployeeAndDate(nv.getManv(), day);
                
                if (schedule != null && schedule.getShift() != null) {
                    // Có schedule
                    if ("OFF".equals(schedule.getShift())) {
                        // Ngày OFF: chỉ hiển thị OFF, không cần giờ
                        data[i][j + 1] = "OFF";
                    } else {
                        // Ca làm bình thường -> hiển thị mã ca + giờ
                        String code = getShiftCode(schedule.getShift());
                        String time = schedule.getStartTime() + "-" + schedule.getEndTime();
                        data[i][j + 1] = code + "|" + time;
                    }
                } else {
                    // Không có schedule -> để trống
                    data[i][j + 1] = "";
                }
            }
        }
        return data;
    }

    /**
     * Chuyển đổi mã ca (C1, C2, ...) sang mã hiển thị (S, C, T, HC)
     */
    private String getShiftCode(String macalam) {
        switch (macalam) {
            case "C1": return "HC"; // Hành chính
            case "C2": return "S";  // Ca sáng
            case "C3": return "C";  // Ca chiều
            case "C4": return "T";  // Ca tối
            case "C5": return "S";  // Ca gãy sáng
            case "C6": return "C";  // Ca gãy chiều
            case "C7": return "T";  // Ca tăng cường
            default:   return "?";
        }
    }

    /**
     * Xóa toàn bộ lịch làm việc trong một tuần (thứ 2 -> chủ nhật)
     * cho tất cả nhân viên.
     *
     * @param monday ngày thứ 2 của tuần cần xóa
     * @return true nếu thao tác thành công, false nếu có lỗi
     */
    public boolean clearScheduleForWeek(LocalDate monday) {
        return scheduleDAO.deleteSchedulesForWeek(monday);
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

        String maDot = defaultMaDotNow();
        Object[][] data = new Object[list.size()][5];
        for (int i = 0; i < list.size(); i++) {
            NhanVienDTO nv = list.get(i);
            data[i][0] = nv.getHoten();
            data[i][1] = nv.getManv();
            data[i][2] = nv.getMachucvu();
            data[i][3] = nv.getMaphongban();
            boolean daDanhGia = phieuDanhGiaDAO.hasEvaluation(nv.getManv(), maDot);
            data[i][4] = daDanhGia ? "Đã hoàn thành" : "Chưa đánh giá";
        }
        return data;
    }

    public int countDaHoanThanhDanhGia() {
        return 0; // thực tế query từ bảng danhgia
    }

    private String defaultMaDotNow() {
        LocalDate now = LocalDate.now();
        int q = ((now.getMonthValue() - 1) / 3) + 1;
        return "Q" + q + "-" + now.getYear();
    }
}
