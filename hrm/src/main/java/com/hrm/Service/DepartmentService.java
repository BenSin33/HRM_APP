package com.hrm.Service;

import java.util.List;

import com.hrm.DAO.HR.DepartmentDAO;
import com.hrm.DTO.HR.DepartmentDTO;

/**
 * Service class for department management.
 */
public class DepartmentService {

    private DepartmentDAO departmentDAO = new DepartmentDAO();

    /**
     * Get all departments with employee count.
     */
    public List<DepartmentDTO> getAllDepartmentsWithEmployeeCount() {
        return departmentDAO.getAllWithEmployeeCount();
    }

    /**
     * Get all departments.
     */
    public List<DepartmentDTO> getAllDepartments() {
        return departmentDAO.getAll();
    }

    /**
     * Find department by ID.
     */
    public DepartmentDTO findDepartmentById(String maPhongBan) {
        return departmentDAO.findById(maPhongBan);
    }

    /**
     * Add a new department.
     */
    public void addDepartment(DepartmentDTO dto) {
        departmentDAO.add(dto);
    }

    /**
     * Update a department.
     */
    public void updateDepartment(DepartmentDTO dto) {
        departmentDAO.update(dto);
    }

    /**
     * Delete a department.
     */
    public void deleteDepartment(String maPhongBan) {
        departmentDAO.delete(maPhongBan);
    }

    /**
     * Count employees in a department.
     */
    public int countEmployeesInDepartment(String maPhongBan) {
        return departmentDAO.countEmployees(maPhongBan);
    }
}