package com.hrm.DTO;

/**
 * DTO cho danh mục phòng ban (bảng phongban)
 */
public class DepartmentCategoryDTO {
    private String maPhongBan;
    private String tenPhongBan;

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
}
