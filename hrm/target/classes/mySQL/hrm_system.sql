CREATE DATABASE hrm_system CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE hrm_system;

-- ========================================================
-- I. NHÓM BẢNG DANH MỤC
-- ========================================================

CREATE TABLE `phongban` (
  `MAPHONGBAN` varchar(10) PRIMARY KEY,
  `TENPHONGBAN` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `chucvu` (
  `MACHUCVU` varchar(10) PRIMARY KEY,
  `TENVITRI` varchar(100) NOT NULL,
  `PHUCAPCHUCVU` decimal(18,2) DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `trinhdo` (
  `MATRINHDO` varchar(10) PRIMARY KEY,
  `TRINHDO` varchar(50) NOT NULL,
  `HESOTRINHDO` decimal(5,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `calam` (
  `MACALAM` varchar(10) PRIMARY KEY,
  `TENCALAM` varchar(50) DEFAULT NULL,
  `GIOVAOCA` time DEFAULT NULL,
  `GIOTANCA` time DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `tieuchidanhgia` (
  `MATIEUCHI` varchar(10) PRIMARY KEY,
  `TENTIEUCHI` varchar(100) DEFAULT NULL,
  `DIEM` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ========================================================
-- II. NHÓM NHÂN SỰ VÀ TÀI KHOẢN
-- ========================================================

CREATE TABLE `nhanvien` (
  `MANV` varchar(10) PRIMARY KEY,
  `MAPHONGBAN` varchar(10),
  `MACHUCVU` varchar(10),
  `MATRINHDO` varchar(10),
  `HOTEN` varchar(100) NOT NULL,
  `GIOITINH` varchar(10) DEFAULT NULL,
  `DIACHI` varchar(255) DEFAULT NULL,
  `DIENTHOAI` varchar(15) DEFAULT NULL,
  `EMAIL` varchar(100) DEFAULT NULL,
  `NGAYVAOLAM` date DEFAULT NULL,
  `SONGAYPHEP` int(11) DEFAULT 12,
  `TRANGTHAI` varchar(50) DEFAULT NULL,
  CONSTRAINT `fk_nv_pb` FOREIGN KEY (`MAPHONGBAN`) REFERENCES `phongban`(`MAPHONGBAN`),
  CONSTRAINT `fk_nv_cv` FOREIGN KEY (`MACHUCVU`) REFERENCES `chucvu`(`MACHUCVU`),
  CONSTRAINT `fk_nv_td` FOREIGN KEY (`MATRINHDO`) REFERENCES `trinhdo`(`MATRINHDO`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `role` (
  `ROLEID` varchar(10) PRIMARY KEY,
  `ROLENAME` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `taikhoan` (
  `USERID` varchar(50) PRIMARY KEY,
  `MANV` varchar(10),
  `ROLEID` varchar(10),
  `PASSWORD` varchar(255) NOT NULL,
  `STATUS` int(11) DEFAULT 1,
  CONSTRAINT `fk_tk_nv` FOREIGN KEY (`MANV`) REFERENCES `nhanvien`(`MANV`),
  CONSTRAINT `fk_tk_role` FOREIGN KEY (`ROLEID`) REFERENCES `role`(`ROLEID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ========================================================
-- III. PHÂN QUYỀN LINH ĐỘNG (DỰA TRÊN QUAN HỆ N-N)
-- ========================================================

CREATE TABLE `chucnang` (
  `MACHUCNANG` varchar(10) PRIMARY KEY,
  `TENCHUCNANG` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `phanquyen_chitiet` (
  `ROLEID` varchar(10),
  `MACHUCNANG` varchar(10),
  `QUYEN_XEM` boolean DEFAULT false,
  `QUYEN_THEM` boolean DEFAULT false,
  `QUYEN_SUA` boolean DEFAULT false,
  `QUYEN_XOA` boolean DEFAULT false,
  PRIMARY KEY (`ROLEID`, `MACHUCNANG`),
  CONSTRAINT `fk_pq_role` FOREIGN KEY (`ROLEID`) REFERENCES `role`(`ROLEID`),
  CONSTRAINT `fk_pq_cn` FOREIGN KEY (`MACHUCNANG`) REFERENCES `chucnang`(`MACHUCNANG`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ========================================================
-- IV. LƯƠNG CHUYÊN SÂU (SNAPSHOT & BIẾN ĐỘNG)
-- ========================================================

CREATE TABLE `danhmuc_phucap` (
  `MAPHUCAP` int AUTO_INCREMENT PRIMARY KEY,
  `TENPHUCAP` varchar(100),
  `SOTIEN_MACDINH` decimal(18,2)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `danhmuc_khautru` (
  `MAKHAUTRU` int AUTO_INCREMENT PRIMARY KEY,
  `TENKHAUTRU` varchar(100),
  `SOTIEN_MACDINH` decimal(18,2)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `bangluong` (
  `MALUONG` varchar(10) PRIMARY KEY,
  `MANV` varchar(10),
  `THANG` int(11),
  `NAM` int(11),
  `LUONGCOBAN_SNAPSHOT` decimal(18,2),
  `SONGAYCONG` float DEFAULT 0,
  `TONG_PHUCAP` decimal(18,2) DEFAULT 0,
  `TONG_KHAUTRU` decimal(18,2) DEFAULT 0,
  `NGAYCHOTLUONG` date,
  `THUCLINH` decimal(18,2),
  `TRANGTHAI` varchar(50),
  CONSTRAINT `fk_bl_nv` FOREIGN KEY (`MANV`) REFERENCES `nhanvien`(`MANV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `chitiet_luong_biendong` (
  `ID` int AUTO_INCREMENT PRIMARY KEY,
  `MALUONG` varchar(10),
  `TENKHOANTIEN` varchar(100),
  `SOTIEN` decimal(18,2),
  `LOAI` varchar(10),
  CONSTRAINT `fk_ct_bl` FOREIGN KEY (`MALUONG`) REFERENCES `bangluong`(`MALUONG`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ========================================================
-- V. CHẤM CÔNG, NGHỈ PHÉP, HỢP ĐỒNG, ĐÁNH GIÁ
-- ========================================================

CREATE TABLE `chamcong` (
  `MACHAMCONG` varchar(10) PRIMARY KEY,
  `MANV` varchar(10),
  `NGAYLAMVIEC` date,
  `SOGIOLAM` float,
  `CHECKIN` time,
  `CHECKOUT` time,
  `TRANGTHAI` varchar(50),
  CONSTRAINT `fk_cc_nv` FOREIGN KEY (`MANV`) REFERENCES `nhanvien`(`MANV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `hopdong` (
  `MAHOPDONG` varchar(10) PRIMARY KEY,
  `MANV` varchar(10),
  `LOAIHOPDONG` varchar(100),
  `NGAYLAMHOPDONG` date,
  `HANHOPDONG` date,
  `LUONGCOBAN` decimal(18,2),
  CONSTRAINT `fk_hd_nv` FOREIGN KEY (`MANV`) REFERENCES `nhanvien`(`MANV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `nghiphep` (
  `MANGHIPHEP` varchar(10) PRIMARY KEY,
  `MANV` varchar(10),
  `LOAINGHI` varchar(50),
  `LYDONGHI` varchar(255),
  `NGAYNGHI` date,
  `NGAYLAMLAI` date,
  `NGUOIDUYET` varchar(100),
  `NGAYDUYET` date,
  CONSTRAINT `fk_np_nv` FOREIGN KEY (`MANV`) REFERENCES `nhanvien`(`MANV`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `lichlamviec` (
  `MALICH` varchar(10) PRIMARY KEY,
  `MANV` varchar(10),
  `MACALAM` varchar(10),
  `NGAYLAMVIEC` date,
  `GHICHU` varchar(255),
  CONSTRAINT `fk_llv_nv` FOREIGN KEY (`MANV`) REFERENCES `nhanvien`(`MANV`),
  CONSTRAINT `fk_llv_cl` FOREIGN KEY (`MACALAM`) REFERENCES `calam`(`MACALAM`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `phieudanhgia` (
  `MAPHIEU` varchar(10) PRIMARY KEY,
  `MANV` varchar(10),
  `MADOT` varchar(10),
  `MATIEUCHI` varchar(10),
  `TONGDIEM` int(11),
  `NHANXET` varchar(255),
  `QUYETDINH` varchar(100),
  `NGAYDANHGIA` date,
  CONSTRAINT `fk_pdg_nv` FOREIGN KEY (`MANV`) REFERENCES `nhanvien`(`MANV`),
  CONSTRAINT `fk_pdg_tc` FOREIGN KEY (`MATIEUCHI`) REFERENCES `tieuchidanhgia`(`MATIEUCHI`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ========================================================
-- VI. DỮ LIỆU MẪU (3 HR, 3 MANAGER, 3 EMPLOYEE)
-- ========================================================

-- Danh mục
INSERT INTO `phongban` VALUES ('PB01', 'Nhân sự'), ('PB02', 'Kỹ thuật'), ('PB03', 'Kinh doanh');
INSERT INTO `chucvu` VALUES ('CV01', 'Trưởng phòng', 3000000.00), ('CV02', 'Nhân viên', 0.00);
INSERT INTO `trinhdo` VALUES ('TD01', 'Đại học', 1.0), ('TD02', 'Thạc sĩ', 1.2);

-- Nhân viên
INSERT INTO `nhanvien` (`MANV`, `MAPHONGBAN`, `MACHUCVU`, `MATRINHDO`, `HOTEN`, `TRANGTHAI`) VALUES 
('NV01', 'PB01', 'CV01', 'TD02', 'Nguyễn HR 1', 'Đang làm việc'),
('NV02', 'PB01', 'CV02', 'TD01', 'Trần HR 2', 'Đang làm việc'),
('NV03', 'PB01', 'CV02', 'TD01', 'Lê HR 3', 'Đang làm việc'),
('NV04', 'PB02', 'CV01', 'TD02', 'Phạm Mgr 1', 'Đang làm việc'),
('NV05', 'PB02', 'CV01', 'TD01', 'Hoàng Mgr 2', 'Đang làm việc'),
('NV06', 'PB03', 'CV01', 'TD01', 'Vũ Mgr 3', 'Đang làm việc'),
('NV07', 'PB02', 'CV02', 'TD01', 'Ngô Staff 1', 'Đang làm việc'),
('NV08', 'PB03', 'CV02', 'TD01', 'Đỗ Staff 2', 'Đang làm việc'),
('NV09', 'PB03', 'CV02', 'TD01', 'Đặng Staff 3', 'Đang làm việc');

-- Quyền & Chức năng
INSERT INTO `role` VALUES ('R1', 'Admin'), ('R2', 'Manager'), ('R3', 'Employee');
INSERT INTO `chucnang` VALUES ('CN01', 'Quản lý nhân sự'), ('CN02', 'Quản lý lương'), ('CN03', 'Chấm công');
INSERT INTO `phanquyen_chitiet` VALUES 
('R1', 'CN01', 1, 1, 1, 1), ('R1', 'CN02', 1, 1, 1, 1),
('R2', 'CN01', 1, 0, 1, 0), ('R3', 'CN03', 1, 1, 0, 0);

-- Tài khoản
INSERT INTO `taikhoan` (`USERID`, `MANV`, `ROLEID`, `PASSWORD`) VALUES 
('admin1', 'NV01', 'R1', '123'), ('admin2', 'NV02', 'R1', '123'), ('admin3', 'NV03', 'R1', '123'),
('mgr1', 'NV04', 'R2', '123'), ('mgr2', 'NV05', 'R2', '123'), ('mgr3', 'NV06', 'R2', '123'),
('emp1', 'NV07', 'R3', '123'), ('emp2', 'NV08', 'R3', '123'), ('emp3', 'NV09', 'R3', '123');

-- Lương & Biến động mẫu (NV07)
INSERT INTO `bangluong` (`MALUONG`, `MANV`, `THANG`, `NAM`, `LUONGCOBAN_SNAPSHOT`, `SONGAYCONG`, `THUCLINH`, `TRANGTHAI`) 
VALUES ('ML01', 'NV07', 1, 2026, 8000000.00, 26, 8500000.00, 'Đã thanh toán');

INSERT INTO `chitiet_luong_biendong` (`MALUONG`, `TENKHOANTIEN`, `SOTIEN`, `LOAI`) VALUES 
('ML01', 'Phụ cấp ăn trưa', 1000000.00, 'CONG'),
('ML01', 'Bảo hiểm xã hội', 500000.00, 'TRU');

-- ========================================================
-- VI. DỮ LIỆU MẪU ĐẦY ĐỦ CHO TOÀN BỘ HỆ THỐNG
-- ========================================================

-- 1. Danh mục bổ sung
INSERT INTO `calam` VALUES ('C1', 'Hành chính', '08:00:00', '17:00:00'), ('C2', 'Ca sáng', '06:00:00', '14:00:00');
INSERT INTO `tieuchidanhgia` VALUES ('TC01', 'Năng suất làm việc', 10), ('TC02', 'Thái độ phối hợp', 10), ('TC03', 'Kỹ năng chuyên môn', 10);
INSERT INTO `danhmuc_phucap` (`TENPHUCAP`, `SOTIEN_MACDINH`) VALUES ('Ăn trưa', 1000000.00), ('Xăng xe', 500000.00), ('Độc hại', 300000.00);
INSERT INTO `danhmuc_khautru` (`TENKHAUTRU`, `SOTIEN_MACDINH`) VALUES ('Bảo hiểm xã hội', 500000.00), ('Phí công đoàn', 100000.00);

-- 2. Hợp đồng lao động (Dành cho 9 nhân viên đã tạo ở phần trước)
INSERT INTO `hopdong` (`MAHOPDONG`, `MANV`, `LOAIHOPDONG`, `NGAYLAMHOPDONG`, `HANHOPDONG`, `LUONGCOBAN`) VALUES 
('HD01', 'NV01', 'Vô thời hạn', '2025-01-01', '2099-12-31', 25000000.00),
('HD02', 'NV02', '3 năm', '2025-01-05', '2028-01-05', 15000000.00),
('HD03', 'NV03', '3 năm', '2025-01-10', '2028-01-10', 12000000.00),
('HD04', 'NV04', 'Vô thời hạn', '2025-01-01', '2099-12-31', 20000000.00),
('HD05', 'NV05', '3 năm', '2025-01-01', '2028-01-01', 18000000.00),
('HD06', 'NV06', 'Vô thời hạn', '2025-01-01', '2099-12-31', 22000000.00),
('HD07', 'NV07', '1 năm', '2025-02-01', '2026-02-01', 8000000.00),
('HD08', 'NV08', '1 năm', '2025-02-01', '2026-02-01', 9000000.00),
('HD09', 'NV09', '1 năm', '2025-02-01', '2026-02-01', 8500000.00);

-- 3. Chấm công mẫu (Dữ liệu mẫu cho vài ngày của tháng 1)
INSERT INTO `chamcong` (`MACHAMCONG`, `MANV`, `NGAYLAMVIEC`, `SOGIOLAM`, `CHECKIN`, `CHECKOUT`, `TRANGTHAI`) VALUES 
('CC01', 'NV07', '2026-01-27', 8, '08:00:00', '17:00:00', 'Đúng giờ'),
('CC02', 'NV08', '2026-01-27', 8, '07:55:00', '17:05:00', 'Đúng giờ'),
('CC03', 'NV09', '2026-01-27', 7.5, '08:30:00', '17:00:00', 'Đi muộn');

-- 4. Lịch làm việc
INSERT INTO `lichlamviec` (`MALICH`, `MANV`, `MACALAM`, `NGAYLAMVIEC`, `GHICHU`) VALUES 
('L01', 'NV07', 'C1', '2026-02-04', 'Làm tại văn phòng'),
('L02', 'NV08', 'C1', '2026-02-04', 'Trực kỹ thuật'),
('L03', 'NV09', 'C1', '2026-02-04', 'Hỗ trợ khách hàng');

-- 5. Nghỉ phép
INSERT INTO `nghiphep` (`MANGHIPHEP`, `MANV`, `LOAINGHI`, `LYDONGHI`, `NGAYNGHI`, `NGAYLAMLAI`, `NGUOIDUYET`, `NGAYDUYET`) VALUES 
('NP01', 'NV07', 'Có lương', 'Ốm nhẹ', '2026-01-20', '2026-01-21', 'Nguyễn HR 1', '2026-01-19');

-- 6. Đánh giá nhân viên
INSERT INTO `phieudanhgia` (`MAPHIEU`, `MANV`, `MADOT`, `MATIEUCHI`, `TONGDIEM`, `NHANXET`, `QUYETDINH`, `NGAYDANHGIA`) VALUES 
('DG01', 'NV07', 'Q1-2026', 'TC01', 9, 'Nhiệt tình, hoàn thành tốt', 'Giữ nguyên', '2026-01-31'),
('DG02', 'NV04', 'Q1-2026', 'TC03', 10, 'Lãnh đạo xuất sắc', 'Khen thưởng', '2026-01-31');