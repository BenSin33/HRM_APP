package com.hrm.Service.Employee;

import com.hrm.DAO.Employee.AttendanceDAO;
import com.hrm.DTO.Employee.AttendanceDTO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class AttendanceService {
    private AttendanceDAO attendanceDAO;

    public AttendanceService() {
        this.attendanceDAO = new AttendanceDAO();
    }

    public Map<String, String> getMonthlyStats(String manv) {
        return attendanceDAO.getMonthlyStats(manv);
    }

    public boolean checkAlreadyCheckedIn(String manv) throws SQLException {
        return attendanceDAO.checkAlreadyCheckedIn(manv);
    }

    public boolean insertCheckIn(String manv) throws SQLException {
        return attendanceDAO.insertCheckIn(manv);
    }

    public boolean updateCheckOut(String manv) throws SQLException {
        return attendanceDAO.updateCheckOut(manv);
    }

    public Map<Integer, String> getAttendanceMap(String manv, int month, int year) {
        return attendanceDAO.getAttendanceMap(manv, month, year);
    }

    public AttendanceDTO getAttendanceDetail(String manv, int day, int month, int year) {
        return attendanceDAO.getAttendanceDetail(manv, day, month, year);
    }

    public LinkedHashMap<String, String> getShiftDisplayMap() {
        return attendanceDAO.getShiftDisplayMap();
    }

    public ArrayList<AttendanceDTO> searchAttendance(String manv, Integer day, Integer month, Integer year, String trangThai, String maCaLam) {
        return attendanceDAO.searchAttendance(manv, day, month, year, trangThai, maCaLam);
    }
}
