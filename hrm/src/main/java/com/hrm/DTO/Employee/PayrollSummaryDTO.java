package com.hrm.DTO.Employee;

public class PayrollSummaryDTO {
    private String employeeId;
    private String employeeName;
    private double basicSalary;
    private double bonus;
    private double deduction;
    private double netSalary;
    private String payPeriod;

    public PayrollSummaryDTO(String employeeId, String employeeName, double basicSalary, double bonus, double deduction, double netSalary, String payPeriod) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.basicSalary = basicSalary;
        this.bonus = bonus;
        this.deduction = deduction;
        this.netSalary = netSalary;
        this.payPeriod = payPeriod;
    }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(double basicSalary) { this.basicSalary = basicSalary; }
    public double getBonus() { return bonus; }
    public void setBonus(double bonus) { this.bonus = bonus; }
    public double getDeduction() { return deduction; }
    public void setDeduction(double deduction) { this.deduction = deduction; }
    public double getNetSalary() { return netSalary; }
    public void setNetSalary(double netSalary) { this.netSalary = netSalary; }
    public String getPayPeriod() { return payPeriod; }
    public void setPayPeriod(String payPeriod) { this.payPeriod = payPeriod; }
}
