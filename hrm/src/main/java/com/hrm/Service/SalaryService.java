package com.hrm.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import com.hrm.DAO.Employee.SalaryDAO;
import com.hrm.DAO.Employee.AttendanceDAO;
import com.hrm.DAO.NhanVienDAO;
import com.hrm.DAO.AllowanceDAO;
import com.hrm.DAO.DeductionDAO;
import com.hrm.DAO.ContractDAO;
import com.hrm.DTO.Employee.SalaryDTO;
import com.hrm.DTO.Manager.NhanVienDTO;
import com.hrm.DTO.ContractDTO;

public class SalaryService {
    private SalaryDAO salaryDAO;

    public SalaryService() {
        this.salaryDAO = new SalaryDAO();
    }

    public List<SalaryDTO> getAllSalaries() {
        return salaryDAO.getAllSalaries();
    }

    public List<SalaryDTO> getSalariesByMonthYear(int thang, int nam) {
        return salaryDAO.getSalariesByMonthYear(thang, nam);
    }

    public SalaryDTO getSalaryByMaNV(String maNV, int thang, int nam) {
        return salaryDAO.getSalaryByMaNV(maNV, thang, nam);
    }

    public boolean updateSalary(SalaryDTO salary) {
        return salaryDAO.updateSalary(salary);
    }

    public boolean insertSalary(SalaryDTO salary) {
        return salaryDAO.insertSalary(salary);
    }

    public boolean lockSalariesByMonth(int thang, int nam) {
        return salaryDAO.lockSalariesByMonth(thang, nam);
    }

    public boolean unlockSalariesByMonth(int thang, int nam) {
        return salaryDAO.unlockSalariesByMonth(thang, nam);
    }

    public boolean updatePaymentStatusByMonth(int thang, int nam) {
        return salaryDAO.updatePaymentStatusByMonth(thang, nam);
    }

    public List<SalaryDTO> getSalariesByMonthYearAndStatus(int thang, int nam, String trangThai) {
        return salaryDAO.getSalariesByMonthYearAndStatus(thang, nam, trangThai);
    }

    public List<SalaryDTO> searchSalaries(int thang, int nam, String keyword) {
        return salaryDAO.searchSalaries(thang, nam, keyword);
    }
    
    public List<SalaryDTO> getSalariesByDateRange(int fromMonth, int fromYear, int toMonth, int toYear) {
        return salaryDAO.getSalariesByDateRange(fromMonth, fromYear, toMonth, toYear);
    }

    public SalaryStatistics getSalaryStatistics(int thang, int nam) {
        List<SalaryDTO> salaries = getSalariesByMonthYear(thang, nam);
        
        BigDecimal totalSalary = BigDecimal.ZERO;
        BigDecimal averageSalary = BigDecimal.ZERO;
        int employeeCount = salaries.size();

        for (SalaryDTO salary : salaries) {
            if (salary.thucLinh != null) {
                totalSalary = totalSalary.add(salary.thucLinh);
            }
        }

        if (employeeCount > 0) {
            averageSalary = totalSalary.divide(new BigDecimal(employeeCount), RoundingMode.HALF_UP);
        }

        return new SalaryStatistics(totalSalary, averageSalary, employeeCount);
    }

