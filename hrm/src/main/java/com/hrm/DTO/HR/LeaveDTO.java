package com.hrm.DTO.HR;


public class LeaveDTO {


    public static class LeaveSummaryDTO {
        public int totalRequests;
        public int pendingCount;
        public int approvedCount;
        public int rejectedCount;

        public LeaveSummaryDTO() {}

        public LeaveSummaryDTO(int total, int pending, int approved, int rejected) {
            this.totalRequests = total;
            this.pendingCount  = pending;
            this.approvedCount = approved;
            this.rejectedCount = rejected;
        }

        @Override public String toString() {
            return "LeaveSummaryDTO{total=" + totalRequests
                    + ", pending=" + pendingCount
                    + ", approved=" + approvedCount
                    + ", rejected=" + rejectedCount + "}";
        }
    }


    public static class LeaveRowDTO {
        /** Mã đơn nghỉ phép, VD: "NP01" */
        public String maNghiPhep;
        /** Mã nhân viên (dùng nội bộ) */
        public String manv;
        /** Họ tên nhân viên */
        public String hoTen;
        /** Tên phòng ban */
        public String phongBan;
        /** Loại nghỉ đã chuẩn hóa: "Nghỉ phép năm" | "Nghỉ ốm" | "Nghỉ việc riêng" … */
        public String loaiNghi;
        /** Ngày bắt đầu nghỉ dạng "d/M/yyyy" */
        public String tuNgay;
        /** Ngày kết thúc / ngày làm lại dạng "d/M/yyyy" */
        public String denNgay;
        /** Số ngày nghỉ (inclusive: tuNgay → denNgay) */
        public int soNgay;
        /** Lý do nghỉ */
        public String lyDo;
        /**
         * Trạng thái: "Chờ duyệt" | "Đã duyệt" | "Từ chối"
         * Khớp đúng với StatusBadgeRenderer và ActionRenderer
         */
        public String trangThai;

        public LeaveRowDTO() {}

        /**
         * Convert sang Object[] cho DefaultTableModel của LeaveTable.
         * Thứ tự: MÃ ĐƠN | NHÂN VIÊN | LOẠI NGHỈ | TỪ NGÀY | ĐẾN NGÀY
         *         | SỐ NGÀY | LÝ DO | TRẠNG THÁI | THAO TÁC
         */
        public Object[] toTableRow() {
            return new Object[]{
                maNghiPhep,
                new String[]{hoTen, phongBan},
                loaiNghi,
                tuNgay,
                denNgay,
                soNgay,
                lyDo,
                trangThai,
                trangThai   // ActionRenderer đọc cùng giá trị để hiện/ẩn icon
            };
        }

        @Override public String toString() {
            return "LeaveRowDTO{" + maNghiPhep + " " + hoTen
                    + " " + loaiNghi + " " + trangThai + "}";
        }
    }
}