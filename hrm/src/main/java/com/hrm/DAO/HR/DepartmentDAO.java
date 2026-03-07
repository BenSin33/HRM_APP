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
}
