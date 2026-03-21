package com.hrm.DTO;

import java.math.BigDecimal;

/**
 * DTO cho danh mục khấu trừ (danhmuc_khautru)
 */
public class DeductionDTO {
    private int maKhauTru;
    private String tenKhauTru;
    private BigDecimal soTienMacDinh;

    // Constructors
    public DeductionDTO() {
    }

    public DeductionDTO(int maKhauTru, String tenKhauTru, BigDecimal soTienMacDinh) {
        this.maKhauTru = maKhauTru;
        this.tenKhauTru = tenKhauTru;
        this.soTienMacDinh = soTienMacDinh;
    }

    // Getters and Setters
    public int getMaKhauTru() {
        return maKhauTru;
    }

    public void setMaKhauTru(int maKhauTru) {
        this.maKhauTru = maKhauTru;
    }

    public String getTenKhauTru() {
        return tenKhauTru;
    }

    public void setTenKhauTru(String tenKhauTru) {
        this.tenKhauTru = tenKhauTru;
    }

    public BigDecimal getSoTienMacDinh() {
        return soTienMacDinh;
    }

    public void setSoTienMacDinh(BigDecimal soTienMacDinh) {
        this.soTienMacDinh = soTienMacDinh;
    }

    @Override
    public String toString() {
        return "DeductionDTO{" +
                "maKhauTru=" + maKhauTru +
                ", tenKhauTru='" + tenKhauTru + '\'' +
                ", soTienMacDinh=" + soTienMacDinh +
                '}';
    }
}
