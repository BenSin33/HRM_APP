package com.hrm.Service;

import com.hrm.DAO.Employee.PayrollSummaryDAO;
import com.hrm.DTO.Employee.PayrollSummaryDTO;
import java.util.List;

public class PayrollSummaryService {
    private PayrollSummaryDAO payrollSummaryDAO;

    public PayrollSummaryService() {
        this.payrollSummaryDAO = new PayrollSummaryDAO();
    }

    public List<PayrollSummaryDTO> getPayrollSummaryByEmployeeId(String manv) {
        return payrollSummaryDAO.getPayrollSummaryByEmployeeId(manv);
    }
}
