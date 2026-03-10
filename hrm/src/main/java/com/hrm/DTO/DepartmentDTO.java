package com.hrm.DTO;

/**
 * DTO cho Phòng ban (Department)
 */
public class DepartmentDTO {
    private String maphongban;
    private String tenphongban;
    private String mota;

    // Thông tin trưởng phòng
    private String truongPhong;
    private String dienThoai;
    private String viTri;
    private String email;

    // Số nhân viên
    private int soNhanVien;

    public DepartmentDTO() {}

    public DepartmentDTO(String maphongban, String tenphongban, String mota) {
        this.maphongban = maphongban;
        this.tenphongban = tenphongban;
        this.mota = mota;
    }

    public DepartmentDTO(String maphongban, String tenphongban, String mota, String truongPhong, String dienThoai, String viTri, String email) {
        this.maphongban = maphongban;
        this.tenphongban = tenphongban;
        this.mota = mota;
        this.truongPhong = truongPhong;
        this.dienThoai = dienThoai;
        this.viTri = viTri;
        this.email = email;
    }

    public DepartmentDTO(String maphongban, String tenphongban, int soNhanVien) {
        this.maphongban = maphongban;
        this.tenphongban = tenphongban;
        this.soNhanVien = soNhanVien;
    }

    public String getMaphongban() {
        return maphongban;
    }

    public void setMaphongban(String maphongban) {
        this.maphongban = maphongban;
    }

    public String getTenphongban() {
        return tenphongban;
    }

    public void setTenphongban(String tenphongban) {
        this.tenphongban = tenphongban;
    }

    public String getMota() {
        return mota;
    }

    public void setMota(String mota) {
        this.mota = mota;
    }

    public String getTruongPhong() {
        return truongPhong;
    }

    public void setTruongPhong(String truongPhong) {
        this.truongPhong = truongPhong;
    }

    public String getDienThoai() {
        return dienThoai;
    }

    public void setDienThoai(String dienThoai) {
        this.dienThoai = dienThoai;
    }

    public String getViTri() {
        return viTri;
    }

    public void setViTri(String viTri) {
        this.viTri = viTri;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getSoNhanVien() {
        return soNhanVien;
    }

    public void setSoNhanVien(int soNhanVien) {
        this.soNhanVien = soNhanVien;
    }
}
