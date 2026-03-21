<<<<<<< HEAD
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
    public String tinhTrangThanToan;
    public BigDecimal hesotrinhdo;  // Hệ số trình độ từ bảng trinhdo
    public BigDecimal phucapChucVu;  // Phụ cấp chức vụ từ bảng chucvu

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

    public String getMaLuong() {
        return maLuong;
    }

    public void setMaLuong(String maLuong) {
        this.maLuong = maLuong;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getPhongBan() {
        return phongBan;
    }

    public void setPhongBan(String phongBan) {
        this.phongBan = phongBan;
    }

    public int getThang() {
        return thang;
    }

    public void setThang(int thang) {
        this.thang = thang;
    }

    public int getNam() {
        return nam;
    }

    public void setNam(int nam) {
        this.nam = nam;
    }

    public BigDecimal getLuongCoBan() {
        return luongCoBan;
    }

    public void setLuongCoBan(BigDecimal luongCoBan) {
        this.luongCoBan = luongCoBan;
    }

    public float getSoNgayCong() {
        return soNgayCong;
    }

    public void setSoNgayCong(float soNgayCong) {
        this.soNgayCong = soNgayCong;
    }

    public BigDecimal getTongPhucap() {
        return tongPhucap;
    }

    public void setTongPhucap(BigDecimal tongPhucap) {
        this.tongPhucap = tongPhucap;
    }

    public BigDecimal getTongKhauTru() {
        return tongKhauTru;
    }

    public void setTongKhauTru(BigDecimal tongKhauTru) {
        this.tongKhauTru = tongKhauTru;
    }

    public BigDecimal getThucLinh() {
        return thucLinh;
    }

    public void setThucLinh(BigDecimal thucLinh) {
        this.thucLinh = thucLinh;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDate getNgayChot() {
        return ngayChot;
    }

    public void setNgayChot(LocalDate ngayChot) {
        this.ngayChot = ngayChot;
    }

    public String getTinhTrangThanToan() {
        return tinhTrangThanToan;
    }

    public void setTinhTrangThanToan(String tinhTrangThanToan) {
        this.tinhTrangThanToan = tinhTrangThanToan;
    }

    public BigDecimal getHesotrinhdo() {
        return hesotrinhdo;
    }

    public void setHesotrinhdo(BigDecimal hesotrinhdo) {
        this.hesotrinhdo = hesotrinhdo;
    }
}
=======
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
    public String tinhTrangThanToan;
    public BigDecimal hesotrinhdo;  // Hệ số trình độ từ bảng trinhdo
    public BigDecimal phucapChucVu;  // Phụ cấp chức vụ từ bảng chucvu

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

    public String getMaLuong() {
        return maLuong;
    }

    public void setMaLuong(String maLuong) {
        this.maLuong = maLuong;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getPhongBan() {
        return phongBan;
    }

    public void setPhongBan(String phongBan) {
        this.phongBan = phongBan;
    }

    public int getThang() {
        return thang;
    }

    public void setThang(int thang) {
        this.thang = thang;
    }

    public int getNam() {
        return nam;
    }

    public void setNam(int nam) {
        this.nam = nam;
    }

    public BigDecimal getLuongCoBan() {
        return luongCoBan;
    }

    public void setLuongCoBan(BigDecimal luongCoBan) {
        this.luongCoBan = luongCoBan;
    }

    public float getSoNgayCong() {
        return soNgayCong;
    }

    public void setSoNgayCong(float soNgayCong) {
        this.soNgayCong = soNgayCong;
    }

    public BigDecimal getTongPhucap() {
        return tongPhucap;
    }

    public void setTongPhucap(BigDecimal tongPhucap) {
        this.tongPhucap = tongPhucap;
    }

    public BigDecimal getTongKhauTru() {
        return tongKhauTru;
    }

    public void setTongKhauTru(BigDecimal tongKhauTru) {
        this.tongKhauTru = tongKhauTru;
    }

    public BigDecimal getThucLinh() {
        return thucLinh;
    }

    public void setThucLinh(BigDecimal thucLinh) {
        this.thucLinh = thucLinh;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDate getNgayChot() {
        return ngayChot;
    }

    public void setNgayChot(LocalDate ngayChot) {
        this.ngayChot = ngayChot;
    }

    public String getTinhTrangThanToan() {
        return tinhTrangThanToan;
    }

    public void setTinhTrangThanToan(String tinhTrangThanToan) {
        this.tinhTrangThanToan = tinhTrangThanToan;
    }

    public BigDecimal getHesotrinhdo() {
        return hesotrinhdo;
    }

    public void setHesotrinhdo(BigDecimal hesotrinhdo) {
        this.hesotrinhdo = hesotrinhdo;
    }
}
>>>>>>> 1cfb281dfcc017364337a4c66ee412a5e8d7de17
