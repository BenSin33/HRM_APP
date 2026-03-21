package com.hrm.DTO.Employee;

import java.util.Date;

public class ScheduleDTO {
    private Date date;
    private String shift; // mã ca
    private String shiftName; // tên ca
    private String startTime; // giờ vào ca
    private String endTime; // giờ tan ca
    private String description;

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public String getShift() {
        return shift;
    }
    public void setShift(String shift) {
        this.shift = shift;
    }
    public String getShiftName() {
        return shiftName;
    }
    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }
    public String getStartTime() {
        return startTime;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public String getEndTime() {
        return endTime;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
