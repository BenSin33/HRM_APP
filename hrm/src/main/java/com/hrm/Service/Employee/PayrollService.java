package com.hrm.Service.Employee;

import com.hrm.DAO.Employee.PayrollDAO;

import java.util.List;
import java.util.Map;

public class PayrollService {
    private PayrollDAO payrollDAO;

    public PayrollService() {
        this.payrollDAO = new PayrollDAO();
    }

    public List<Map<String, Object>> getSalaryHistory(String manv) {
        return payrollDAO.getSalaryHistory(manv);
    }

    public List<Map<String, Object>> getPayrollDetails(String manv) {
        return payrollDAO.getPayrollDetails(manv);
    }

    public Map<String, Object> getCurrentMonthPayroll(String manv) {
        return payrollDAO.getCurrentMonthPayroll(manv);
    }

    public Map<String, Object> getPayrollByMonth(String manv, int thang, int nam) {
        return payrollDAO.getPayrollByMonth(manv, thang, nam);
    }

    public List<Map<String, Object>> getPayrollDetailItems(String maluong) {
        return payrollDAO.getPayrollDetailItems(maluong);
    }

    public List<Map<String, Object>> getAllAllowances() {
        return payrollDAO.getAllAllowances();
    }

    public List<Map<String, Object>> getAllDeductions() {
        return payrollDAO.getAllDeductions();
    }
}
