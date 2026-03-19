package com.hrm.Service.Employee;

import com.hrm.DAO.Employee.LeaveDAO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class LeaveService {
    private LeaveDAO leaveDAO;

    public LeaveService() {
        this.leaveDAO = new LeaveDAO();
    }

    public boolean insertLeaveRequest(Map<String, Object> leaveData) {
        return leaveDAO.insertLeaveRequest(leaveData);
    }

    public boolean updateLeaveRequest(Map<String, Object> leaveData) {
        return leaveDAO.updateLeaveRequest(leaveData);
    }

    public List<Map<String, Object>> getLeaveRequestsByEmployee(String manv) {
        return leaveDAO.getLeaveRequestsByEmployee(manv);
    }

    public String generateLeaveRequestId() {
        return leaveDAO.generateLeaveRequestId();
    }

    public int getTotalLeaveRequestCount(String manv) {
        return leaveDAO.getTotalLeaveRequestCount(manv);
    }

    public int getApprovedPaidLeaveCount(String manv) {
        return leaveDAO.getApprovedPaidLeaveCount(manv);
    }

    public int getUnpaidLeaveCount(String manv) {
        return leaveDAO.getUnpaidLeaveCount(manv);
    }
}
