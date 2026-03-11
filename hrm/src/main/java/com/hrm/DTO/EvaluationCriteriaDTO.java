package com.hrm.DTO;

/**
 * DTO cho tiêu chí đánh giá (bảng tieuchidanhgia)
 */
public class EvaluationCriteriaDTO {
    private String maTieuChi;
    private String tenTieuChi;
    private int diem;

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

    public int getDiem() {
        return diem;
    }

    public void setDiem(int diem) {
        this.diem = diem;
    }
}
