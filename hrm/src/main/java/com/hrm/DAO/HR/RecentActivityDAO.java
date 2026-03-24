package com.hrm.DAO.HR;

import com.hrm.utils.JDBCConection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.YearMonth;

public class RecentActivityDAO {

    public int countEmployeesCheckedInToday() {
        String sql = """
                SELECT COUNT(DISTINCT cc.MANV) AS cnt
                FROM chamcong cc
                WHERE cc.NGAYLAMVIEC = CURDATE()
                  AND cc.CHECKIN IS NOT NULL
                """;
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("cnt");
        } catch (Exception e) {
            System.err.println("[RecentActivityDAO] countEmployeesCheckedInToday: " + e.getMessage());
        }
        return 0;
    }

    public int countLeaveRequestsToday() {
        // Hiểu "đơn nghỉ phép hôm nay" là đơn có ngày nghỉ = hôm nay
        String sql = """
                SELECT COUNT(*) AS cnt
                FROM nghiphep np
                WHERE np.NGAYNGHI = CURDATE()
                """;
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("cnt");
        } catch (Exception e) {
            System.err.println("[RecentActivityDAO] countLeaveRequestsToday: " + e.getMessage());
        }
        return 0;
    }

    public PayrollStatus getPayrollStatus(YearMonth month) {
        PayrollStatus st = new PayrollStatus(month.getMonthValue(), month.getYear());

        String sql = """
                SELECT
                    COUNT(*) AS total,
                    SUM(CASE WHEN bl.TRANGTHAI = 1 THEN 1 ELSE 0 END) AS lockedCount,
                    SUM(CASE WHEN bl.TINH_TRANG_TT = 'Đã thanh toán' THEN 1 ELSE 0 END) AS paidCount
                FROM bangluong bl
                WHERE bl.THANG = ? AND bl.NAM = ?
                """;
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, st.thang);
            ps.setInt(2, st.nam);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    st.total = rs.getInt("total");
                    st.lockedCount = rs.getInt("lockedCount");
                    st.paidCount = rs.getInt("paidCount");
                }
            }
        } catch (Exception e) {
            System.err.println("[RecentActivityDAO] getPayrollStatus: " + e.getMessage());
        }
        return st;
    }

    public EvaluationStatus getEvaluationStatusForLastMonth() {
        YearMonth lastMonth = YearMonth.now().minusMonths(1);
        int q = ((lastMonth.getMonthValue() - 1) / 3) + 1; // map tháng -> quý
        int y = lastMonth.getYear();
        String kyKy = "Q" + q;

        EvaluationStatus st = new EvaluationStatus(kyKy, y);

        // 1) Có tạo đợt đánh giá cho quý của "tháng trước" chưa?
        String sqlPeriod = """
                SELECT MADOT
                FROM dotdanhgia
                WHERE KYKY = ? AND NAM = ?
                ORDER BY MADOT DESC
                LIMIT 1
                """;
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlPeriod)) {
            ps.setString(1, kyKy);
            ps.setInt(2, y);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) st.maDot = rs.getString("MADOT");
            }
        } catch (Exception e) {
            System.err.println("[RecentActivityDAO] getEvaluationStatusForLastMonth(period): " + e.getMessage());
        }

        if (st.maDot == null || st.maDot.isBlank()) {
            st.hasPeriod = false;
            return st;
        }
        st.hasPeriod = true;

        // 2) Đã tạo phiếu/chưa? Đã duyệt bao nhiêu?
        String sqlCount = """
                SELECT
                    COUNT(*) AS total,
                    SUM(CASE WHEN TRANGTHAI_DUYET = 'Đã duyệt' THEN 1 ELSE 0 END) AS approved
                FROM phieudanhgia
                WHERE MADOT = ?
                """;
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlCount)) {
            ps.setString(1, st.maDot);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    st.totalEvaluations = rs.getInt("total");
                    st.approvedEvaluations = rs.getInt("approved");
                }
            }
        } catch (Exception e) {
            System.err.println("[RecentActivityDAO] getEvaluationStatusForLastMonth(count): " + e.getMessage());
        }
        return st;
    }

    public static class PayrollStatus {
        public final int thang;
        public final int nam;
        public int total;
        public int lockedCount;
        public int paidCount;

        public PayrollStatus(int thang, int nam) {
            this.thang = thang;
            this.nam = nam;
        }

        public boolean hasAny() { return total > 0; }
        public boolean isLockedAll() { return total > 0 && lockedCount == total; }
        public boolean isPaidAll() { return total > 0 && paidCount == total; }
    }

    public static class EvaluationStatus {
        public final String kyKy;
        public final int nam;
        public boolean hasPeriod;
        public String maDot;
        public int totalEvaluations;
        public int approvedEvaluations;

        public EvaluationStatus(String kyKy, int nam) {
            this.kyKy = kyKy;
            this.nam = nam;
        }
    }
}

