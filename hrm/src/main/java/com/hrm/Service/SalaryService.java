package com.hrm.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import com.hrm.DAO.Employee.SalaryDAO;
import com.hrm.DTO.Employee.SalaryDTO;

public class SalaryService {
    private SalaryDAO salaryDAO;

    public SalaryService() {
        this.salaryDAO = new SalaryDAO();
    }

    public List<SalaryDTO> getAllSalaries() {
        return salaryDAO.getAllSalaries();
    }

    public List<SalaryDTO> getSalariesByMonthYear(int thang, int nam) {
        return salaryDAO.getSalariesByMonthYear(thang, nam);
    }

    public SalaryDTO getSalaryByMaNV(String maNV, int thang, int nam) {
        return salaryDAO.getSalaryByMaNV(maNV, thang, nam);
    }

    public boolean updateSalary(SalaryDTO salary) {
        return salaryDAO.updateSalary(salary);
    }

    public boolean lockSalariesByMonth(int thang, int nam) {
        return salaryDAO.lockSalariesByMonth(thang, nam);
    }

    public boolean unlockSalariesByMonth(int thang, int nam) {
        return salaryDAO.unlockSalariesByMonth(thang, nam);
    }

    public List<SalaryDTO> getSalariesByMonthYearAndStatus(int thang, int nam, String trangThai) {
        return salaryDAO.getSalariesByMonthYearAndStatus(thang, nam, trangThai);
    }

    public List<SalaryDTO> searchSalaries(int thang, int nam, String keyword) {
        return salaryDAO.searchSalaries(thang, nam, keyword);
    }
    
    public List<SalaryDTO> getSalariesByDateRange(int fromMonth, int fromYear, int toMonth, int toYear) {
        return salaryDAO.getSalariesByDateRange(fromMonth, fromYear, toMonth, toYear);
    }

    public SalaryStatistics getSalaryStatistics(int thang, int nam) {
        List<SalaryDTO> salaries = getSalariesByMonthYear(thang, nam);
        
        BigDecimal totalSalary = BigDecimal.ZERO;
        BigDecimal averageSalary = BigDecimal.ZERO;
        int employeeCount = salaries.size();

        for (SalaryDTO salary : salaries) {
            if (salary.thucLinh != null) {
                totalSalary = totalSalary.add(salary.thucLinh);
            }
        }

        if (employeeCount > 0) {
            averageSalary = totalSalary.divide(new BigDecimal(employeeCount), RoundingMode.HALF_UP);
        }

        return new SalaryStatistics(totalSalary, averageSalary, employeeCount);
    }

    public static class SalaryStatistics {
        public BigDecimal totalSalary;
        public BigDecimal averageSalary;
        public int employeeCount;

        public SalaryStatistics(BigDecimal totalSalary, BigDecimal averageSalary, int employeeCount) {
            this.totalSalary = totalSalary;
            this.averageSalary = averageSalary;
            this.employeeCount = employeeCount;
        }
    }
}
