package com.hrm.DTO.Manager;

public class TieuChiDanhGiaDTO {
    private String maTieuChi;
    private String tenTieuChi;
    private int diemToiDa;

    public TieuChiDanhGiaDTO() {}

    public TieuChiDanhGiaDTO(String maTieuChi, String tenTieuChi, int diemToiDa) {
        this.maTieuChi = maTieuChi;
        this.tenTieuChi = tenTieuChi;
        this.diemToiDa = diemToiDa;
    }

    public String getMaTieuChi() {
        return maTieuChi;
    }

    public void setMaTieuChi(String maTieuChi) {
        this.maTieuChi = maTieuChi;
    }

    public String getTenTieuChi() {
        return tenTieuChi;
    }

    public void setTenTieuChi(String tenTieuChi) {
        this.tenTieuChi = tenTieuChi;
    }

    public int getDiemToiDa() {
        return diemToiDa;
    }

    public void setDiemToiDa(int diemToiDa) {
        this.diemToiDa = diemToiDa;
    }
}

