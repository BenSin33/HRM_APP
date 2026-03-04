package com.hrm.DAO.Employee;

import com.hrm.DTO.Employee.EvaluationHistoryDTO;
import com.hrm.utils.JDBCConection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EvaluationDAO {

    public List<EvaluationHistoryDTO> getEvaluationHistory(String manv) {
        List<EvaluationHistoryDTO> histories = new ArrayList<>();

        String sql = "SELECT MADOT, TONGDIEM, NHANXET, QUYETDINH, NGAYDANHGIA " +
                "FROM phieudanhgia " +
                "WHERE MANV = ? " +
                "ORDER BY NGAYDANHGIA DESC";

        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, manv);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                EvaluationHistoryDTO dto = new EvaluationHistoryDTO();
                dto.setMaDot(rs.getString("MADOT"));
                dto.setTongDiem(rs.getInt("TONGDIEM"));
                dto.setNhanXet(rs.getString("NHANXET"));
                dto.setQuyetDinh(rs.getString("QUYETDINH"));
                dto.setNgayDanhGia(rs.getDate("NGAYDANHGIA"));
                histories.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return histories;
    }
}
