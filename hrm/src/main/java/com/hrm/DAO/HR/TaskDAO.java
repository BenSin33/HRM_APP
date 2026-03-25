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

import com.hrm.DTO.HR.TaskDTO;
import com.hrm.utils.JDBCConection;

/**
 * Lấy dữ liệu công việc cần xử lý từ các bảng có sẵn (không tạo bảng mới).
 * Tổng hợp từ: nghiphep (chờ duyệt), dotdanhgia (đang mở),
 * bangluong (chưa chốt), hopdong (sắp hết hạn).
 */
public class TaskDAO {

    public List<TaskDTO> getAll() {
        Map<String, TaskDTO> tasks = new LinkedHashMap<>();

        // 1. Đơn nghỉ phép chờ duyệt
        String sqlNP = "SELECT COUNT(*) AS so_luong FROM nghiphep WHERE TRIM(COALESCE(TRANGTHAI,'')) = 'Chờ duyệt'";
        runQuery(sqlNP, (rs, row) -> {
            int count = rs.getInt("so_luong");
            if (count > 0) {
                String content = count == 1 ? "Duyệt đơn xin nghỉ phép" : "Duyệt " + count + " đơn xin nghỉ phép";
                tasks.put("np", new TaskDTO("np-1", content, false, LocalDateTime.now()));
            }
        });

        // 2. Bảng lương tháng hiện tại chưa chốt
        String sqlBL = "SELECT MONTH(CURDATE()) AS thang_hien_tai, YEAR(CURDATE()) AS nam_hien_tai, COUNT(*) AS so_nv " +
                "FROM bangluong WHERE THANG = MONTH(CURDATE()) AND NAM = YEAR(CURDATE()) AND TRANGTHAI = 0";
        runQuery(sqlBL, (rs, row) -> {
            int thang = rs.getInt("thang_hien_tai");
            int nam = rs.getInt("nam_hien_tai");
            int soNv = rs.getInt("so_nv");
            if (soNv > 0) {
                String content = "Chốt bảng lương tháng " + thang + "/" + nam + " (" + soNv + " nhân viên)";
                tasks.put("bl", new TaskDTO("bl-1", content, false, LocalDateTime.now()));
            } else {
                // Tạo task thông báo nếu chưa có bản ghi nào
                String content = "Chốt bảng lương tháng " + thang + "/" + nam;
                tasks.put("bl", new TaskDTO("bl-1", content, false, LocalDateTime.now()));
            }
        });

        // 3. Đợt đánh giá đang mở
        String sqlDG = "SELECT MADOT, TENDOT FROM dotdanhgia WHERE TRIM(COALESCE(TRANGTHAI,'')) = 'Đang mở' ORDER BY NAM DESC, MADOT DESC";
        runQuery(sqlDG, (rs, row) -> {
            String ma = rs.getString("MADOT");
            String ten = rs.getString("TENDOT");
            String content = "Hoàn thành đợt đánh giá " + (ten != null ? ten : ma);
            tasks.put("dg", new TaskDTO("dg-1", content, false, LocalDateTime.now()));
        });

        // 4. Hợp đồng sắp hết hạn trong 30 ngày
        String sqlHD = "SELECT COUNT(*) AS so_hd FROM hopdong WHERE HANHOPDONG BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 30 DAY)";
        runQuery(sqlHD, (rs, row) -> {
            int count = rs.getInt("so_hd");
            if (count > 0) {
                String content = count == 1 ? "Cập nhật hợp đồng sắp hết hạn" : "Cập nhật " + count + " hợp đồng sắp hết hạn";
                tasks.put("hd", new TaskDTO("hd-1", content, false, LocalDateTime.now()));
            }
        });

        // 5. Nhân viên chưa có lịch làm việc tuần này
        String sqlLich = "SELECT COUNT(*) AS chua_co_lich FROM nhanvien nv " +
                "LEFT JOIN lichlamviec llv ON nv.MANV = llv.MANV " +
                "AND llv.NGAYLAMVIEC BETWEEN DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY) " +
                "AND DATE_ADD(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), INTERVAL 6 DAY) " +
                "WHERE nv.TRANGTHAI = 'Đang làm việc' AND llv.MANV IS NULL";
        runQuery(sqlLich, (rs, row) -> {
            int count = rs.getInt("chua_co_lich");
            if (count > 0) {
                String content = "Xếp lịch làm việc cho " + count + " nhân viên chưa có lịch";
                tasks.put("lich", new TaskDTO("lich-1", content, false, LocalDateTime.now()));
            }
        });

        List<TaskDTO> list = new ArrayList<>(tasks.values());
        list.sort(Comparator.comparing(TaskDTO::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())));
        return list;
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

    @FunctionalInterface
    private interface ResultSetConsumer {
        void accept(ResultSet rs, int row) throws SQLException;
    }

    /** Không lưu DB — dữ liệu chỉ đọc từ bảng có sẵn. */
    public void add(TaskDTO task) { /* no-op */ }

    /** Không xóa trong DB — dữ liệu chỉ đọc. */
    public void delete(String id) { /* no-op */ }

    /** Không cập nhật trong DB — dữ liệu chỉ đọc. */
    public void updateCompleted(String id, boolean completed) { /* no-op */ }
}
