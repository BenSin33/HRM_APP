package com.hrm.DTO.Employee;

import java.sql.Time;
import java.util.Date;

public class AttendanceDTO {
    private String maChamCong;
    private String maNV;
    private Date ngayLamViec;
    private Time checkIn;
    private Time checkOut;
    private String trangThai;
    private double soGioLam;
    private String maCaLam;

    public String getMaChamCong() { return maChamCong; }
    public void setMaChamCong(String maChamCong) { this.maChamCong = maChamCong; }

    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }

    public Date getNgayLamViec() { return ngayLamViec; }
    public void setNgayLamViec(Date ngayLamViec) { this.ngayLamViec = ngayLamViec; }

    public Time getCheckIn() { return checkIn; }
    public void setCheckIn(Time checkIn) { this.checkIn = checkIn; }

    public Time getCheckOut() { return checkOut; }
    public void setCheckOut(Time checkOut) { this.checkOut = checkOut; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public double getSoGioLam() { return soGioLam; }
    public void setSoGioLam(double soGioLam) { this.soGioLam = soGioLam; }

    public String getMaCaLam() { return maCaLam; }
    public void setMaCaLam(String maCaLam) { this.maCaLam = maCaLam; }
}