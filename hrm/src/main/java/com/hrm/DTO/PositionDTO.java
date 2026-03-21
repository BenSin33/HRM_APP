package com.hrm.DTO;

import java.math.BigDecimal;

/**
 * DTO cho chức vụ (bảng chucvu)
 */
public class PositionDTO {
    private String maChucVu;
    private String tenViTri;
    private BigDecimal phuCapChucVu;

    public String getMaChucVu() {
        return maChucVu;
    }

    public void setMaChucVu(String maChucVu) {
        this.maChucVu = maChucVu;
    }

    public String getTenViTri() {
        return tenViTri;
    }

    public void setTenViTri(String tenViTri) {
        this.tenViTri = tenViTri;
    }

    public BigDecimal getPhuCapChucVu() {
        return phuCapChucVu;
    }

    public void setPhuCapChucVu(BigDecimal phuCapChucVu) {
        this.phuCapChucVu = phuCapChucVu;
    }
}
