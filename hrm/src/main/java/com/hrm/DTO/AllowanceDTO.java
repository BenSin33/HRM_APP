package com.hrm.DTO;

import java.math.BigDecimal;

/**
 * DTO cho danh mục phụ cấp (danhmuc_phucap)
 */
public class AllowanceDTO {
    private int maPhucap;
    private String tenPhucap;
    private BigDecimal soTienMacDinh;

    // Constructors
    public AllowanceDTO() {
    }

    public AllowanceDTO(int maPhucap, String tenPhucap, BigDecimal soTienMacDinh) {
        this.maPhucap = maPhucap;
        this.tenPhucap = tenPhucap;
        this.soTienMacDinh = soTienMacDinh;
    }

    // Getters and Setters
    public int getMaPhucap() {
        return maPhucap;
    }

    public void setMaPhucap(int maPhucap) {
        this.maPhucap = maPhucap;
    }

    public String getTenPhucap() {
        return tenPhucap;
    }

    public void setTenPhucap(String tenPhucap) {
        this.tenPhucap = tenPhucap;
    }

    public BigDecimal getSoTienMacDinh() {
        return soTienMacDinh;
    }

    public void setSoTienMacDinh(BigDecimal soTienMacDinh) {
        this.soTienMacDinh = soTienMacDinh;
    }

    @Override
    public String toString() {
        return "AllowanceDTO{" +
                "maPhucap=" + maPhucap +
                ", tenPhucap='" + tenPhucap + '\'' +
                ", soTienMacDinh=" + soTienMacDinh +
                '}';
    }
}
