package com.hrm.DTO.Employee;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SalaryDTO {
    public String maLuong;
    public String maNV;
    public String hoTen;
    public String phongBan;
    public int thang;
    public int nam;
    public BigDecimal luongCoBan;
    public float soNgayCong;
    public BigDecimal tongPhucap;
    public BigDecimal tongKhauTru;
    public BigDecimal thucLinh;
    public String trangThai;
    public LocalDate ngayChot;

    public SalaryDTO() {}

    public SalaryDTO(String maLuong, String maNV, String hoTen, String phongBan,
                     int thang, int nam, BigDecimal luongCoBan, float soNgayCong,
                     BigDecimal tongPhucap, BigDecimal tongKhauTru, BigDecimal thucLinh,
                     String trangThai, LocalDate ngayChot) {
        this.maLuong = maLuong;
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.phongBan = phongBan;
        this.thang = thang;
        this.nam = nam;
        this.luongCoBan = luongCoBan;
        this.soNgayCong = soNgayCong;
        this.tongPhucap = tongPhucap;
        this.tongKhauTru = tongKhauTru;
        this.thucLinh = thucLinh;
        this.trangThai = trangThai;
        this.ngayChot = ngayChot;
    }
}
