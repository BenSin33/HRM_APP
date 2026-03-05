package com.hrm.DTO.Manager;
import java.time.LocalDate;

public class NhanVienDTO {
    private String manv;
    private String maphongban;
    private String machucvu;
    private String matrinhdo;
    private String hoten;
    private String gioitinh;
    private String diachi;
    private String dienthoai;
    private String email;
    private LocalDate ngayvaolam;
    private int songayphep;
    private String trangthai;
   

    // Constructor không tham số
    public NhanVienDTO() {
    }

    // Constructor đầy đủ tham số
    public NhanVienDTO(String manv, String maphongban, String machucvu, String matrinhdo,
                   String hoten, String gioitinh, String diachi, String dienthoai,
                   String email, LocalDate ngayvaolam, int songayphep, String trangthai) {
    this.manv = manv;
    this.maphongban = maphongban;
    this.machucvu = machucvu;
    this.matrinhdo = matrinhdo;
    this.hoten = hoten;
    this.gioitinh = gioitinh;
    this.diachi = diachi;
    this.dienthoai = dienthoai;
    this.email = email;
    this.ngayvaolam = ngayvaolam;
    this.songayphep = songayphep;
    this.trangthai = trangthai;
}

    // Getter và Setter
    public String getManv() {
        return manv;
    }

    public void setManv(String manv) {
        this.manv = manv;
    }

    public String getMaphongban() {
        return maphongban;
    }

    public void setMaphongban(String maphongban) {
        this.maphongban = maphongban;
    }

    public String getMachucvu() {
        return machucvu;
    }

    public void setMachucvu(String machucvu) {
        this.machucvu = machucvu;
    }

    public String getMatrinhdo() {
        return matrinhdo;
    }

    public void setMatrinhdo(String matrinhdo) {
        this.matrinhdo = matrinhdo;
    }

    public String getHoten() {
        return hoten;
    }

    public void setHoten(String hoten) {
        this.hoten = hoten;
    }

    public String getGioitinh() {
        return gioitinh;
    }

    public void setGioitinh(String gioitinh) {
        this.gioitinh = gioitinh;
    }

    public String getDiachi() {
        return diachi;
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
    }

    public String getDienthoai() {
        return dienthoai;
    }

    public void setDienthoai(String dienthoai) {
        this.dienthoai = dienthoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
    }

    public LocalDate getNgayvaolam() {
        return ngayvaolam;
    }

    public void setNgayvaolam(LocalDate ngayvaolam) {
        this.ngayvaolam = ngayvaolam;
    }

    public int getSongayphep() {
        return songayphep;
    }

    public void setSongayphep(int songayphep) {
        this.songayphep = songayphep;
    }
}
