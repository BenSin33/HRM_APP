package com.hrm.DTO.Employee;

import java.sql.Date;

public class EvaluationHistoryDTO {
    private String maDot;
    private int tongDiem;
    private String nhanXet;
    private String quyetDinh;
    private Date ngayDanhGia;

    public EvaluationHistoryDTO() {
    }

    public EvaluationHistoryDTO(String maDot, int tongDiem, String nhanXet, String quyetDinh, Date ngayDanhGia) {
        this.maDot = maDot;
        this.tongDiem = tongDiem;
        this.nhanXet = nhanXet;
        this.quyetDinh = quyetDinh;
        this.ngayDanhGia = ngayDanhGia;
    }

    public String getMaDot() {
        return maDot;
    }

    public void setMaDot(String maDot) {
        this.maDot = maDot;
    }

    public int getTongDiem() {
        return tongDiem;
    }

    public void setTongDiem(int tongDiem) {
        this.tongDiem = tongDiem;
    }

    public String getNhanXet() {
        return nhanXet;
    }

    public void setNhanXet(String nhanXet) {
        this.nhanXet = nhanXet;
    }

    public String getQuyetDinh() {
        return quyetDinh;
    }

    public void setQuyetDinh(String quyetDinh) {
        this.quyetDinh = quyetDinh;
    }

    public Date getNgayDanhGia() {
        return ngayDanhGia;
    }

    public void setNgayDanhGia(Date ngayDanhGia) {
        this.ngayDanhGia = ngayDanhGia;
    }
}
