package com.hrm.Service;

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

    public boolean lockSalaries(int thang, int nam) {
        return salaryDAO.lockSalaries(thang, nam);
    }
}
