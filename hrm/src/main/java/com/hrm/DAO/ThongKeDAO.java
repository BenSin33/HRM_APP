package com.hrm.DAO;

import com.hrm.DTO.ContractDTO;
import com.hrm.utils.JDBCConection;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ThongKeDAO {
    
    // 1. Thống kê nhân viên theo phòng ban (Biểu đồ tròn)
    public Map<String, Integer> getEmployeeCountByDepartment() {
        Map<String, Integer> data = new HashMap<>();
        String sql = "SELECT pb.TENPHONGBAN, COUNT(nv.MANV) as SL " +
                     "FROM phongban pb LEFT JOIN nhanvien nv ON pb.MAPHONGBAN = nv.MAPHONGBAN " +
                     "GROUP BY pb.TENPHONGBAN";
        
        try (Connection conn = JDBCConection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                data.put(rs.getString("TENPHONGBAN"), rs.getInt("SL"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    // 2. Thống kê lương trung bình theo tháng (Biểu đồ đường - 6 tháng gần nhất)
    public Map<String, Double> getAverageSalaryByMonth() {
        Map<String, Double> data = new LinkedHashMap<>();
        String sql = "SELECT CONCAT(THANG, '/', NAM) as MONTH_YEAR, AVG(THUCLINH) as AVG_SALARY, NAM, THANG " +
                     "FROM bangluong " +
                     "GROUP BY NAM, THANG " +
                     "ORDER BY NAM ASC, THANG ASC " +
                     "LIMIT 12";
        
        try (Connection conn = JDBCConection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                data.put(rs.getString("MONTH_YEAR"), rs.getDouble("AVG_SALARY"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    // 3. Thống kê hiệu suất nhân viên (Biểu đồ cột - Top 10 nhân viên)
    public Map<String, Double> getTopEmployeePerformance() {
        Map<String, Double> data = new LinkedHashMap<>();
        String sql = "SELECT nv.HOTEN, AVG(pdg.TONGDIEM) as AVG_SCORE " +
                     "FROM nhanvien nv JOIN phieudanhgia pdg ON nv.MANV = pdg.MANV " +
                     "GROUP BY nv.MANV, nv.HOTEN " +
                     "ORDER BY AVG_SCORE DESC " +
                     "LIMIT 10";
        
        try (Connection conn = JDBCConection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                data.put(rs.getString("HOTEN"), rs.getDouble("AVG_SCORE"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    // 4. Tỉ lệ chênh lệch: Hợp đồng đã kí trong năm vs Hợp đồng hết hạn trong năm
    public Map<String, Integer> getContractDiscrepancy() {
        Map<String, Integer> data = new HashMap<>();
        int currentYear = java.time.LocalDate.now().getYear();
        
        String sqlSigned = "SELECT COUNT(*) FROM hopdong WHERE YEAR(NGAYLAMHOPDONG) = ?";
        String sqlExpired = "SELECT COUNT(*) FROM hopdong WHERE YEAR(HANHOPDONG) = ?";
        
        try (Connection conn = JDBCConection.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sqlSigned)) {
                ps.setInt(1, currentYear);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) data.put("Hợp đồng đã kí trong năm", rs.getInt(1));
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sqlExpired)) {
                ps.setInt(1, currentYear);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) data.put("Hợp đồng hết hạn trong năm", rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    // 5. Tỉ lệ các loại hợp đồng (theo thời hạn)
    public Map<String, Integer> getContractTypeDistribution() {
        Map<String, Integer> data = new LinkedHashMap<>();
        data.put("1 - 2 năm", 0);
        data.put("3 - 5 năm", 0);
        data.put("6 - 8 năm", 0);
        data.put("9 - 10 năm", 0);
        data.put("trên 10 năm", 0);
        
        String sql = "SELECT NGAYLAMHOPDONG, HANHOPDONG FROM hopdong";
        
        try (Connection conn = JDBCConection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                java.sql.Date start = rs.getDate("NGAYLAMHOPDONG");
                java.sql.Date end = rs.getDate("HANHOPDONG");
                if (start != null && end != null) {
                    long diffInMillies = Math.abs(end.getTime() - start.getTime());
                    long years = diffInMillies / (1000L * 60 * 60 * 24 * 365L);
                    
                    if (years >= 1 && years <= 2) data.put("1 - 2 năm", data.get("1 - 2 năm") + 1);
                    else if (years >= 3 && years <= 5) data.put("3 - 5 năm", data.get("3 - 5 năm") + 1);
                    else if (years >= 6 && years <= 8) data.put("6 - 8 năm", data.get("6 - 8 năm") + 1);
                    else if (years >= 9 && years <= 10) data.put("9 - 10 năm", data.get("9 - 10 năm") + 1);
                    else if (years > 10) data.put("trên 10 năm", data.get("trên 10 năm") + 1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    // 6. Danh sách hợp đồng chi tiết
    public List<ContractDTO> getContractDetails(String filter) {
        List<ContractDTO> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT hd.*, nv.HOTEN, pb.TENPHONGBAN " +
            "FROM hopdong hd " +
            "JOIN nhanvien nv ON hd.MANV = nv.MANV " +
            "LEFT JOIN phongban pb ON nv.MAPHONGBAN = pb.MAPHONGBAN " +
            "WHERE 1=1 "
        );
        
        if (filter != null && !filter.equals("Tất cả hợp đồng")) {
            if (filter.equals("Đã ký trong năm")) {
                sql.append("AND YEAR(hd.NGAYLAMHOPDONG) = YEAR(CURDATE()) ");
            } else if (filter.equals("Hết hạn trong năm")) {
                sql.append("AND YEAR(hd.HANHOPDONG) = YEAR(CURDATE()) ");
            }
        }
        
        try (Connection conn = JDBCConection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql.toString())) {
            while (rs.next()) {
                ContractDTO dto = new ContractDTO(
                    rs.getString("MAHOPDONG"),
                    rs.getString("MANV"),
                    rs.getString("HOTEN"),
                    rs.getString("TENPHONGBAN"),
                    rs.getString("LOAIHOPDONG"),
                    rs.getDate("NGAYLAMHOPDONG") != null ? rs.getDate("NGAYLAMHOPDONG").toLocalDate() : null,
                    rs.getDate("HANHOPDONG") != null ? rs.getDate("HANHOPDONG").toLocalDate() : null,
                    rs.getBigDecimal("LUONGCOBAN")
                );
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