    // Tính lương cho khoảng tháng/năm
    // Công thức: thực lĩnh = (lương cơ bản x hệ số trình độ x (số ngày công / số ngày công chuẩn)) + (Tổng phụ cấp + Phụ cấp chức vụ) - tổng khấu trừ
    public boolean calculateSalaryForMonthRange(int fromMonth, int fromYear, int toMonth, int toYear) {
        try {
            AttendanceDAO attendanceDAO = new AttendanceDAO();
            NhanVienDAO nhanVienDAO = new NhanVienDAO();
            AllowanceDAO allowanceDAO = new AllowanceDAO();
            DeductionDAO deductionDAO = new DeductionDAO();
            ContractDAO contractDAO = new ContractDAO();
            
            // Lấy tất cả nhân viên
            List<NhanVienDTO> employees = nhanVienDAO.getAll();
            
            // Lấy allowances và deductions mặc định cho nhân viên
            BigDecimal defaultAllowances = allowanceDAO.getTotalAllowances();
            BigDecimal defaultDeductions = deductionDAO.getTotalDeductions();
            
            int successCount = 0;
            
            // Số ngày công chuẩn (công ty quy định) - mặc định 26 ngày
            float soNgayCongChuan = 26;
            
            // Duyệt qua từng nhân viên và tính lương cho từng tháng
            for (NhanVienDTO employee : employees) {
                // Lấy hợp đồng hiện hành của nhân viên (lấy hợp đồng mới nhất)
                List<ContractDTO> contracts = contractDAO.getContractsByMaNV(employee.getManv());
                if (contracts.isEmpty()) {
                    continue; // Bỏ qua nhân viên không có hợp đồng
                }
                
                // Lấy hợp đồng mới nhất (đã sắp xếp theo thời gian giảm dần)
                ContractDTO currentContract = contracts.get(0);
                BigDecimal luongCoBanFromContract = currentContract.luongCoBan != null 
                    ? currentContract.luongCoBan : BigDecimal.ZERO;
                
                // Lấy phụ cấp chức vụ từ chucvu table dựa trên mã chức vụ của nhân viên
                BigDecimal phucapChucVuFromDB = getPhucapChucVuForEmployee(employee.getManv());
                
                java.time.YearMonth current = java.time.YearMonth.of(fromYear, fromMonth);
                java.time.YearMonth to = java.time.YearMonth.of(toYear, toMonth);
                
                while (!current.isAfter(to)) {
                    // Kiểm tra xem bản lương đã tồn tại chưa
                    SalaryDTO existingSalary = getSalaryByMaNV(employee.getManv(), current.getMonthValue(), current.getYear());
                    
                    // Chỉ tính nếu chưa có bản lương hoặc trạng thái là nháp (0)
                    if (existingSalary == null || "0".equals(existingSalary.trangThai)) {
                        // Lấy dữ liệu nhân viên (lương cơ bản từ hợp đồng, hệ số trình độ)
                        BigDecimal luongCoBan = luongCoBanFromContract; // Lấy từ hợp đồng
                        BigDecimal hesoTrinhDo = existingSalary != null && existingSalary.hesotrinhdo != null 
                            ? existingSalary.hesotrinhdo : new BigDecimal("1.00");
                        
                        // Lấy số ngày công từ bảng chấm công (chỉ tính những ngày TRANGTHAI = '1')
                        float soNgayCong = attendanceDAO.getWorkingDaysForEmployeeInMonth(
                            employee.getManv(), 
                            current.getMonthValue(), 
                            current.getYear()
                        );
                        
                        // Tính toán thực lĩnh theo công thức:
                        // (lương cơ bản * hệ số trình độ * (số ngày công / số ngày công chuẩn)) + (Tổng phụ cấp + Phụ cấp chức vụ) - tổng khấu trừ
                        BigDecimal ngayCongRatio = new BigDecimal(soNgayCong)
                            .divide(new BigDecimal(soNgayCongChuan), 4, RoundingMode.HALF_UP);
                        
                        BigDecimal tongPhucap = defaultAllowances.add(phucapChucVuFromDB);
                        
                        BigDecimal thucLinh = luongCoBan
                            .multiply(hesoTrinhDo)
                            .multiply(ngayCongRatio)
                            .add(tongPhucap)
                            .subtract(defaultDeductions)
                            .setScale(2, RoundingMode.HALF_UP);
                        
                        // Tạo hoặc cập nhật bản lương
                        SalaryDTO salaryDTO = new SalaryDTO();
                        if (existingSalary != null) {
                            salaryDTO.maLuong = existingSalary.maLuong;
                        }
                        salaryDTO.maNV = employee.getManv();
                        salaryDTO.hoTen = employee.getHoten();
                        salaryDTO.phongBan = employee.getMaphongban(); // Mã phòng ban
                        salaryDTO.thang = current.getMonthValue();
                        salaryDTO.nam = current.getYear();
                        salaryDTO.luongCoBan = luongCoBan;
                        salaryDTO.soNgayCong = soNgayCong;
                        salaryDTO.soNgayCongChuan = soNgayCongChuan; // Lưu số ngày công chuẩn
                        salaryDTO.hesotrinhdo = hesoTrinhDo;
                        salaryDTO.phucapChucVu = phucapChucVuFromDB;
                        salaryDTO.tongPhucap = tongPhucap;  // ← FIX: Include phụ cấp chức vụ
                        salaryDTO.tongKhauTru = defaultDeductions;
                        salaryDTO.thucLinh = thucLinh;
                        salaryDTO.trangThai = "0"; // Nháp
                        salaryDTO.tinhTrangThanToan = "Chưa thanh toán";
                        
                        // INSERT nếu chưa tồn tại, UPDATE nếu đã tồn tại
                        boolean success = false;
                        if (existingSalary == null) {
                            // Tạo mới - INSERT
                            success = insertSalary(salaryDTO);
                        } else {
                            // Cập nhật - UPDATE
                            success = updateSalary(salaryDTO);
                        }
                        
                        if (success) {
                            successCount++;
                        }
                    }
                    
                    current = current.plusMonths(1);
                }
            }
            
            return successCount > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Helper method: Get phụ cấp chức vụ for an employee
    private BigDecimal getPhucapChucVuForEmployee(String maNV) {
        try {
            java.sql.Connection conn = com.hrm.utils.JDBCConection.getConnection();
            String sql = "SELECT cv.PHUCAPCHUCVU FROM nhanvien nv " +
                         "JOIN chucvu cv ON nv.MACHUCVU = cv.MACHUCVU " +
                         "WHERE nv.MANV = ?";
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, maNV);
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                BigDecimal phucap = rs.getBigDecimal("PHUCAPCHUCVU");
                rs.close();
                ps.close();
                conn.close();
                return phucap != null ? phucap : BigDecimal.ZERO;
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    public static class SalaryStatistics {
        public BigDecimal totalSalary;
        public BigDecimal averageSalary;
        public int employeeCount;

        public SalaryStatistics(BigDecimal totalSalary, BigDecimal averageSalary, int employeeCount) {
            this.totalSalary = totalSalary;
            this.averageSalary = averageSalary;
            this.employeeCount = employeeCount;
        }
    }
}
