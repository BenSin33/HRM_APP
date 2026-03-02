package com.hrm.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.hrm.DTO.ContractDTO;
import com.hrm.utils.JDBCConection;

public class ContractDAO {
    
    // Lấy tất cả hợp đồng
    public List<ContractDTO> getAllContracts() {
        List<ContractDTO> list = new ArrayList<>();
        String sql = "SELECT hd.MAHOPDONG, hd.MANV, nv.HOTEN, pb.TENPHONGBAN, hd.LOAIHOPDONG, " +
                     "hd.NGAYLAMHOPDONG, hd.HANHOPDONG, hd.LUONGCOBAN " +
                     "FROM hopdong hd " +
                     "JOIN nhanvien nv ON hd.MANV = nv.MANV " +
                     "JOIN phongban pb ON nv.MAPHONGBAN = pb.MAPHONGBAN " +
                     "ORDER BY nv.HOTEN ASC";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                ContractDTO dto = new ContractDTO(
                    rs.getString("MAHOPDONG"),
                    rs.getString("MANV"),
                    rs.getString("HOTEN"),
                    rs.getString("TENPHONGBAN"),
                    rs.getString("LOAIHOPDONG"),
                    rs.getDate("NGAYLAMHOPDONG").toLocalDate(),
                    rs.getDate("HANHOPDONG").toLocalDate(),
                    rs.getBigDecimal("LUONGCOBAN")
                );
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy hợp đồng theo mã
    public ContractDTO getContractByMa(String maHopDong) {
        String sql = "SELECT hd.MAHOPDONG, hd.MANV, nv.HOTEN, pb.TENPHONGBAN, hd.LOAIHOPDONG, " +
                     "hd.NGAYLAMHOPDONG, hd.HANHOPDONG, hd.LUONGCOBAN " +
                     "FROM hopdong hd " +
                     "JOIN nhanvien nv ON hd.MANV = nv.MANV " +
                     "JOIN phongban pb ON nv.MAPHONGBAN = pb.MAPHONGBAN " +
                     "WHERE hd.MAHOPDONG = ?";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maHopDong);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ContractDTO(
                        rs.getString("MAHOPDONG"),
                        rs.getString("MANV"),
                        rs.getString("HOTEN"),
                        rs.getString("TENPHONGBAN"),
                        rs.getString("LOAIHOPDONG"),
                        rs.getDate("NGAYLAMHOPDONG").toLocalDate(),
                        rs.getDate("HANHOPDONG").toLocalDate(),
                        rs.getBigDecimal("LUONGCOBAN")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lấy hợp đồng theo nhân viên
    public List<ContractDTO> getContractsByMaNV(String maNV) {
        List<ContractDTO> list = new ArrayList<>();
        String sql = "SELECT hd.MAHOPDONG, hd.MANV, nv.HOTEN, pb.TENPHONGBAN, hd.LOAIHOPDONG, " +
                     "hd.NGAYLAMHOPDONG, hd.HANHOPDONG, hd.LUONGCOBAN " +
                     "FROM hopdong hd " +
                     "JOIN nhanvien nv ON hd.MANV = nv.MANV " +
                     "JOIN phongban pb ON nv.MAPHONGBAN = pb.MAPHONGBAN " +
                     "WHERE hd.MANV = ? " +
                     "ORDER BY hd.NGAYLAMHOPDONG DESC";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maNV);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ContractDTO dto = new ContractDTO(
                        rs.getString("MAHOPDONG"),
                        rs.getString("MANV"),
                        rs.getString("HOTEN"),
                        rs.getString("TENPHONGBAN"),
                        rs.getString("LOAIHOPDONG"),
                        rs.getDate("NGAYLAMHOPDONG").toLocalDate(),
                        rs.getDate("HANHOPDONG").toLocalDate(),
                        rs.getBigDecimal("LUONGCOBAN")
                    );
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Thêm hợp đồng
    public boolean addContract(ContractDTO contract) {
        String sql = "INSERT INTO hopdong (MAHOPDONG, MANV, LOAIHOPDONG, NGAYLAMHOPDONG, HANHOPDONG, LUONGCOBAN) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, contract.maHopDong);
            ps.setString(2, contract.maNV);
            ps.setString(3, contract.loaiHopDong);
            ps.setObject(4, contract.ngayLamHopDong);
            ps.setObject(5, contract.hanHopDong);
            ps.setBigDecimal(6, contract.luongCoBan);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật hợp đồng
    public boolean updateContract(ContractDTO contract) {
        String sql = "UPDATE hopdong SET LOAIHOPDONG = ?, NGAYLAMHOPDONG = ?, HANHOPDONG = ?, LUONGCOBAN = ? " +
                     "WHERE MAHOPDONG = ?";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, contract.loaiHopDong);
            ps.setObject(2, contract.ngayLamHopDong);
            ps.setObject(3, contract.hanHopDong);
            ps.setBigDecimal(4, contract.luongCoBan);
            ps.setString(5, contract.maHopDong);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa hợp đồng
    public boolean deleteContract(String maHopDong) {
        String sql = "DELETE FROM hopdong WHERE MAHOPDONG = ?";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maHopDong);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
