package com.hrm.DTO.Manager;
import java.time.LocalDate;

public class NghiPhepDTO {
    private String manghiphep;      // Mã đơn nghỉ phép
    private String manv;             // Mã nhân viên
    private String loainghi;         // Có lương / Không lương
    private String lydonghi;         // Lý do nghỉ
    private LocalDate ngaynghi;      // Ngày nghỉ
    private LocalDate ngaylamlai;    // Ngày làm lại
    private String nguoiduyet;       // Người duyệt
    private LocalDate ngayduyet;     // Ngày duyệt
    private String trangthai;        // Chờ duyệt / Đã duyệt / Từ chối
    private String lydotuchoi;       // Lý do từ chối
    
    // Thông tin JOIN từ bảng nhanvien
    private String tennv;
    private String tenphongban;
    private String tenchucvu;
    
    // Constructor, Getter, Setter
    public NghiPhepDTO() {}
    
    public NghiPhepDTO(String manghiphep, String manv, String loainghi, 
                       LocalDate ngaynghi, LocalDate ngaylamlai, 
                       String lydonghi, String nguoiduyet, LocalDate ngayduyet) {
        this.manghiphep = manghiphep;
        this.manv = manv;
        this.loainghi = loainghi;
        this.ngaynghi = ngaynghi;
        this.ngaylamlai = ngaylamlai;
        this.lydonghi = lydonghi;
        this.nguoiduyet = nguoiduyet;
        this.ngayduyet = ngayduyet;
    }
    
    // Getters & Setters
    public String getManghiphep() { return manghiphep; }
    public void setManghiphep(String manghiphep) { this.manghiphep = manghiphep; }
    
    public String getManv() { return manv; }
    public void setManv(String manv) { this.manv = manv; }
    
    public String getLoainghi() { return loainghi; }
    public void setLoainghi(String loainghi) { this.loainghi = loainghi; }
    
    public String getLydonghi() { return lydonghi; }
    public void setLydonghi(String lydonghi) { this.lydonghi = lydonghi; }
    
    public LocalDate getNgaynghi() { return ngaynghi; }
    public void setNgaynghi(LocalDate ngaynghi) { this.ngaynghi = ngaynghi; }
    
    public LocalDate getNgaylamlai() { return ngaylamlai; }
    public void setNgaylamlai(LocalDate ngaylamlai) { this.ngaylamlai = ngaylamlai; }
    
    public String getNguoiduyet() { return nguoiduyet; }
    public void setNguoiduyet(String nguoiduyet) { this.nguoiduyet = nguoiduyet; }
    
    public LocalDate getNgayduyet() { return ngayduyet; }
    public void setNgayduyet(LocalDate ngayduyet) { this.ngayduyet = ngayduyet; }
    
    public String getTennv() { return tennv; }
    public void setTennv(String tennv) { this.tennv = tennv; }
    
    public String getTenphongban() { return tenphongban; }
    public void setTenphongban(String tenphongban) { this.tenphongban = tenphongban; }
    
    public String getTenchucvu() { return tenchucvu; }
    public void setTenchucvu(String tenchucvu) { this.tenchucvu = tenchucvu; }
    
    public String getTrangthai() { return trangthai; }
    public void setTrangthai(String trangthai) { this.trangthai = trangthai; }
    
    public String getLydotuchoi() { return lydotuchoi; }
    public void setLydotuchoi(String lydotuchoi) { this.lydotuchoi = lydotuchoi; }
}
