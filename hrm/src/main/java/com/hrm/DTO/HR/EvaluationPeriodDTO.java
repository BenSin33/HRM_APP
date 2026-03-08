package com.hrm.DTO.HR;

public class EvaluationPeriodDTO {

    private String maDot;
    private String tenDot;
    private String kyKy;
    private int    nam;
    private String nguoiDanhGia;
    private String trangThai;

    public String getMaDot()                { return maDot; }
    public void   setMaDot(String v)        { this.maDot = v; }

    public String getTenDot()               { return tenDot; }
    public void   setTenDot(String v)       { this.tenDot = v; }

    public String getKyKy()                 { return kyKy; }
    public void   setKyKy(String v)         { this.kyKy = v; }

    public int    getNam()                  { return nam; }
    public void   setNam(int v)             { this.nam = v; }

    public String getNguoiDanhGia()         { return nguoiDanhGia; }
    public void   setNguoiDanhGia(String v) { this.nguoiDanhGia = v; }

    public String getTrangThai()            { return trangThai; }
    public void   setTrangThai(String v)    { this.trangThai = v; }

    /** Label hiển thị trên ComboBox: "Q4 2024" */
    public String getLabel() {
        return kyKy + " " + nam;
    }

    @Override
    public String toString() {
        return getLabel();
    }
}