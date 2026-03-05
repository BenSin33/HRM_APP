package com.hrm.DTO.HR;

/**
 * Simple data transfer object for a department (phòng ban).
 */
public class DepartmentDTO {
    private String maPhongBan;
    private String tenPhongBan;
    private int soNhanVien;    // optional, computed on the fly if needed

    public DepartmentDTO() {
    }

    public DepartmentDTO(String maPhongBan, String tenPhongBan) {
        this.maPhongBan = maPhongBan;
        this.tenPhongBan = tenPhongBan;
    }

    public DepartmentDTO(String maPhongBan, String tenPhongBan, int soNhanVien) {
        this(maPhongBan, tenPhongBan);
        this.soNhanVien = soNhanVien;
    }

    public String getMaPhongBan() {
        return maPhongBan;
    }

    public void setMaPhongBan(String maPhongBan) {
        this.maPhongBan = maPhongBan;
    }

    public String getTenPhongBan() {
        return tenPhongBan;
    }

    public void setTenPhongBan(String tenPhongBan) {
        this.tenPhongBan = tenPhongBan;
    }

    public int getSoNhanVien() {
        return soNhanVien;
    }

    public void setSoNhanVien(int soNhanVien) {
        this.soNhanVien = soNhanVien;
    }
}
