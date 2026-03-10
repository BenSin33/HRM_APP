package com.hrm.DTO.HR;

import java.math.BigDecimal;

/**
 * Chứa một số số liệu tổng hợp xuất hiện trên màn hình tổng quan/bảng điều khiển HR.
 */
public class OverviewDTO {
    private int totalEmployees;
    private int workingEmployees;
    private int onLeaveToday;
    private BigDecimal totalSalaryThisMonth;

    public OverviewDTO() {
        this.totalSalaryThisMonth = BigDecimal.ZERO;
    }

    public OverviewDTO(int totalEmployees, int workingEmployees, int onLeaveToday, BigDecimal totalSalaryThisMonth) {
        this.totalEmployees = totalEmployees;
        this.workingEmployees = workingEmployees;
        this.onLeaveToday = onLeaveToday;
        this.totalSalaryThisMonth = totalSalaryThisMonth == null ? BigDecimal.ZERO : totalSalaryThisMonth;
    }

    public int getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(int totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public int getWorkingEmployees() {
        return workingEmployees;
    }

    public void setWorkingEmployees(int workingEmployees) {
        this.workingEmployees = workingEmployees;
    }

    public int getOnLeaveToday() {
        return onLeaveToday;
    }

    public void setOnLeaveToday(int onLeaveToday) {
        this.onLeaveToday = onLeaveToday;
    }

    public BigDecimal getTotalSalaryThisMonth() {
        return totalSalaryThisMonth;
    }

    public void setTotalSalaryThisMonth(BigDecimal totalSalaryThisMonth) {
        this.totalSalaryThisMonth = totalSalaryThisMonth;
    }
}
