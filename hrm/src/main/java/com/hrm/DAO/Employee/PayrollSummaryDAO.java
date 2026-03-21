package com.hrm.DAO.Employee;

import com.hrm.DTO.Employee.PayrollSummaryDTO;
import java.util.List;

public class PayrollSummaryDAO {
    public List<PayrollSummaryDTO> getPayrollSummaryByEmployeeId(String manv) {
        // TODO: Thay thế bằng truy vấn DB thực tế
        java.util.List<PayrollSummaryDTO> list = new java.util.ArrayList<>();
        // Giả lập dữ liệu
        PayrollSummaryDTO dto = new PayrollSummaryDTO(
            manv,
            "Nguyen Van A",
            20000000, // Lương cơ bản
            3000000,  // Thưởng
            500000,   // Khấu trừ
            22500000, // Thực lĩnh
            "Tháng 2/2026"
        );
        list.add(dto);
        return list;
    }
}
