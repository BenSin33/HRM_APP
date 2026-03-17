-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1
-- Thời gian đã tạo: Th3 11, 2026 lúc 03:39 PM
-- Phiên bản máy phục vụ: 10.4.32-MariaDB
-- Phiên bản PHP: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `hrm_system`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `bangluong`
--

CREATE TABLE `bangluong` (
  `MALUONG` varchar(10) NOT NULL,
  `MANV` varchar(10) DEFAULT NULL,
  `THANG` int(11) DEFAULT NULL,
  `NAM` int(11) DEFAULT NULL,
  `LUONGCOBAN_SNAPSHOT` decimal(18,2) DEFAULT NULL,
  `SONGAYCONG` float DEFAULT 0,
  `TONG_PHUCAP` decimal(18,2) DEFAULT 0.00,
  `TONG_KHAUTRU` decimal(18,2) DEFAULT 0.00,
  `NGAYCHOTLUONG` date DEFAULT NULL,
  `THUCLINH` decimal(18,2) DEFAULT NULL,
  `TRANGTHAI` tinyint(1) DEFAULT 0 COMMENT '0: Chưa khóa (Draft), 1: Đã khóa (Locked)',
  `TINH_TRANG_TT` varchar(50) DEFAULT 'Chưa thanh toán'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `bangluong`
--

INSERT INTO `bangluong` (`MALUONG`, `MANV`, `THANG`, `NAM`, `LUONGCOBAN_SNAPSHOT`, `SONGAYCONG`, `TONG_PHUCAP`, `TONG_KHAUTRU`, `NGAYCHOTLUONG`, `THUCLINH`, `TRANGTHAI`, `TINH_TRANG_TT`) VALUES
('ML01', 'NV07', 1, 2026, 8000000.00, 26, 1800000.00, 700000.00, NULL, 10554545.45, 0, 'Chưa thanh toán'),
('ML08', 'NV01', 2, 2026, 25000000.00, 22, 1800000.00, 700000.00, '2026-02-28', 26100000.00, 0, 'Chưa thanh toán'),
('ML09', 'NV02', 2, 2026, 15000000.00, 23, 1800000.00, 700000.00, '2026-02-28', 16781818.18, 0, 'Chưa thanh toán'),
('ML10', 'NV04', 2, 2026, 20000000.00, 24, 1800000.00, 700000.00, NULL, 22918181.82, 0, 'Chưa thanh toán'),
('ML11', 'NV05', 2, 2026, 18000000.00, 22, 1800000.00, 700000.00, NULL, 19100000.00, 0, 'Chưa thanh toán'),
('ML12', 'NV07', 2, 2026, 8000000.00, 24, 1800000.00, 700000.00, NULL, 9827272.73, 0, 'Chưa thanh toán'),
('ML13', 'NV08', 2, 2026, 9000000.00, 24, 1800000.00, 700000.00, NULL, 10918181.82, 0, 'Chưa thanh toán'),
('ML14', 'NV09', 2, 2026, 8500000.00, 20, 1800000.00, 700000.00, NULL, 8827272.73, 0, 'Chưa thanh toán');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `calam`
--

CREATE TABLE `calam` (
  `MACALAM` varchar(10) NOT NULL,
  `TENCALAM` varchar(50) DEFAULT NULL,
  `GIOVAOCA` time DEFAULT NULL,
  `GIOTANCA` time DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `calam`
--

INSERT INTO `calam` (`MACALAM`, `TENCALAM`, `GIOVAOCA`, `GIOTANCA`) VALUES
('C1', 'Hành chính', '08:00:00', '17:00:00'),
('C2', 'Ca sáng', '06:00:00', '14:00:00'),
('OFF', 'Ngày OFF', NULL, NULL);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `chamcong`
--

CREATE TABLE `chamcong` (
  `MACHAMCONG` varchar(10) NOT NULL,
  `MANV` varchar(10) DEFAULT NULL,
  `NGAYLAMVIEC` date DEFAULT NULL,
  `SOGIOLAM` float DEFAULT NULL,
  `CHECKIN` time DEFAULT NULL,
  `CHECKOUT` time DEFAULT NULL,
  `TRANGTHAI` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `chamcong`
--

INSERT INTO `chamcong` (`MACHAMCONG`, `MANV`, `NGAYLAMVIEC`, `SOGIOLAM`, `CHECKIN`, `CHECKOUT`, `TRANGTHAI`) VALUES
('CC01', 'NV07', '2026-01-27', 8, '08:00:00', '17:00:00', 'Đúng giờ'),
('CC02', 'NV08', '2026-01-27', 8, '07:55:00', '17:05:00', 'Đúng giờ'),
('CC03', 'NV09', '2026-01-27', 7.5, '08:30:00', '17:00:00', 'Đi muộn');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `chitiet_luong_biendong`
--

CREATE TABLE `chitiet_luong_biendong` (
  `ID` int(11) NOT NULL,
  `MALUONG` varchar(10) DEFAULT NULL,
  `TENKHOANTIEN` varchar(100) DEFAULT NULL,
  `SOTIEN` decimal(18,2) DEFAULT NULL,
  `LOAI` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `chitiet_luong_biendong`
--

INSERT INTO `chitiet_luong_biendong` (`ID`, `MALUONG`, `TENKHOANTIEN`, `SOTIEN`, `LOAI`) VALUES
(1, 'ML01', 'Phụ cấp ăn trưa', 1000000.00, 'CONG'),
(2, 'ML01', 'Bảo hiểm xã hội', 500000.00, 'TRU');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `chucnang`
--

CREATE TABLE `chucnang` (
  `MACHUCNANG` varchar(10) NOT NULL,
  `TENCHUCNANG` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `chucnang`
--

INSERT INTO `chucnang` (`MACHUCNANG`, `TENCHUCNANG`) VALUES
('CN01', 'Quản lý nhân sự'),
('CN02', 'Quản lý lương'),
('CN03', 'Chấm công'),
('CN04', 'Nghỉ phép'),
('CN05', 'Đánh giá hiệu suất'),
('CN06', 'Quản lý hợp đồng'),
('CN07', 'Quản lý phòng ban'),
('CN08', 'Quản lý quyền'),
('CN09', 'Báo cáo & Thống kê'),
('CN10', 'Lịch làm việc');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `chucvu`
--

CREATE TABLE `chucvu` (
  `MACHUCVU` varchar(10) NOT NULL,
  `TENVITRI` varchar(100) NOT NULL,
  `PHUCAPCHUCVU` decimal(18,2) DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `chucvu`
--

INSERT INTO `chucvu` (`MACHUCVU`, `TENVITRI`, `PHUCAPCHUCVU`) VALUES
('CV01', 'Trưởng phòng', 3000000.00),
('CV02', 'Nhân viên', 1000000.00),
('CV03', 'Nhân sự', 3000000.00);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `danhmuc_khautru`
--

CREATE TABLE `danhmuc_khautru` (
  `MAKHAUTRU` int(11) NOT NULL,
  `TENKHAUTRU` varchar(100) DEFAULT NULL,
  `SOTIEN_MACDINH` decimal(18,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `danhmuc_khautru`
--

INSERT INTO `danhmuc_khautru` (`MAKHAUTRU`, `TENKHAUTRU`, `SOTIEN_MACDINH`) VALUES
(1, 'Bảo hiểm xã hội', 500000.00),
(2, 'Phí công đoàn', 100000.00),
(3, 'Phí gửi xe', 100000.00);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `danhmuc_phucap`
--

CREATE TABLE `danhmuc_phucap` (
  `MAPHUCAP` int(11) NOT NULL,
  `TENPHUCAP` varchar(100) DEFAULT NULL,
  `SOTIEN_MACDINH` decimal(18,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `danhmuc_phucap`
--

INSERT INTO `danhmuc_phucap` (`MAPHUCAP`, `TENPHUCAP`, `SOTIEN_MACDINH`) VALUES
(1, 'Ăn trưa', 1000000.00),
(2, 'Xăng xe', 500000.00),
(3, 'Độc hại', 300000.00);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `dotdanhgia`
--

CREATE TABLE `dotdanhgia` (
  `MADOT` varchar(10) NOT NULL,
  `TENDOT` varchar(100) DEFAULT NULL,
  `KYKY` varchar(10) DEFAULT NULL,
  `NAM` int(11) DEFAULT NULL,
  `NGUOIDANHGIA` varchar(100) DEFAULT NULL,
  `TRANGTHAI` varchar(50) DEFAULT 'Đang mở'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `dotdanhgia`
--

INSERT INTO `dotdanhgia` (`MADOT`, `TENDOT`, `KYKY`, `NAM`, `NGUOIDANHGIA`, `TRANGTHAI`) VALUES
('Q1-2024', 'Đánh giá Quý 1 năm 2024', 'Q1', 2024, 'Trần Thị B', 'Đã đóng'),
('Q1-2026', 'Đánh giá Quý 1 năm 2026', 'Q1', 2026, 'Trần Thị B', 'Đang mở'),
('Q2-2024', 'Đánh giá Quý 2 năm 2024', 'Q2', 2024, 'Trần Thị B', 'Đã đóng'),
('Q3-2024', 'Đánh giá Quý 3 năm 2024', 'Q3', 2024, 'Trần Thị B', 'Đã đóng'),
('Q4-2024', 'Đánh giá Quý 4 năm 2024', 'Q4', 2024, 'Trần Thị B', 'Đã đóng');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `hopdong`
--

CREATE TABLE `hopdong` (
  `MAHOPDONG` varchar(10) NOT NULL,
  `MANV` varchar(10) DEFAULT NULL,
  `LOAIHOPDONG` varchar(100) DEFAULT NULL,
  `NGAYLAMHOPDONG` date DEFAULT NULL,
  `HANHOPDONG` date DEFAULT NULL,
  `LUONGCOBAN` decimal(18,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `hopdong`
--

INSERT INTO `hopdong` (`MAHOPDONG`, `MANV`, `LOAIHOPDONG`, `NGAYLAMHOPDONG`, `HANHOPDONG`, `LUONGCOBAN`) VALUES
('HD01', 'NV01', 'Vô thời hạn', '2025-01-01', '2099-12-31', 25000000.00),
('HD02', 'NV02', '3 năm', '2025-01-05', '2028-01-05', 15000000.00),
('HD03', 'NV03', '3 năm', '2025-01-10', '2028-01-10', 12000000.00),
('HD04', 'NV04', 'Vô thời hạn', '2025-01-01', '2099-12-31', 20000000.00),
('HD06', 'NV06', 'Vô thời hạn', '2025-01-01', '2099-12-31', 22000000.00),
('HD07', 'NV07', '1 năm', '2025-02-01', '2026-02-01', 8000000.00),
('HD08', 'NV08', '1 năm', '2025-02-01', '2026-02-01', 9000000.00),
('HD09', 'NV09', '1 năm', '2025-02-01', '2026-02-01', 8500000.00);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `lichlamviec`
--

CREATE TABLE `lichlamviec` (
  `MALICH` varchar(10) NOT NULL,
  `MANV` varchar(10) DEFAULT NULL,
  `MACALAM` varchar(10) DEFAULT NULL,
  `NGAYLAMVIEC` date DEFAULT NULL,
  `GHICHU` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `lichlamviec`
--

INSERT INTO `lichlamviec` (`MALICH`, `MANV`, `MACALAM`, `NGAYLAMVIEC`, `GHICHU`) VALUES
('L01', 'NV07', 'C1', '2026-02-04', 'Làm tại văn phòng'),
('L02', 'NV08', 'C1', '2026-02-04', 'Trực kỹ thuật'),
('L03', 'NV09', 'C1', '2026-02-04', 'Hỗ trợ khách hàng'),
('L4', 'NV07', 'C1', '2026-03-14', NULL);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `nghiphep`
--

CREATE TABLE `nghiphep` (
  `MANGHIPHEP` varchar(10) NOT NULL,
  `MANV` varchar(10) DEFAULT NULL,
  `LOAINGHI` varchar(50) DEFAULT NULL,
  `LYDONGHI` varchar(255) DEFAULT NULL,
  `NGAYNGHI` date DEFAULT NULL,
  `NGAYLAMLAI` date DEFAULT NULL,
  `NGUOIDUYET` varchar(100) DEFAULT NULL,
  `NGAYDUYET` date DEFAULT NULL,
  `TRANGTHAI` varchar(50) DEFAULT 'Chờ duyệt',
  `LYDOTUCHOI` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `nghiphep`
--

INSERT INTO `nghiphep` (`MANGHIPHEP`, `MANV`, `LOAINGHI`, `LYDONGHI`, `NGAYNGHI`, `NGAYLAMLAI`, `NGUOIDUYET`, `NGAYDUYET`, `TRANGTHAI`, `LYDOTUCHOI`) VALUES
('NP01', 'NV07', 'Có lương', 'Ốm nhẹ', '2026-01-20', '2026-01-21', 'Nguyễn HR 1', '2026-01-19', 'Đã duyệt', NULL);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `nhanvien`
--

CREATE TABLE `nhanvien` (
  `MANV` varchar(10) NOT NULL,
  `MAPHONGBAN` varchar(10) DEFAULT NULL,
  `MACHUCVU` varchar(10) DEFAULT NULL,
  `MATRINHDO` varchar(10) DEFAULT NULL,
  `HOTEN` varchar(100) NOT NULL,
  `GIOITINH` varchar(10) DEFAULT NULL,
  `DIACHI` varchar(255) DEFAULT NULL,
  `DIENTHOAI` varchar(15) DEFAULT NULL,
  `EMAIL` varchar(100) DEFAULT NULL,
  `NGAYVAOLAM` date DEFAULT NULL,
  `SONGAYPHEP` int(11) DEFAULT 12,
  `TRANGTHAI` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `nhanvien`
--

INSERT INTO `nhanvien` (`MANV`, `MAPHONGBAN`, `MACHUCVU`, `MATRINHDO`, `HOTEN`, `GIOITINH`, `DIACHI`, `DIENTHOAI`, `EMAIL`, `NGAYVAOLAM`, `SONGAYPHEP`, `TRANGTHAI`) VALUES
('NV01', 'PB01', 'CV02', 'TD02', 'Nguyễn Hoàng Nam', 'Nam', '123 Lê Lợi, Quận 1, TP.HCM', '0901234567', 'nam.nguyen@company.com', '2022-01-15', 12, 'Đang làm việc'),
('NV02', 'PB01', 'CV02', 'TD01', 'Trần Thị Thu Thảo', 'Nữ', '456 Nguyễn Huệ, Quận 1, TP.HCM', '0912345678', 'thao.tran@company.com', '2024-02-01', 12, 'Đang làm việc'),
('NV03', 'PB01', 'CV01', 'TD01', 'Lê Văn Tùng', 'Nam', '789 CMT8, Quận 3, TP.HCM', '0923456789', 'tung.le@company.com', '2024-06-15', 12, 'Đang làm việc'),
('NV04', 'PB02', 'CV01', 'TD02', 'Phạm Minh Quang', 'Nam', '12 Hòa Bình, Quận Tân Phú, TP.HCM', '0934567890', 'quang.pham@company.com', '2022-03-20', 12, 'Đang làm việc'),
('NV05', 'PB02', 'CV01', 'TD01', 'Hoàng Bảo Ngọc', 'Nữ', '88 Cộng Hòa, Quận Tân Bình, TP.HCM', '0945678901', 'ngoc.hoang@company.com', '2023-05-10', 12, 'Đang làm việc'),
('NV06', 'PB03', 'CV01', 'TD01', 'Vũ Anh Tuấn', 'Nam', '202 Võ Văn Kiệt, Quận 5, TP.HCM', '0956789012', 'tuan.vu@company.com', '2023-08-15', 12, 'Đang làm việc'),
('NV07', 'PB02', 'CV02', 'TD01', 'Ngô Thanh Sơn', 'Nam', '15 Trần Hưng Đạo, Quận 1, TP.HCM', '0967890123', 'son.ngo@company.com', '2025-01-10', 12, 'Đang làm việc'),
('NV08', 'PB03', 'CV02', 'TD01', 'Đỗ Mỹ Linh', 'Nữ', '33 Phan Xích Long, Quận Phú Nhuận, TP.HCM', '0878901234', 'linh.do@company.com', '2025-01-20', 12, 'Đang làm việc'),
('NV09', 'PB03', 'CV02', 'TD01', 'Đặng Quốc Huy', 'Nam', '55 Quang Trung, Quận Gò Vấp, TP.HCM', '0989012345', 'huy.dang@company.com', '2025-02-01', 12, 'Đang làm việc');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `phanquyen_chitiet`
--

CREATE TABLE `phanquyen_chitiet` (
  `ROLEID` varchar(10) NOT NULL,
  `MACHUCNANG` varchar(10) NOT NULL,
  `QUYEN_XEM` tinyint(1) DEFAULT 0,
  `QUYEN_THEM` tinyint(1) DEFAULT 0,
  `QUYEN_SUA` tinyint(1) DEFAULT 0,
  `QUYEN_XOA` tinyint(1) DEFAULT 0,
  `QUYEN_DUYET` tinyint(1) DEFAULT 0 COMMENT 'Quyền duyệt (for approval workflows)',
  `QUYEN_XUAT_BC` tinyint(1) DEFAULT 0 COMMENT 'Quyền xuất báo cáo',
  `NGAY_TAO` timestamp DEFAULT CURRENT_TIMESTAMP,
  `NGAY_CAP_NHAT` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `phanquyen_chitiet`
--

INSERT INTO `phanquyen_chitiet` (`ROLEID`, `MACHUCNANG`, `QUYEN_XEM`, `QUYEN_THEM`, `QUYEN_SUA`, `QUYEN_XOA`, `QUYEN_DUYET`, `QUYEN_XUAT_BC`) VALUES
-- Admin (R1): Full access to all features
('R1', 'CN01', 1, 1, 1, 1, 1, 1),
('R1', 'CN02', 1, 1, 1, 1, 1, 1),
('R1', 'CN03', 1, 1, 1, 1, 1, 1),
('R1', 'CN04', 1, 1, 1, 1, 1, 1),
('R1', 'CN05', 1, 1, 1, 1, 1, 1),
('R1', 'CN06', 1, 1, 1, 1, 1, 1),
('R1', 'CN07', 1, 1, 1, 1, 1, 1),
('R1', 'CN08', 1, 1, 1, 1, 0, 1),
('R1', 'CN09', 1, 0, 0, 0, 0, 1),
('R1', 'CN10', 1, 1, 1, 1, 0, 1),
-- Manager (R2): Can manage team, view and approve
('R2', 'CN01', 1, 0, 1, 0, 0, 1),
('R2', 'CN02', 1, 0, 0, 0, 0, 1),
('R2', 'CN03', 1, 0, 0, 0, 0, 1),
('R2', 'CN04', 1, 0, 0, 0, 1, 0),
('R2', 'CN05', 1, 0, 0, 0, 1, 1),
('R2', 'CN06', 1, 0, 0, 0, 0, 1),
('R2', 'CN07', 1, 0, 0, 0, 0, 0),
('R2', 'CN08', 0, 0, 0, 0, 0, 0),
('R2', 'CN09', 1, 0, 0, 0, 0, 1),
('R2', 'CN10', 1, 1, 1, 0, 0, 0),
-- Employee (R3): View own data and submit requests
('R3', 'CN01', 1, 0, 0, 0, 0, 0),
('R3', 'CN02', 1, 0, 0, 0, 0, 0),
('R3', 'CN03', 1, 0, 0, 0, 0, 0),
('R3', 'CN04', 1, 1, 1, 0, 0, 0),
('R3', 'CN05', 1, 0, 0, 0, 0, 0),
('R3', 'CN06', 1, 0, 0, 0, 0, 0),
('R3', 'CN07', 0, 0, 0, 0, 0, 0),
('R3', 'CN08', 0, 0, 0, 0, 0, 0),
('R3', 'CN09', 0, 0, 0, 0, 0, 0),
('R3', 'CN10', 1, 0, 0, 0, 0, 0);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `nhom_quyen`
--

CREATE TABLE `nhom_quyen` (
  `GROUPID` varchar(10) NOT NULL,
  `GROUPNAME` varchar(50) NOT NULL,
  `MO_TA` varchar(255) DEFAULT NULL,
  `NGAY_TAO` timestamp DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Nhóm quyền để quản lý nhiều role cùng lúc';

INSERT INTO `nhom_quyen` (`GROUPID`, `GROUPNAME`, `MO_TA`) VALUES
('GR1', 'Quản trị hệ thống', 'Quản lý toàn bộ hệ thống'),
('GR2', 'Quản lý nhân sự', 'Quản lý nhân sự và lương'),
('GR3', 'Quản lý tài chính', 'Quản lý lương và báo cáo tài chính'),
('GR4', 'Nhân viên', 'Nhân viên thông thường');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `phanquyen_theo_user`
--

CREATE TABLE `phanquyen_theo_user` (
  `MANV` varchar(10) NOT NULL,
  `MACHUCNANG` varchar(10) NOT NULL,
  `QUYEN_XEM` tinyint(1) DEFAULT NULL,
  `QUYEN_THEM` tinyint(1) DEFAULT NULL,
  `QUYEN_SUA` tinyint(1) DEFAULT NULL,
  `QUYEN_XOA` tinyint(1) DEFAULT NULL,
  `QUYEN_DUYET` tinyint(1) DEFAULT NULL,
  `QUYEN_XUAT_BC` tinyint(1) DEFAULT NULL,
  `NGAY_CAP` date DEFAULT NULL,
  `NGAY_HET_HAN` date DEFAULT NULL,
  `GHI_CHU` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`MANV`,`MACHUCNANG`),
  FOREIGN KEY (`MANV`) REFERENCES `nhanvien` (`MANV`) ON DELETE CASCADE,
  FOREIGN KEY (`MACHUCNANG`) REFERENCES `chucnang` (`MACHUCNANG`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Phân quyền đặc biệt theo từng user (ghi đè role)';

-- --------------------------------------------------------
--

CREATE TABLE `phieudanhgia` (
  `MAPHIEU` varchar(10) NOT NULL,
  `MANV` varchar(10) DEFAULT NULL,
  `MADOT` varchar(10) DEFAULT NULL,
  `MATIEUCHI` varchar(10) DEFAULT NULL,
  `TONGDIEM` int(11) DEFAULT NULL,
  `NHANXET` varchar(255) DEFAULT NULL,
  `QUYETDINH` varchar(100) DEFAULT NULL,
  `NGAYDANHGIA` date DEFAULT NULL,
  `TRANGTHAI_DUYET` varchar(50) DEFAULT 'Chờ duyệt',
  `LOAIQUYETDINH` varchar(50) DEFAULT 'Không có'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `phieudanhgia`
--

INSERT INTO `phieudanhgia` (`MAPHIEU`, `MANV`, `MADOT`, `MATIEUCHI`, `TONGDIEM`, `NHANXET`, `QUYETDINH`, `NGAYDANHGIA`, `TRANGTHAI_DUYET`, `LOAIQUYETDINH`) VALUES
('DG01', 'NV07', 'Q1-2026', 'TC01', 90, 'Nhiệt tình, hoàn thành tốt', 'Giữ nguyên', '2026-01-31', 'Đã duyệt', 'Thưởng'),
('DG02', 'NV04', 'Q1-2026', 'TC03', 100, 'Lãnh đạo xuất sắc', 'Khen thưởng', '2026-01-31', 'Chờ duyệt', 'Tăng lương'),
('DG03', 'NV01', 'Q4-2024', 'TC01', 95, 'Xuất sắc', 'Tăng lương 10%', '2024-12-31', 'Chờ duyệt', 'Tăng lương'),
('DG04', 'NV02', 'Q4-2024', 'TC02', 88, 'Phối hợp tốt', 'Thưởng quý', '2024-12-31', 'Đã duyệt', 'Thưởng'),
('DG06', 'NV07', 'Q4-2024', 'TC01', 75, 'Đạt yêu cầu', 'Giữ nguyên', '2024-12-31', 'Chờ duyệt', 'Không có'),
('DG07', 'NV08', 'Q4-2024', 'TC02', 45, 'Không đạt KPI', 'Trừ lương tháng', '2024-12-31', 'Chờ duyệt', 'Trừ lương');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `phongban`
--

CREATE TABLE `phongban` (
  `MAPHONGBAN` varchar(10) NOT NULL,
  `TENPHONGBAN` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `phongban`
--

INSERT INTO `phongban` (`MAPHONGBAN`, `TENPHONGBAN`) VALUES
('PB01', 'Nhân sự'),
('PB02', 'Kỹ thuật'),
('PB03', 'Kinh doanh');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `role`
--

CREATE TABLE `role` (
  `ROLEID` varchar(10) NOT NULL,
  `ROLENAME` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `role`
--

INSERT INTO `role` (`ROLEID`, `ROLENAME`) VALUES
('R1', 'Admin'),
('R2', 'Manager'),
('R3', 'Employee');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `taikhoan`
--

CREATE TABLE `taikhoan` (
  `MANV` varchar(10) NOT NULL,
  `ROLEID` varchar(10) DEFAULT NULL,
  `PASSWORD` varchar(255) NOT NULL DEFAULT '123',
  `STATUS` int(11) DEFAULT 1 COMMENT '1: Hoạt động, 0: Bị khóa'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `taikhoan`
--

INSERT INTO `taikhoan` (`MANV`, `ROLEID`, `PASSWORD`, `STATUS`) VALUES
('NV01', 'R1', '$2a$10$k5pCrw5eJ/CFfMvijiPOHO29fMN7P3tzKfVa2Bd7Ec1FZEem1fd2C', 1),
('NV02', 'R1', '123', 1),
('NV03', 'R3', '$2a$10$vvQpSR6sVF32IEktxEgiR.fNDqA.TI.XSGNRZWGubfdatV.tq7qbm', 1),
('NV04', 'R2', '$2a$10$i/S66NPy33bqOFvgd30KZegwRMpjPSS80VR2Z6HjeH/MxbAn18Ceu', 1),
('NV05', 'R3', '$2a$10$d9pyZOH0FKQroXOZPLl/Tuzp63ajwa4CDY7HWEP1/zPqPDUL4WD0y', 1),
('NV06', 'R3', '$2a$10$K0UCZyGB7rqSpdx8EMNVZuuEsKevFzEfZ0/SRNmFMF.nFaTwoLRCK', 1),
('NV07', 'R3', '$2a$10$FuMgt4oA0qaGGh3WnT4LYekMMDCCN7122w.1BsfA33L5vNapzVJ5e', 1),
('NV08', 'R3', '$2a$10$sU0xCtR0JyUsGryfeFcUMu9aFUPdaGtZriL9kBZSQG82wtmqWfwXm', 1),
('NV09', 'R3', '$2a$10$FgpraoVoMbSA.Lo9i2L6z.Z9PKFR.Y65fhRLYVJ2E.CvqIMhPmnpW', 1);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `tieuchidanhgia`
--

CREATE TABLE `tieuchidanhgia` (
  `MATIEUCHI` varchar(10) NOT NULL,
  `TENTIEUCHI` varchar(100) DEFAULT NULL,
  `DIEM` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `tieuchidanhgia`
--

INSERT INTO `tieuchidanhgia` (`MATIEUCHI`, `TENTIEUCHI`, `DIEM`) VALUES
('TC01', 'Năng suất làm việc', 10),
('TC02', 'Thái độ phối hợp', 10),
('TC03', 'Kỹ năng chuyên môn', 10);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `trinhdo`
--

CREATE TABLE `trinhdo` (
  `MATRINHDO` varchar(10) NOT NULL,
  `TRINHDO` varchar(50) NOT NULL,
  `HESOTRINHDO` decimal(5,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `trinhdo`
--

INSERT INTO `trinhdo` (`MATRINHDO`, `TRINHDO`, `HESOTRINHDO`) VALUES
('TD01', 'Đại học', 1.00),
('TD02', 'Thạc sĩ', 1.50),
('TD03', 'Tiến sĩ', 2.00);

--
-- Chỉ mục cho các bảng đã đổ
--

--
-- Chỉ mục cho bảng `bangluong`
--
ALTER TABLE `bangluong`
  ADD PRIMARY KEY (`MALUONG`),
  ADD KEY `fk_bl_nv` (`MANV`);

--
-- Chỉ mục cho bảng `calam`
--
ALTER TABLE `calam`
  ADD PRIMARY KEY (`MACALAM`);

--
-- Chỉ mục cho bảng `chamcong`
--
ALTER TABLE `chamcong`
  ADD PRIMARY KEY (`MACHAMCONG`),
  ADD KEY `fk_cc_nv` (`MANV`);

--
-- Chỉ mục cho bảng `chitiet_luong_biendong`
--
ALTER TABLE `chitiet_luong_biendong`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `fk_ct_bl` (`MALUONG`);

--
-- Chỉ mục cho bảng `chucnang`
--
ALTER TABLE `chucnang`
  ADD PRIMARY KEY (`MACHUCNANG`);

--
-- Chỉ mục cho bảng `chucvu`
--
ALTER TABLE `chucvu`
  ADD PRIMARY KEY (`MACHUCVU`);

--
-- Chỉ mục cho bảng `danhmuc_khautru`
--
ALTER TABLE `danhmuc_khautru`
  ADD PRIMARY KEY (`MAKHAUTRU`);

--
-- Chỉ mục cho bảng `danhmuc_phucap`
--
ALTER TABLE `danhmuc_phucap`
  ADD PRIMARY KEY (`MAPHUCAP`);

--
-- Chỉ mục cho bảng `dotdanhgia`
--
ALTER TABLE `dotdanhgia`
  ADD PRIMARY KEY (`MADOT`);

--
-- Chỉ mục cho bảng `hopdong`
--
ALTER TABLE `hopdong`
  ADD PRIMARY KEY (`MAHOPDONG`),
  ADD KEY `fk_hd_nv` (`MANV`);

--
-- Chỉ mục cho bảng `lichlamviec`
--
ALTER TABLE `lichlamviec`
  ADD PRIMARY KEY (`MALICH`),
  ADD KEY `fk_llv_nv` (`MANV`),
  ADD KEY `fk_llv_cl` (`MACALAM`);

--
-- Chỉ mục cho bảng `nghiphep`
--
ALTER TABLE `nghiphep`
  ADD PRIMARY KEY (`MANGHIPHEP`),
  ADD KEY `fk_np_nv` (`MANV`);

--
-- Chỉ mục cho bảng `nhanvien`
--
ALTER TABLE `nhanvien`
  ADD PRIMARY KEY (`MANV`),
  ADD KEY `fk_nv_pb` (`MAPHONGBAN`),
  ADD KEY `fk_nv_cv` (`MACHUCVU`),
  ADD KEY `fk_nv_td` (`MATRINHDO`);

--
-- Chỉ mục cho bảng `phanquyen_chitiet`
--
ALTER TABLE `phanquyen_chitiet`
  ADD PRIMARY KEY (`ROLEID`,`MACHUCNANG`),
  ADD KEY `fk_pq_cn` (`MACHUCNANG`);

--
-- Chỉ mục cho bảng `phieudanhgia`
--
ALTER TABLE `phieudanhgia`
  ADD PRIMARY KEY (`MAPHIEU`),
  ADD KEY `fk_pdg_nv` (`MANV`),
  ADD KEY `fk_pdg_tc` (`MATIEUCHI`);

--
-- Chỉ mục cho bảng `phongban`
--
ALTER TABLE `phongban`
  ADD PRIMARY KEY (`MAPHONGBAN`);

--
-- Chỉ mục cho bảng `role`
--
ALTER TABLE `role`
  ADD PRIMARY KEY (`ROLEID`);

--
-- Chỉ mục cho bảng `taikhoan`
--
ALTER TABLE `taikhoan`
  ADD PRIMARY KEY (`MANV`);

--
-- Chỉ mục cho bảng `tieuchidanhgia`
--
ALTER TABLE `tieuchidanhgia`
  ADD PRIMARY KEY (`MATIEUCHI`);

--
-- Chỉ mục cho bảng `trinhdo`
--
ALTER TABLE `trinhdo`
  ADD PRIMARY KEY (`MATRINHDO`);

--
-- Chỉ mục cho bảng `nhom_quyen`
--
ALTER TABLE `nhom_quyen`
  ADD PRIMARY KEY (`GROUPID`);

--
-- AUTO_INCREMENT cho các bảng đã đổ
--

--
-- AUTO_INCREMENT cho bảng `chitiet_luong_biendong`
--
ALTER TABLE `chitiet_luong_biendong`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT cho bảng `danhmuc_khautru`
--
ALTER TABLE `danhmuc_khautru`
  MODIFY `MAKHAUTRU` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT cho bảng `danhmuc_phucap`
--
ALTER TABLE `danhmuc_phucap`
  MODIFY `MAPHUCAP` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Các ràng buộc cho các bảng đã đổ
--

--
-- Các ràng buộc cho bảng `bangluong`
--
ALTER TABLE `bangluong`
  ADD CONSTRAINT `fk_bl_nv` FOREIGN KEY (`MANV`) REFERENCES `nhanvien` (`MANV`);

--
-- Các ràng buộc cho bảng `chamcong`
--
ALTER TABLE `chamcong`
  ADD CONSTRAINT `fk_cc_nv` FOREIGN KEY (`MANV`) REFERENCES `nhanvien` (`MANV`);

--
-- Các ràng buộc cho bảng `chitiet_luong_biendong`
--
ALTER TABLE `chitiet_luong_biendong`
  ADD CONSTRAINT `fk_ct_bl` FOREIGN KEY (`MALUONG`) REFERENCES `bangluong` (`MALUONG`);

--
-- Các ràng buộc cho bảng `hopdong`
--
ALTER TABLE `hopdong`
  ADD CONSTRAINT `fk_hd_nv` FOREIGN KEY (`MANV`) REFERENCES `nhanvien` (`MANV`);

--
-- Các ràng buộc cho bảng `lichlamviec`
--
ALTER TABLE `lichlamviec`
  ADD CONSTRAINT `fk_llv_cl` FOREIGN KEY (`MACALAM`) REFERENCES `calam` (`MACALAM`),
  ADD CONSTRAINT `fk_llv_nv` FOREIGN KEY (`MANV`) REFERENCES `nhanvien` (`MANV`);

--
-- Các ràng buộc cho bảng `nghiphep`
--
ALTER TABLE `nghiphep`
  ADD CONSTRAINT `fk_np_nv` FOREIGN KEY (`MANV`) REFERENCES `nhanvien` (`MANV`);

--
-- Các ràng buộc cho bảng `nhanvien`
--
ALTER TABLE `nhanvien`
  ADD CONSTRAINT `fk_nv_cv` FOREIGN KEY (`MACHUCVU`) REFERENCES `chucvu` (`MACHUCVU`),
  ADD CONSTRAINT `fk_nv_pb` FOREIGN KEY (`MAPHONGBAN`) REFERENCES `phongban` (`MAPHONGBAN`),
  ADD CONSTRAINT `fk_nv_td` FOREIGN KEY (`MATRINHDO`) REFERENCES `trinhdo` (`MATRINHDO`);

--
-- Các ràng buộc cho bảng `phanquyen_chitiet`
--
ALTER TABLE `phanquyen_chitiet`
  ADD CONSTRAINT `fk_pq_cn` FOREIGN KEY (`MACHUCNANG`) REFERENCES `chucnang` (`MACHUCNANG`),
  ADD CONSTRAINT `fk_pq_role` FOREIGN KEY (`ROLEID`) REFERENCES `role` (`ROLEID`);

--
-- Các ràng buộc cho bảng `phieudanhgia`
--
ALTER TABLE `phieudanhgia`
  ADD CONSTRAINT `fk_pdg_nv` FOREIGN KEY (`MANV`) REFERENCES `nhanvien` (`MANV`),
  ADD CONSTRAINT `fk_pdg_tc` FOREIGN KEY (`MATIEUCHI`) REFERENCES `tieuchidanhgia` (`MATIEUCHI`);

--
-- Các ràng buộc cho bảng `taikhoan`
--
ALTER TABLE `taikhoan`
  ADD CONSTRAINT `fk_tk_nv` FOREIGN KEY (`MANV`) REFERENCES `nhanvien` (`MANV`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
