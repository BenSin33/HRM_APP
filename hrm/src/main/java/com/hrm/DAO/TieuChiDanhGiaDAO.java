package com.hrm.DAO;

import com.hrm.DTO.Manager.TieuChiDanhGiaDTO;
import com.hrm.UI.Manager.config.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TieuChiDanhGiaDAO {

    public List<TieuChiDanhGiaDTO> getAll() {
        List<TieuChiDanhGiaDTO> list = new ArrayList<>();
        String sql = "SELECT MATIEUCHI, TENTIEUCHI, DIEM FROM tieuchidanhgia ORDER BY MATIEUCHI";

        try (Connection conn = JDBCUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new TieuChiDanhGiaDTO(
                        rs.getString("MATIEUCHI"),
                        rs.getString("TENTIEUCHI"),
                        rs.getInt("DIEM")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}

