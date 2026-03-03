package com.hrm.DAO.Employee;

import com.hrm.DTO.Employee.ScheduleDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.hrm.utils.JDBCConection;

public class ScheduleDAO {
        // Lấy tất cả ca làm việc từ bảng calam
        public List<ScheduleDTO> getAllShifts() {
            List<ScheduleDTO> shifts = new ArrayList<>();
            String sql = "SELECT MACALAM, TENCALAM, GIOVAOCA, GIOTANCA FROM calam";
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
}
