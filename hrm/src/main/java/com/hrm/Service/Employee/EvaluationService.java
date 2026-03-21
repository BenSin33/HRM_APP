package com.hrm.Service.Employee;

import com.hrm.DAO.Employee.EvaluationDAO;
import com.hrm.DTO.Employee.EvaluationHistoryDTO;

import java.util.List;

public class EvaluationService {
    private EvaluationDAO evaluationDAO;

    public EvaluationService() {
        this.evaluationDAO = new EvaluationDAO();
    }

    public List<EvaluationHistoryDTO> getEvaluationHistory(String manv) {
        return evaluationDAO.getEvaluationHistory(manv);
    }
}
