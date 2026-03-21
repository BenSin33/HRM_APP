package com.hrm.Service;

import java.util.List;

import com.hrm.DAO.HR.DepartmentDAO;
import com.hrm.DTO.HR.DepartmentDTO;

/**
 * Service class cho phần HR sử dụng DepartmentDAO/DepartmentDTO trong package HR.
 */
public class DepartmentService {

    private final DepartmentDAO departmentDAO = new DepartmentDAO();

    /**
     * Lấy tất cả phòng ban kèm số lượng nhân viên.
     */
    public List<DepartmentDTO> getAllDepartmentsWithEmployeeCount() {
        return departmentDAO.getAllWithEmployeeCount();
    }

    /**
     * Lấy tất cả phòng ban.
     */
    public List<DepartmentDTO> getAllDepartments() {
        return departmentDAO.getAll();
    }

    /**
     * Sinh mã phòng ban tiếp theo (PB01, PB02, ...).
     */
    public String generateNextMaPhongBan() {
        return departmentDAO.generateNextMaPhongBan();
    }

    /**
     * Tìm phòng ban theo mã.
     */
    public DepartmentDTO findDepartmentById(String maPhongBan) {
        return departmentDAO.findById(maPhongBan);
    }

    /**
     * Thêm phòng ban.
     */
    public void addDepartment(DepartmentDTO dto) {
        departmentDAO.add(dto);
    }

    /**
     * Cập nhật phòng ban.
     */
    public void updateDepartment(DepartmentDTO dto) {
        departmentDAO.update(dto);
    }

    /**
     * Xóa phòng ban.
     */
    public void deleteDepartment(String maPhongBan) {
        departmentDAO.delete(maPhongBan);
    }

    /**
     * Đếm số nhân viên trong 1 phòng ban.
     */
    public int countEmployeesInDepartment(String maPhongBan) {
        return departmentDAO.countEmployees(maPhongBan);
    }

    public List<DepartmentDAO.EmployeeOption> getEmployeesInDepartment(String maPhongBan) {
        return departmentDAO.getEmployeesInDepartment(maPhongBan);
    }

    public void setDepartmentHead(String maPhongBan, String manv) {
        departmentDAO.setDepartmentHead(maPhongBan, manv);
    }

    public void updateEmployeeContact(String manv, String email, String dienthoai) {
        departmentDAO.updateEmployeeContact(manv, email, dienthoai);
    }
}