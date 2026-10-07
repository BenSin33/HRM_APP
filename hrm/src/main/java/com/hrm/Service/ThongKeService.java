package com.hrm.Service;

import com.hrm.DAO.ThongKeDAO;
import com.hrm.DTO.ContractDTO;
import java.util.List;
import java.util.Map;

public class ThongKeService {
    private final ThongKeDAO thongKeDAO;

    public ThongKeService() {
        this.thongKeDAO = new ThongKeDAO();
    }

    public Map<String, Integer> getEmployeeCountByDepartment() {
        return thongKeDAO.getEmployeeCountByDepartment();
    }

    public Map<String, Double> getAverageSalaryByMonth() {
        return thongKeDAO.getAverageSalaryByMonth();
    }

    public Map<String, Double> getTopEmployeePerformance() {
        return thongKeDAO.getTopEmployeePerformance();
    }

    public Map<String, Integer> getContractDiscrepancy() {
        return thongKeDAO.getContractDiscrepancy();
    }

    public Map<String, Integer> getContractTypeDistribution() {
        return thongKeDAO.getContractTypeDistribution();
    }

    public List<ContractDTO> getContractDetails(String filter) {
        return thongKeDAO.getContractDetails(filter);
    }
}
