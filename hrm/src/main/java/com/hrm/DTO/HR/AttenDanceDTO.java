package com.hrm.DTO.HR;

import java.util.List;


public class AttenDanceDTO {


    public static class SummaryDTO {
        /** Tỷ lệ đi làm đúng giờ, VD: "87%" */
        public String onTimeRate;

        /** Tổng số lần đi muộn trong tháng */
        public int lateDays;

        /** Tổng số ngày nghỉ có phép trong tháng */
        public int leaveDays;

        /** Tổng số ngày vắng không phép trong tháng */
        public int absentDays;

        public SummaryDTO() {}

        public SummaryDTO(String onTimeRate, int lateDays, int leaveDays, int absentDays) {
            this.onTimeRate = onTimeRate;
            this.lateDays   = lateDays;
            this.leaveDays  = leaveDays;
            this.absentDays = absentDays;
        }

        @Override public String toString() {
            return "SummaryDTO{onTime=" + onTimeRate
                    + ", late=" + lateDays
                    + ", leave=" + leaveDays
                    + ", absent=" + absentDays + "}";
        }
    }

 
    public static class EmployeeRowDTO {
        /** Mã nhân viên, VD: "NV07" */
        public String manv;

        /** Họ tên đầy đủ */
        public String hoTen;

        /** Tên chức vụ */
        public String chucVu;

        /** Tên phòng ban */
        public String phongBan;

        /** Số ngày công thực tế trong tháng */
        public int workDays;

        /** Số lần đi muộn trong tháng */
        public int lateDays;

        /** Số ngày nghỉ có phép trong tháng */
        public int leaveDays;

        /** Số ngày vắng không phép trong tháng */
        public int absentDays;

        public EmployeeRowDTO() {}

        /**
         * Convert sang Object[] để truyền vào AttenDanceTable / AttenDanceDetail.
         * Format: {hoTen, manv, chucVu, phongBan, workDays, lateDays, absentDays}
         * (khớp với fullData format trong AttenDanceTable)
         */
        public Object[] toObjectArray() {
            return new Object[]{
                hoTen, manv, chucVu, phongBan,
                workDays, lateDays, absentDays
            };
        }

        @Override public String toString() {
            return "EmployeeRowDTO{" + manv + " - " + hoTen
                    + ", work=" + workDays + ", late=" + lateDays
                    + ", leave=" + leaveDays + ", absent=" + absentDays + "}";
        }
    }


    public static class DailyRecordDTO {
        /** Ngày hiển thị, VD: "1/2/2026" */
        public String ngay;

        /** Tên thứ viết tắt tiếng Việt: "Hai", "Ba", "Tư", "Năm", "Sáu", "Bảy", "CN" */
        public String thu;

        /** Giờ check-in HH:mm, hoặc "--:--" nếu không có */
        public String checkIn;

        /** Giờ check-out HH:mm, hoặc "--:--" nếu không có */
        public String checkOut;

        /** Số giờ làm việc, hoặc "-" nếu không có */
        public String soGio;

        /**
         * Trạng thái: "Đúng giờ" | "Đi muộn" | "Vắng mặt" | "Ngày nghỉ" | "Nghỉ phép"
         * Khớp với switch-case trong AttenDanceDetail.StatusBadgeRenderer
         */
        public String trangThai;

        public DailyRecordDTO() {}

        public DailyRecordDTO(String ngay, String thu, String checkIn,
                               String checkOut, String soGio, String trangThai) {
            this.ngay      = ngay;
            this.thu       = thu;
            this.checkIn   = checkIn;
            this.checkOut  = checkOut;
            this.soGio     = soGio;
            this.trangThai = trangThai;
        }

        /**
         * Convert sang Object[] để đưa vào DefaultTableModel của AttenDanceDetail.
         * Thứ tự: {ngay, thu, checkIn, checkOut, soGio, trangThai}
         */
        public Object[] toTableRow() {
            return new Object[]{ngay, thu, checkIn, checkOut, soGio, trangThai};
        }

        @Override public String toString() {
            return "DailyRecord{" + ngay + " " + thu
                    + " in=" + checkIn + " out=" + checkOut
                    + " " + trangThai + "}";
        }
    }


    public static class DetailHeaderDTO {
        public String manv;
        public String hoTen;
        public String chucVu;

        /** Tổng ngày công thực tế (Đúng giờ + Đi muộn) */
        public int totalWorkDays;

        /** Số lần đi muộn */
        public int totalLate;

        /** Số ngày vắng mặt */
        public int totalAbsent;

        /** Danh sách bản ghi ngày để hiển thị bảng */
        public List<DailyRecordDTO> records;

        public DetailHeaderDTO() {}

        /**
         * Convert sang Object[] để truyền vào AttenDanceDetail constructor.
         * Format: {hoTen, manv, chucVu, phongBan(mock), workDays, late, absent}
         */
        public Object[] toEmpArray(String phongBan) {
            return new Object[]{
                hoTen, manv, chucVu, phongBan,
                totalWorkDays, totalLate, totalAbsent
            };
        }
    }
}