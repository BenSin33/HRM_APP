
package com.hrm.DTO.HR;

import java.util.Date;

public class EvaluationDTO {

    private String maPhieu;
    private String maNV;
    private String maDot;
    private String maTieuChi;
    private String hoTen;
    private String chucVu;
    private String phongBan;
    private String nguoiDanhGia;
    private int    tongDiem;
    private String xepLoai;
    private String nhanXet;
    private String quyetDinh;
    private String loaiQuyetDinh;
    private String trangThaiDuyet;
    private Date   ngayDanhGia;
    private String tenTieuChi;

    public String getMaPhieu()              { return maPhieu; }
    public void   setMaPhieu(String v)      { this.maPhieu = v; }

    public String getMaNV()                 { return maNV; }
    public void   setMaNV(String v)         { this.maNV = v; }

    public String getMaDot()                { return maDot; }
    public void   setMaDot(String v)        { this.maDot = v; }

    public String getMaTieuChi()            { return maTieuChi; }
    public void   setMaTieuChi(String v)    { this.maTieuChi = v; }

    public String getHoTen()                { return hoTen; }
    public void   setHoTen(String v)        { this.hoTen = v; }

    public String getChucVu()               { return chucVu; }
    public void   setChucVu(String v)       { this.chucVu = v; }

    public String getPhongBan()             { return phongBan; }
    public void   setPhongBan(String v)     { this.phongBan = v; }

    public String getNguoiDanhGia()         { return nguoiDanhGia; }
    public void   setNguoiDanhGia(String v) { this.nguoiDanhGia = v; }

    public int    getTongDiem()             { return tongDiem; }
    public void   setTongDiem(int v)        { this.tongDiem = v; }

    public String getXepLoai()              { return xepLoai; }
    public void   setXepLoai(String v)      { this.xepLoai = v; }

    public String getNhanXet()              { return nhanXet; }
    public void   setNhanXet(String v)      { this.nhanXet = v; }

    public String getQuyetDinh()            { return quyetDinh; }
    public void   setQuyetDinh(String v)    { this.quyetDinh = v; }

    public String getLoaiQuyetDinh()        { return loaiQuyetDinh; }
    public void   setLoaiQuyetDinh(String v){ this.loaiQuyetDinh = v; }

    public String getTrangThaiDuyet()       { return trangThaiDuyet; }
    public void   setTrangThaiDuyet(String v){ this.trangThaiDuyet = v; }

    public Date   getNgayDanhGia()          { return ngayDanhGia; }
    public void   setNgayDanhGia(Date v)    { this.ngayDanhGia = v; }

    public String getTenTieuChi()           { return tenTieuChi; }
    public void   setTenTieuChi(String v)   { this.tenTieuChi = v; }

    /**
     * Chuyển thành Object[] để đổ vào DefaultTableModel của EvaluationTable.
     * Thứ tự: MÃ NV | NHÂN VIÊN | NGƯỜI ĐÁNH GIÁ | ĐIỂM SỐ | XẾP LOẠI | THƯỞNG/PHẠT | TRẠNG THÁI
     */
    public Object[] toTableRow() {
        return new Object[]{
            maNV,
            new String[]{ hoTen, chucVu, phongBan },
            nguoiDanhGia,
            new Object[]{ tongDiem, xepLoai },
            xepLoai,
            new String[]{ loaiQuyetDinh != null ? loaiQuyetDinh : "Không có", xepLoai },
            trangThaiDuyet != null ? trangThaiDuyet : "-"
        };
    }
}