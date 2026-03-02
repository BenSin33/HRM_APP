package com.hrm.DTO.Employee;

public class HomeReportDTO {
    // Hoạt động gần đây (Bên trái)
    private String activityLuong;      // Bảng lương + Trạng thái
    private String activityChamCong;   // Check-in gần nhất
    private String activityNghiPhep;   // Đơn nghỉ phép + Trạng thái duyệt

    // Lịch sắp tới (Bên phải)
    private String scheduleLichLam;    // Lịch làm việc + Ghi chú
    private String scheduleDanhGia;    // Đánh giá gần nhất
    private String scheduleCapNhatLich; // Ngày cập nhật lịch cuối cùng

    // Getters và Setters
    public String getActivityLuong() { return activityLuong; }
    public void setActivityLuong(String activityLuong) { this.activityLuong = activityLuong; }
    public String getActivityChamCong() { return activityChamCong; }
    public void setActivityChamCong(String activityChamCong) { this.activityChamCong = activityChamCong; }
    public String getActivityNghiPhep() { return activityNghiPhep; }
    public void setActivityNghiPhep(String activityNghiPhep) { this.activityNghiPhep = activityNghiPhep; }
    public String getScheduleLichLam() { return scheduleLichLam; }
    public void setScheduleLichLam(String scheduleLichLam) { this.scheduleLichLam = scheduleLichLam; }
    public String getScheduleDanhGia() { return scheduleDanhGia; }
    public void setScheduleDanhGia(String scheduleDanhGia) { this.scheduleDanhGia = scheduleDanhGia; }
    public String getScheduleCapNhatLich() { return scheduleCapNhatLich; }
    public void setScheduleCapNhatLich(String scheduleCapNhatLich) { this.scheduleCapNhatLich = scheduleCapNhatLich; }
}