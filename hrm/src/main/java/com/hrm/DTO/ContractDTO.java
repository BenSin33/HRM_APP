package com.hrm.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ContractDTO {
    public String maHopDong;
    public String maNV;
    public String hoTen;
    public String phongBan;
    public String loaiHopDong;
    public LocalDate ngayLamHopDong;
    public LocalDate hanHopDong;
    public BigDecimal luongCoBan;
    public String trangThai;

    public ContractDTO() {}

    public ContractDTO(String maHopDong, String maNV, String hoTen, String phongBan,
                       String loaiHopDong, LocalDate ngayLamHopDong, LocalDate hanHopDong,
                       BigDecimal luongCoBan) {
        this.maHopDong = maHopDong;
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.phongBan = phongBan;
        this.loaiHopDong = loaiHopDong;
        this.ngayLamHopDong = ngayLamHopDong;
        this.hanHopDong = hanHopDong;
        this.luongCoBan = luongCoBan;
        this.trangThai = calculateStatus(hanHopDong);
    }

    private String calculateStatus(LocalDate hanHopDong) {
        if (hanHopDong == null) return "Không xác định";
        LocalDate now = LocalDate.now();
        if (hanHopDong.isBefore(now)) {
            return "Hết hạn";
        } else if (hanHopDong.isBefore(now.plusMonths(3))) {
            return "Sắp hết hạn";
        } else {
            return "Đang hiệu lực";
        }
    }
}
