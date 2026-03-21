package com.hrm.DTO.Salary;

import java.math.BigDecimal;

public class SalaryDTO {
    private String maLuong;
    private String maNV;
    private String hoTen;    // Từ bảng nhanvien
    private String phongBan; // Từ bảng phongban
    private int thang;
    private int nam;
    private BigDecimal luongCoBan;
    private float soNgayCong;
    private BigDecimal tongPhuCap;
    private BigDecimal tongKhauTru;
    private BigDecimal thucLinh;
    private String trangThai;

    // Constructor, Getter và Setter
    public SalaryDTO() {}

    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getPhongBan() { return phongBan; }
    public void setPhongBan(String phongBan) { this.phongBan = phongBan; }
    public BigDecimal getThucLinh() { return thucLinh; }
    public void setThucLinh(BigDecimal thucLinh) { this.thucLinh = thucLinh; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    // ... bổ sung các getter/setter còn lại tương tự
}
