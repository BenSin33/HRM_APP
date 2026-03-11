package com.hrm.DAO.HR;

import com.hrm.DTO.HR.LeaveDTO.*;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class LeaveDao {

    private static final DateTimeFormatter DISPLAY_FMT =
            DateTimeFormatter.ofPattern("d/M/yyyy");


    public LeaveSummaryDTO getSummary() {
        LeaveSummaryDTO dto = new LeaveSummaryDTO();

        String sql = """
                SELECT
                    COUNT(*)                                                   AS total,
                    SUM(CASE WHEN NGUOIDUYET IS NULL
                              AND  NGAYDUYET IS NULL     THEN 1 ELSE 0 END)   AS pending,
                    SUM(CASE WHEN NGUOIDUYET IS NOT NULL
                              AND  NGAYDUYET IS NOT NULL THEN 1 ELSE 0 END)   AS approved,
                    SUM(CASE WHEN NGUOIDUYET IS NOT NULL
                              AND  NGAYDUYET IS NULL     THEN 1 ELSE 0 END)   AS rejected
                FROM nghiphep
                """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                dto.totalRequests = rs.getInt("total");
                dto.pendingCount  = rs.getInt("pending");
                dto.approvedCount = rs.getInt("approved");
                dto.rejectedCount = rs.getInt("rejected");
            }
        } catch (SQLException e) {
            System.err.println("[LeaveDao] getSummary: " + e.getMessage());
        }

        return dto;
    }

    // =============================================================
    // 2. LEAVE ROWS – danh sách đơn theo filter
    // =============================================================

    /**
     * Lấy danh sách đơn nghỉ phép kèm thông tin nhân viên + phòng ban.
     *
     * @param filter "all" | "pending" | "approved" | "rejected"
     *               Khớp với activeFilter trong LeaveTable
     */
    public List<LeaveRowDTO> getAllLeaveRows(String filter) {
        List<LeaveRowDTO> list = new ArrayList<>();

        String whereClause = switch (filter == null ? "all" : filter) {
            case "pending"  -> "AND np.NGUOIDUYET IS NULL AND np.NGAYDUYET IS NULL";
            case "approved" -> "AND np.NGUOIDUYET IS NOT NULL AND np.NGAYDUYET IS NOT NULL";
            case "rejected" -> "AND np.NGUOIDUYET IS NOT NULL AND np.NGAYDUYET IS NULL";
            default         -> "";
        };

        String sql = """
                SELECT
                    np.MANGHIPHEP,
                    np.MANV,
                    nv.HOTEN,
                    pb.TENPHONGBAN,
                    np.LOAINGHI,
                    np.LYDONGHI,
                    np.NGAYNGHI,
                    np.NGAYLAMLAI,
                    np.NGUOIDUYET,
                    np.NGAYDUYET
                FROM nghiphep np
                LEFT JOIN nhanvien nv ON np.MANV        = nv.MANV
                LEFT JOIN phongban pb ON nv.MAPHONGBAN  = pb.MAPHONGBAN
                WHERE 1=1
                """ + whereClause + "\nORDER BY np.NGAYNGHI DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                LeaveRowDTO dto = new LeaveRowDTO();
                dto.maNghiPhep = rs.getString("MANGHIPHEP");
                dto.manv       = rs.getString("MANV");
                dto.hoTen      = rs.getString("HOTEN");
                dto.phongBan   = rs.getString("TENPHONGBAN");
                dto.loaiNghi   = normalizeLoaiNghi(rs.getString("LOAINGHI"));
                dto.lyDo       = rs.getString("LYDONGHI");

                Date ngayNghi   = rs.getDate("NGAYNGHI");
                Date ngayLamLai = rs.getDate("NGAYLAMLAI");

                dto.tuNgay  = ngayNghi   != null ? ngayNghi.toLocalDate().format(DISPLAY_FMT)   : "";
                dto.denNgay = ngayLamLai != null ? ngayLamLai.toLocalDate().format(DISPLAY_FMT) : dto.tuNgay;
                dto.soNgay  = calcDays(ngayNghi, ngayLamLai);

                dto.trangThai = deriveStatus(rs.getString("NGUOIDUYET"), rs.getDate("NGAYDUYET"));

                list.add(dto);
            }
        } catch (SQLException e) {
            System.err.println("[LeaveDao] getAllLeaveRows: " + e.getMessage());
        }

        return list;
    }

    // =============================================================
    // 3. UPDATE STATUS – duyệt / từ chối đơn
    // =============================================================

    /**
     * Cập nhật trạng thái đơn nghỉ khi HR bấm ✓ hoặc ✕.
     *
     * Quy ước ghi DB:
     *   approve=true  → NGUOIDUYET = reviewer, NGAYDUYET = CURDATE()  → "Đã duyệt"
     *   approve=false → NGUOIDUYET = reviewer, NGAYDUYET = NULL        → "Từ chối"
     *
     * @param maNghiPhep mã đơn cần cập nhật
     * @param approve    true = duyệt, false = từ chối
     * @param reviewer   tên / mã HR đang đăng nhập
     * @return true nếu update thành công
     */
    public boolean updateStatus(String maNghiPhep, boolean approve, String reviewer) {
        String sql = approve
                ? "UPDATE nghiphep SET NGUOIDUYET = ?, NGAYDUYET = CURDATE() WHERE MANGHIPHEP = ?"
                : "UPDATE nghiphep SET NGUOIDUYET = ?, NGAYDUYET = NULL      WHERE MANGHIPHEP = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reviewer);
            ps.setString(2, maNghiPhep);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[LeaveDao] updateStatus: " + e.getMessage());
            return false;
        }
    }

    // =============================================================
    // HELPERS
    // =============================================================

    /**
     * Suy ra trạng thái hiển thị từ NGUOIDUYET / NGAYDUYET.
     */
    private String deriveStatus(String nguoiDuyet, Date ngayDuyet) {
        if (nguoiDuyet == null || nguoiDuyet.isBlank()) return "Chờ duyệt";
        return ngayDuyet != null ? "Đã duyệt" : "Từ chối";
    }

    /**
     * Chuẩn hóa loại nghỉ từ DB sang tên hiển thị trong bảng.
     */
    private String normalizeLoaiNghi(String raw) {
        if (raw == null) return "Nghỉ phép";
        return switch (raw.trim()) {
            case "Có lương"    -> "Nghỉ phép năm";
            case "Ốm"          -> "Nghỉ ốm";
            case "Ốm nhẹ"      -> "Nghỉ ốm";
            case "Việc riêng"  -> "Nghỉ việc riêng";
            case "Thai sản"    -> "Nghỉ thai sản";
            case "Không lương" -> "Nghỉ không lương";
            default            -> raw;
        };
    }

    /**
     * Tính số ngày nghỉ (inclusive).
     * VD: 1/2 → 3/2 = 3 ngày.
     */
    private int calcDays(Date from, Date to) {
        if (from == null) return 1;
        if (to   == null) return 1;
        long diff = java.time.temporal.ChronoUnit.DAYS.between(
                from.toLocalDate(), to.toLocalDate());
        return (int) diff + 1;
    }

    private Connection getConnection() throws SQLException {
        return com.hrm.utils.JDBCConection.getConnection();
    }
}