package com.hrm.DAO.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.hrm.utils.JDBCConection;

public class PayrollDAO {
    public List<Map<String, Object>> getSalaryHistory(String manv) {
        List<Map<String, Object>> history = new ArrayList<>();
        LocalDate now = LocalDate.now();

        // Duyệt ngược từ tháng hiện tại về 5 tháng trước
        for (int i = 4; i >= 0; i--) {
            LocalDate targetDate = now.minusMonths(i);
            int thang = targetDate.getMonthValue();
            int nam = targetDate.getYear();

            double thuclinh = 0.0;

            // Truy vấn giá trị thực tế từ DB
            String sql = "SELECT THUCLINH FROM bangluong WHERE MANV = ? AND THANG = ? AND NAM = ?";
            try (Connection conn = JDBCConection.getConnection();
                    PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, manv);
                ps.setInt(2, thang);
                ps.setInt(3, nam);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    thuclinh = rs.getDouble("THUCLINH");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Map<String, Object> row = new HashMap<>();
            row.put("label", thang + "/" + nam);
            row.put("value", thuclinh);
            history.add(row);
        }
        return history;
    }
}