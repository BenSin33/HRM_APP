package com.hrm.DAO.HR;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.hrm.DTO.HR.ActivityDTO;
import com.hrm.utils.JDBCConection;

/**
 * Lấy dữ liệu hoạt động gần đây từ các bảng có sẵn (không tạo bảng mới).
 * Tổng hợp từ: nhanvien, nghiphep, bangluong, dotdanhgia, chamcong.
 */
public class ActivityDAO {

    public List<ActivityDTO> getAll() {
        Map<LocalDateTime, ActivityDTO> byDate = new LinkedHashMap<>();

        // 1. Nhân viên mới tuyển (NGAYVAOLAM trong 90 ngày gần)
        String sqlNV = "SELECT nv.HOTEN, pb.TENPHONGBAN, nv.NGAYVAOLAM FROM nhanvien nv " +
                "LEFT JOIN phongban pb ON nv.MAPHONGBAN = pb.MAPHONGBAN " +
                "WHERE nv.NGAYVAOLAM >= DATE_SUB(CURDATE(), INTERVAL 90 DAY) " +
                "ORDER BY nv.NGAYVAOLAM DESC LIMIT 5";
        runQuery(sqlNV, (rs, row) -> {
            String pb = rs.getString("TENPHONGBAN");
            String content = rs.getString("HOTEN") + " đã được tuyển dụng vào phòng " + (pb != null ? pb : "—");
            LocalDateTime dt = toLocalDateTime(rs.getDate("NGAYVAOLAM"));
            if (dt != null) byDate.put(dt.plusNanos(row), new ActivityDTO("nv-" + row, content, dt));
        });

        // 2. Đơn nghỉ phép gần đây
        String sqlNP = "SELECT nv.HOTEN, np.NGAYNGHI, np.TRANGTHAI FROM nghiphep np " +
                "JOIN nhanvien nv ON np.MANV = nv.MANV " +
                "ORDER BY np.NGAYNGHI DESC LIMIT 5";
        runQuery(sqlNP, (rs, row) -> {
            String ten = rs.getString("HOTEN");
            String status = rs.getString("TRANGTHAI");
            String content = status != null && status.contains("Chờ") ? ten + " đã gửi đơn xin nghỉ phép" : "Đơn nghỉ của " + ten + " (" + status + ")";
            LocalDateTime dt = toLocalDateTime(rs.getDate("NGAYNGHI"));
            if (dt != null) byDate.put(dt.plusNanos(row), new ActivityDTO("np-" + row, content, dt));
        });

        // 3. Bảng lương đã chốt
        String sqlBL = "SELECT THANG, NAM, NGAYCHOTLUONG FROM bangluong WHERE NGAYCHOTLUONG IS NOT NULL AND TRANGTHAI = 1 " +
                "GROUP BY THANG, NAM, NGAYCHOTLUONG ORDER BY NGAYCHOTLUONG DESC LIMIT 5";
        runQuery(sqlBL, (rs, row) -> {
            String content = "Đã chốt bảng lương tháng " + rs.getInt("THANG") + "/" + rs.getInt("NAM");
            LocalDateTime dt = toLocalDateTime(rs.getDate("NGAYCHOTLUONG"));
            if (dt != null) byDate.put(dt.plusNanos(row), new ActivityDTO("bl-" + row, content, dt));
        });

        // 4. Đợt đánh giá hoàn thành
        String sqlDG = "SELECT TENDOT, KYKY, NAM FROM dotdanhgia WHERE TRIM(COALESCE(TRANGTHAI,'')) IN ('Hoàn thành','Đã đóng','Đã khóa') " +
                "ORDER BY NAM DESC, KYKY DESC LIMIT 5";
        runQuery(sqlDG, (rs, row) -> {
            String ky = rs.getString("KYKY");
            String content = "Hoàn thành đánh giá " + (ky != null ? ky + " " : "") + rs.getInt("NAM");
            LocalDateTime dt = LocalDateTime.now().withYear(rs.getInt("NAM")).plusNanos(row);
            byDate.put(dt, new ActivityDTO("dg-" + row, content, dt));
        });

        // 5. Chấm công gần đây (admin view: lấy mẫu toàn công ty)
        String sqlCC = "SELECT nv.HOTEN, cc.NGAYLAMVIEC, cc.CHECKIN FROM chamcong cc " +
                "JOIN nhanvien nv ON cc.MANV = nv.MANV " +
                "ORDER BY cc.NGAYLAMVIEC DESC, cc.CHECKIN DESC LIMIT 5";
        runQuery(sqlCC, (rs, row) -> {
            java.sql.Time checkin = rs.getTime("CHECKIN");
            String content = rs.getString("HOTEN") + " đã chấm công ngày " + rs.getDate("NGAYLAMVIEC") + (checkin != null ? " vào lúc " + checkin : "");
            LocalDateTime dt = toLocalDateTime(rs.getDate("NGAYLAMVIEC"));
            if (dt != null) byDate.put(dt.plusNanos(row), new ActivityDTO("cc-" + row, content, dt));
        });

        List<ActivityDTO> list = new ArrayList<>(byDate.values());
        list.sort(Comparator.comparing(ActivityDTO::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        return list.size() > 10 ? list.subList(0, 10) : list;
    }

    private void runQuery(String sql, ResultSetConsumer consumer) {
        Connection conn = JDBCConection.getConnection();
        if (conn == null) return;
        try (conn;
             java.sql.Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            int row = 0;
            while (rs.next()) {
                row++;
                consumer.accept(rs, row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private LocalDateTime toLocalDateTime(java.sql.Date d) {
        if (d == null) return null;
        return d.toLocalDate().atStartOfDay();
    }

    @FunctionalInterface
    private interface ResultSetConsumer {
        void accept(ResultSet rs, int row) throws SQLException;
    }

    /** Không lưu DB — dữ liệu chỉ đọc từ bảng có sẵn. */
    public void add(ActivityDTO activity) { /* no-op */ }

    /** Không xóa trong DB — dữ liệu chỉ đọc. */
    public void delete(String id) { /* no-op */ }
}
