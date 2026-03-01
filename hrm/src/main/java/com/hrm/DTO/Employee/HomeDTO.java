package com.hrm.DTO.Employee;


public class HomeDTO {
    private String hoTen;
    private double luongThangTruoc;
    private double tongGioLam;
    private int diemDanhGia;
    private int soNgayNghiPhep;

    public HomeDTO() {}

    // Constructor đầy đủ
    public HomeDTO(String hoTen, double luongThangTruoc, double tongGioLam, int diemDanhGia, int soNgayNghiPhep) {
        this.hoTen = hoTen;
        this.luongThangTruoc = luongThangTruoc;
        this.tongGioLam = tongGioLam;
        this.diemDanhGia = diemDanhGia;
        this.soNgayNghiPhep = soNgayNghiPhep;
    }

    // Getters và Setters
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public double getLuongThangTruoc() { return luongThangTruoc; }
    public void setLuongThangTruoc(double luong) { this.luongThangTruoc = luong; }

    public double getTongGioLam() { return tongGioLam; }
    public void setTongGioLam(double gio) { this.tongGioLam = gio; }

    public int getDiemDanhGia() { return diemDanhGia; }
    public void setDiemDanhGia(int diem) { this.diemDanhGia = diem; }

    public int getSoNgayNghiPhep() { return soNgayNghiPhep; }
    public void setSoNgayNghiPhep(int nghi) { this.soNgayNghiPhep = nghi; }
}
