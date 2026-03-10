package com.hrm.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.hrm.DTO.Manager.ScheduleDTO;
import com.hrm.utils.JDBCConection;

public class ScheduleDAO {
        // Lấy tất cả ca làm việc từ bảng calam (trừ OFF – OFF là ca đặc biệt chỉ dùng để đánh dấu nghỉ)
        public List<ScheduleDTO> getAllShifts() {
            List<ScheduleDTO> shifts = new ArrayList<>();
            String sql = "SELECT MACALAM, TENCALAM, GIOVAOCA, GIOTANCA FROM calam WHERE MACALAM <> 'OFF'";
            try (Connection conn = JDBCConection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    ScheduleDTO shift = new ScheduleDTO();
                    shift.setShift(rs.getString("MACALAM"));
                    shift.setShiftName(rs.getString("TENCALAM"));
                    shift.setStartTime(rs.getString("GIOVAOCA"));
                    shift.setEndTime(rs.getString("GIOTANCA"));
                    shifts.add(shift);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return shifts;
        }
    public List<ScheduleDTO> getSchedulesForEmployeeAndWeek(String manv, java.time.LocalDate weekStart) {
        List<ScheduleDTO> schedules = new ArrayList<>();
        String sql = "SELECT l.NGAYLAMVIEC, l.MACALAM, c.TENCALAM, c.GIOVAOCA, c.GIOTANCA, l.GHICHU " +
                "FROM lichlamviec l " +
                "LEFT JOIN calam c ON l.MACALAM = c.MACALAM " +
                "WHERE l.MANV = ? AND l.NGAYLAMVIEC BETWEEN ? AND ?";
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, manv);
            ps.setDate(2, java.sql.Date.valueOf(weekStart));
            ps.setDate(3, java.sql.Date.valueOf(weekStart.plusDays(6)));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ScheduleDTO schedule = new ScheduleDTO();
                schedule.setDate(rs.getDate("NGAYLAMVIEC"));
                schedule.setShift(rs.getString("MACALAM"));
                schedule.setShiftName(rs.getString("TENCALAM"));
                schedule.setStartTime(rs.getString("GIOVAOCA"));
                schedule.setEndTime(rs.getString("GIOTANCA"));
                schedule.setDescription(rs.getString("GHICHU"));
                schedules.add(schedule);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return schedules;
    }

    public ScheduleDTO getScheduleByEmployeeAndDate(String manv, java.time.LocalDate date) {
        String sql = "SELECT l.NGAYLAMVIEC, l.MACALAM, c.TENCALAM, c.GIOVAOCA, c.GIOTANCA, l.GHICHU " +
                "FROM lichlamviec l " +
                "LEFT JOIN calam c ON l.MACALAM = c.MACALAM " +
                "WHERE l.MANV = ? AND l.NGAYLAMVIEC = ?";
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, manv);
            ps.setDate(2, java.sql.Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ScheduleDTO schedule = new ScheduleDTO();
                schedule.setDate(rs.getDate("NGAYLAMVIEC"));
                schedule.setShift(rs.getString("MACALAM"));
                schedule.setShiftName(rs.getString("TENCALAM"));
                schedule.setStartTime(rs.getString("GIOVAOCA"));
                schedule.setEndTime(rs.getString("GIOTANCA"));
                schedule.setDescription(rs.getString("GHICHU"));
                return schedule;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lưu hoặc cập nhật lịch làm việc
     * @param manv Mã nhân viên
     * @param date Ngày làm việc
     * @param macalam Mã ca làm (C1, C2, ... hoặc 'OFF' cho ngày nghỉ)
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean saveOrUpdateSchedule(String manv, java.time.LocalDate date, String macalam) {
        try (Connection conn = JDBCConection.getConnection()) {
            // Kiểm tra xem record đã tồn tại chưa
            String checkSql = "SELECT MALICH FROM lichlamviec WHERE MANV = ? AND NGAYLAMVIEC = ?";
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setString(1, manv);
            checkPs.setDate(2, java.sql.Date.valueOf(date));
            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {
                // UPDATE: Record đã tồn tại
                String updateSql = "UPDATE lichlamviec SET MACALAM = ? WHERE MANV = ? AND NGAYLAMVIEC = ?";
                PreparedStatement updatePs = conn.prepareStatement(updateSql);
                updatePs.setString(1, macalam);
                updatePs.setString(2, manv);
                updatePs.setDate(3, java.sql.Date.valueOf(date));
                int result = updatePs.executeUpdate();
                updatePs.close();
                return result > 0;
            } else {
                // INSERT: Tạo record mới
                String insertSql = "INSERT INTO lichlamviec (MALICH, MANV, MACALAM, NGAYLAMVIEC, GHICHU) " +
                        "VALUES (?, ?, ?, ?, ?)";
                PreparedStatement insertPs = conn.prepareStatement(insertSql);
                
                // Generate MALICH (L + timestamp hoặc auto-increment)
                String malich = generateMaLich();
                insertPs.setString(1, malich);
                insertPs.setString(2, manv);
                insertPs.setString(3, macalam);
                insertPs.setDate(4, java.sql.Date.valueOf(date));
                insertPs.setString(5, null); // GHICHU
                
                int result = insertPs.executeUpdate();
                insertPs.close();
                return result > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa lịch làm việc (khi user chọn OFF)
     * @param manv Mã nhân viên
     * @param date Ngày làm việc
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean deleteSchedule(String manv, java.time.LocalDate date) {
        String sql = "DELETE FROM lichlamviec WHERE MANV = ? AND NGAYLAMVIEC = ?";
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, manv);
            ps.setDate(2, java.sql.Date.valueOf(date));
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa toàn bộ lịch làm việc trong một tuần cho tất cả nhân viên.
     * Tuần được xác định bởi ngày bắt đầu (thứ 2) đến chủ nhật (weekStart + 6).
     *
     * @param weekStart ngày thứ 2 của tuần cần xóa
     * @return true nếu xóa thành công, false nếu có lỗi
     */
    public boolean deleteSchedulesForWeek(java.time.LocalDate weekStart) {
        String sql = "DELETE FROM lichlamviec WHERE NGAYLAMVIEC BETWEEN ? AND ?";
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(weekStart));
            ps.setDate(2, java.sql.Date.valueOf(weekStart.plusDays(6)));
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Generate mã lịch (MALICH) duy nhất
     * Format: L + số tự tăng từ DB
     */
    private String generateMaLich() {
        String sql = "SELECT MAX(CAST(SUBSTRING(MALICH, 2) AS UNSIGNED)) as maxNum FROM lichlamviec";
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int maxNum = rs.getInt("maxNum");
                return "L" + (maxNum + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Fallback: sử dụng timestamp
        return "L" + System.currentTimeMillis();
    }
}
