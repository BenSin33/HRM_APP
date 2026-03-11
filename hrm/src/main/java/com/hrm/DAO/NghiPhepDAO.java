package com.hrm.DAO;
import com.hrm.DTO.Manager.NghiPhepDTO;
import com.hrm.UI.Manager.config.JDBCUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NghiPhepDAO {

    // Lấy tất cả đơn nghỉ phép (JOIN với nhanvien, phongban, chucvu)
    public List<NghiPhepDTO> getAll() {
        List<NghiPhepDTO> list = new ArrayList<>();
        String sql = "SELECT np.*, nv.HOTEN as tennv, pb.TENPHONGBAN, cv.TENVITRI " +
                     "FROM nghiphep np " +
                     "JOIN nhanvien nv ON np.MANV = nv.MANV " +
                     "LEFT JOIN phongban pb ON nv.MAPHONGBAN = pb.MAPHONGBAN " +
                     "LEFT JOIN chucvu cv ON nv.MACHUCVU = cv.MACHUCVU " +
                     "ORDER BY np.NGAYNGHI DESC";
        
        try (Connection conn = JDBCUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            int count = 0;
            while (rs.next()) {
                count++;
                NghiPhepDTO dto = new NghiPhepDTO();
                dto.setManghiphep(rs.getString("MANGHIPHEP"));
                dto.setManv(rs.getString("MANV"));
                dto.setLoainghi(rs.getString("LOAINGHI"));
                dto.setLydonghi(rs.getString("LYDONGHI"));
                
                // Parse dates
                Date ngaynghi = rs.getDate("NGAYNGHI");
                dto.setNgaynghi(ngaynghi != null ? ngaynghi.toLocalDate() : null);
                
                Date ngaylamlai = rs.getDate("NGAYLAMLAI");
                dto.setNgaylamlai(ngaylamlai != null ? ngaylamlai.toLocalDate() : null);
                
                dto.setNguoiduyet(rs.getString("NGUOIDUYET"));
                
                Date ngayduyet = rs.getDate("NGAYDUYET");
                dto.setNgayduyet(ngayduyet != null ? ngayduyet.toLocalDate() : null);
                
                // Trạng thái
                String trangthai = rs.getString("TRANGTHAI");
                dto.setTrangthai(trangthai != null ? trangthai : "Chờ duyệt");
                
                dto.setLydotuchoi(rs.getString("LYDOTUCHOI"));
                
                // Thông tin JOIN
                dto.setTennv(rs.getString("tennv"));
                dto.setTenphongban(rs.getString("TENPHONGBAN"));
                dto.setTenchucvu(rs.getString("TENVITRI"));
                
                list.add(dto);
            }
            System.out.println("[NghiPhepDAO] Loaded " + count + " leave requests from DB");
        } catch (SQLException e) {
            System.err.println("[NghiPhepDAO] Error loading data: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
    
    // Duyệt đơn
    public boolean duyetDon(String manghiphep, String nguoiduyet) {
        String sql = "UPDATE nghiphep SET TRANGTHAI = 'Đã duyệt', NGUOIDUYET = ?, NGAYDUYET = CURDATE() WHERE MANGHIPHEP = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nguoiduyet);
            ps.setString(2, manghiphep);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[NghiPhepDAO] Error approving: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Từ chối đơn
    public boolean tuChoiDon(String manghiphep, String nguoiduyet, String lydo) {
        String sql = "UPDATE nghiphep SET TRANGTHAI = 'Từ chối', NGUOIDUYET = ?, NGAYDUYET = CURDATE(), LYDOTUCHOI = ? WHERE MANGHIPHEP = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nguoiduyet);
            ps.setString(2, lydo);
            ps.setString(3, manghiphep);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[NghiPhepDAO] Error rejecting: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Thống kê theo trạng thái
    public int countByTrangThai(String trangthai) {
        String sql = "SELECT COUNT(*) FROM nghiphep WHERE TRANGTHAI = ?";
        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangthai);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[NghiPhepDAO] Error counting: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // Số đơn đang nghỉ trong ngày hôm nay (đã được duyệt)
    public int countOnLeaveToday() {
        String sql = "SELECT COUNT(*) FROM nghiphep " +
                     "WHERE TRANGTHAI = 'Đã duyệt' " +
                     "AND (CURDATE() BETWEEN NGAYNGHI AND COALESCE(NGAYLAMLAI, NGAYNGHI))";
        try (Connection conn = JDBCUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[NghiPhepDAO] Error countOnLeaveToday: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
}

