package com.hrm.DAO.HR;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.hrm.DTO.HR.DepartmentDTO;
import com.hrm.utils.JDBCConection;

/**
 * Data access object for the `phongban` table.
 */
public class DepartmentDAO {

    public List<DepartmentDTO> getAll() {
        List<DepartmentDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM phongban";
        Connection conn = JDBCConection.getConnection();
        if (conn == null) {
            System.err.println("Lỗi: Không thể kết nối database trong DepartmentDAO.getAll()");
            return list;
        }
        try (conn;
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                DepartmentDTO dto = new DepartmentDTO(
                        rs.getString("maphongban"),
                        rs.getString("tenphongban")
                );
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Sinh mã phòng ban tiếp theo (PB01, PB02, ...).
     */
    public String generateNextMaPhongBan() {
        String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(maphongban, 3) AS UNSIGNED)), 0) AS MAX_ID FROM phongban WHERE maphongban LIKE 'PB%'";
        Connection conn = JDBCConection.getConnection();
        if (conn == null) return "PB01";
        try (conn;
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int next = rs.getInt("MAX_ID") + 1;
                return String.format("PB%02d", next);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi DepartmentDAO.generateNextMaPhongBan(): " + e.getMessage());
        }
        return "PB01";
    }

    public DepartmentDTO findById(String maPhongBan) {
        String sql = "SELECT * FROM phongban WHERE maphongban = ?";
        Connection conn = JDBCConection.getConnection();
        if (conn == null) {
            System.err.println("Lỗi: Không thể kết nối database trong DepartmentDAO.findById()");
            return null;
        }
        try (conn;
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhongBan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new DepartmentDTO(
                            rs.getString("maphongban"),
                            rs.getString("tenphongban")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void add(DepartmentDTO dto) {
        String sql = "INSERT INTO phongban (maphongban, tenphongban) VALUES (?,?)";
        Connection conn = JDBCConection.getConnection();
        if (conn == null) {
            System.err.println("Lỗi: Không thể kết nối database trong DepartmentDAO.add()");
            return;
        }
        try (conn;
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dto.getMaPhongBan());
            ps.setString(2, dto.getTenPhongBan());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(DepartmentDTO dto) {
        String sql = "UPDATE phongban SET tenphongban = ? WHERE maphongban = ?";
        Connection conn = JDBCConection.getConnection();
        if (conn == null) {
            System.err.println("Lỗi: Không thể kết nối database trong DepartmentDAO.update()");
            return;
        }
        try (conn;
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dto.getTenPhongBan());
            ps.setString(2, dto.getMaPhongBan());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String maPhongBan) {
        String sql = "DELETE FROM phongban WHERE maphongban = ?";
        Connection conn = JDBCConection.getConnection();
        if (conn == null) {
            System.err.println("Lỗi: Không thể kết nối database trong DepartmentDAO.delete()");
            return;
        }
        try (conn;
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhongBan);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lấy danh sách nhân viên trong phòng ban.
     */
    public List<EmployeeOption> getEmployeesInDepartment(String maPhongBan) {
        List<EmployeeOption> list = new ArrayList<>();
        String sql = "SELECT manv, hoten, email, dienthoai FROM nhanvien WHERE maphongban = ? ORDER BY hoten";
        Connection conn = JDBCConection.getConnection();
        if (conn == null) return list;
        try (conn;
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhongBan);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new EmployeeOption(
                            rs.getString("manv"),
                            rs.getString("hoten"),
                            rs.getString("email"),
                            rs.getString("dienthoai")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Đặt nhân viên làm trưởng phòng (CV01). Các nhân viên khác trong phòng chuyển thành CV02.
     */
    public void setDepartmentHead(String maPhongBan, String manv) {
        Connection conn = JDBCConection.getConnection();
        if (conn == null) return;
        try (conn) {
            try (PreparedStatement ps1 = conn.prepareStatement("UPDATE nhanvien SET machucvu = 'CV02' WHERE maphongban = ?")) {
                ps1.setString(1, maPhongBan);
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = conn.prepareStatement("UPDATE nhanvien SET machucvu = 'CV01' WHERE manv = ? AND maphongban = ?")) {
                ps2.setString(1, manv);
                ps2.setString(2, maPhongBan);
                ps2.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cập nhật email và điện thoại của nhân viên (trưởng phòng).
     */
    public void updateEmployeeContact(String manv, String email, String dienthoai) {
        String sql = "UPDATE nhanvien SET email = ?, dienthoai = ? WHERE manv = ?";
        Connection conn = JDBCConection.getConnection();
        if (conn == null) return;
        try (conn;
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email != null ? email : "");
            ps.setString(2, dienthoai != null ? dienthoai : "");
            ps.setString(3, manv);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static class EmployeeOption {
        public final String manv;
        public final String hoten;
        public final String email;
        public final String dienthoai;

        public EmployeeOption(String manv, String hoten, String email, String dienthoai) {
            this.manv = manv != null ? manv : "";
            this.hoten = hoten != null ? hoten : "";
            this.email = email != null ? email : "";
            this.dienthoai = dienthoai != null ? dienthoai : "";
        }

        @Override
        public String toString() {
            return hoten;
        }
    }

    /**
     * Utility that counts number of employees in a given department.
     */
    public int countEmployees(String maPhongBan) {
        String sql = "SELECT COUNT(*) FROM nhanvien WHERE maphongban = ?";
        Connection conn = JDBCConection.getConnection();
        if (conn == null) {
            System.err.println("Lỗi: Không thể kết nối database trong DepartmentDAO.countEmployees()");
            return 0;
        }
        try (conn;
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhongBan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get all departments with employee count.
     */
    public List<DepartmentDTO> getAllWithEmployeeCount() {
        List<DepartmentDTO> list = new ArrayList<>();
        String sql = "SELECT pb.maphongban, pb.tenphongban, COUNT(nv.manv) as soNhanVien " +
                     "FROM phongban pb LEFT JOIN nhanvien nv ON pb.maphongban = nv.maphongban " +
                     "GROUP BY pb.maphongban, pb.tenphongban";
        Connection conn = JDBCConection.getConnection();
        if (conn == null) {
            System.err.println("Lỗi: Không thể kết nối database trong DepartmentDAO.getAllWithEmployeeCount()");
            return list;
        }
        try (conn;
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                DepartmentDTO dto = new DepartmentDTO(
                        rs.getString("maphongban"),
                        rs.getString("tenphongban"),
                        rs.getInt("soNhanVien")
                );
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
