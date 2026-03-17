-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Mar 17, 2026 at 09:26 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `hrm_system`
--

-- --------------------------------------------------------

--
-- Table structure for table `bangluong`
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
-- Dumping data for table `bangluong`
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
-- Table structure for table `calam`
--

CREATE TABLE `calam` (
  `MACALAM` varchar(10) NOT NULL,
  `TENCALAM` varchar(50) DEFAULT NULL,
  `GIOVAOCA` time DEFAULT NULL,
  `GIOTANCA` time DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `calam`
--

INSERT INTO `calam` (`MACALAM`, `TENCALAM`, `GIOVAOCA`, `GIOTANCA`) VALUES
('C1', 'Hành chính', '08:00:00', '17:00:00'),
('C2', 'Ca sáng', '06:00:00', '15:00:00'),
('OFF', 'Ngày OFF', NULL, NULL),
('C3', 'Ca chiều', '14:00:00', '22:00:00'),
('C5', 'Ca gãy sáng', '08:00:00', '12:00:00'),
('C6', 'Ca gãy chiều', '16:00:00', '20:00:00'),
('C7', 'Ca tăng cường', '17:00:00', '21:00:00'),
('C4', 'Ca gãy sáng', '08:00:00', '12:00:00'),
('C5', 'Ca gãy chiều', '13:00:00', '17:00:00'),
('C6', 'Ca tăng cường', '18:00:00', '22:00:00');

-- --------------------------------------------------------

--
-- Table structure for table `chamcong`
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
-- Dumping data for table `chamcong`
--

INSERT INTO `chamcong` (`MACHAMCONG`, `MANV`, `NGAYLAMVIEC`, `SOGIOLAM`, `CHECKIN`, `CHECKOUT`, `TRANGTHAI`) VALUES
('CC01', 'NV07', '2026-01-27', 9, '08:00:00', '17:00:00', '1'),
('CC02', 'NV08', '2026-01-27', 8, '07:55:00', '17:05:00', '1'),
('CC03', 'NV09', '2026-01-27', 7.5, '08:30:00', '17:00:00', '0'),
('CC04', 'NV07', '2026-03-06', 8, '07:55:00', '17:00:00', '1'),
('CC05', 'NV07', '2026-03-07', 7.5, '08:30:00', '17:00:00', '0'),
('CC06', 'NV07', '2026-03-09', 8, '05:50:00', '15:00:00', '1'),
('CC07', 'NV07', '2026-03-10', 7.5, '06:30:00', '15:00:00', '0'),
('CC08', 'NV07', '2026-03-11', 8, '13:55:00', '22:00:00', '1'),
('CC09', 'NV07', '2026-03-12', 7.5, '14:30:00', '22:00:00', '0'),
('CC10', 'NV07', '2026-03-13', 4, '07:50:00', '12:00:00', '1'),
('CC11', 'NV07', '2026-03-14', 2.5, '16:30:00', '20:00:00', '0'),
('CC12', 'NV07', '2026-03-15', 5, '16:55:00', '21:00:00', '1'),
('CC13', 'NV07', '2026-03-16', 7, '09:00:00', '17:00:00', '0');

-- --------------------------------------------------------

--
-- Table structure for table `chitiet_luong_biendong`
--

CREATE TABLE `chitiet_luong_biendong` (
  `ID` int(11) NOT NULL,
  `MALUONG` varchar(10) DEFAULT NULL,
  `TENKHOANTIEN` varchar(100) DEFAULT NULL,
  `SOTIEN` decimal(18,2) DEFAULT NULL,
  `LOAI` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `chitiet_luong_biendong`
--

INSERT INTO `chitiet_luong_biendong` (`ID`, `MALUONG`, `TENKHOANTIEN`, `SOTIEN`, `LOAI`) VALUES
(1, 'ML01', 'Phụ cấp ăn trưa', 1000000.00, 'CONG'),
(2, 'ML01', 'Bảo hiểm xã hội', 500000.00, 'TRU');

-- --------------------------------------------------------

--
-- Table structure for table `chucnang`
--

CREATE TABLE `chucnang` (
  `MACHUCNANG` varchar(10) NOT NULL,
  `TENCHUCNANG` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `chucnang`
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
-- Table structure for table `chucvu`
--

CREATE TABLE `chucvu` (
  `MACHUCVU` varchar(10) NOT NULL,
  `TENVITRI` varchar(100) NOT NULL,
  `PHUCAPCHUCVU` decimal(18,2) DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `chucvu`
--

INSERT INTO `chucvu` (`MACHUCVU`, `TENVITRI`, `PHUCAPCHUCVU`) VALUES
('CV01', 'Trưởng phòng', 3000000.00),
('CV02', 'Nhân viên', 1000000.00),
('CV03', 'Nhân sự', 3000000.00);

-- --------------------------------------------------------

--
-- Table structure for table `danhmuc_khautru`
--

CREATE TABLE `danhmuc_khautru` (
  `MAKHAUTRU` int(11) NOT NULL,
  `TENKHAUTRU` varchar(100) DEFAULT NULL,
  `SOTIEN_MACDINH` decimal(18,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `danhmuc_khautru`
--

INSERT INTO `danhmuc_khautru` (`MAKHAUTRU`, `TENKHAUTRU`, `SOTIEN_MACDINH`) VALUES
(1, 'Bảo hiểm xã hội', 500000.00),
(2, 'Phí công đoàn', 100000.00),
(3, 'Phí gửi xe', 100000.00);

-- --------------------------------------------------------

--
-- Table structure for table `danhmuc_phucap`
--

CREATE TABLE `danhmuc_phucap` (
  `MAPHUCAP` int(11) NOT NULL,
  `TENPHUCAP` varchar(100) DEFAULT NULL,
  `SOTIEN_MACDINH` decimal(18,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `danhmuc_phucap`
--

INSERT INTO `danhmuc_phucap` (`MAPHUCAP`, `TENPHUCAP`, `SOTIEN_MACDINH`) VALUES
(1, 'Ăn trưa', 1000000.00),
(2, 'Xăng xe', 500000.00),
(3, 'Độc hại', 300000.00);

-- --------------------------------------------------------

--
-- Table structure for table `dotdanhgia`
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
-- Dumping data for table `dotdanhgia`
--

INSERT INTO `dotdanhgia` (`MADOT`, `TENDOT`, `KYKY`, `NAM`, `NGUOIDANHGIA`, `TRANGTHAI`) VALUES
('Q1-2024', 'Đánh giá Quý 1 năm 2024', 'Q1', 2024, 'Trần Thị B', 'Đã đóng'),
('Q1-2026', 'Đánh giá Quý 1 năm 2026', 'Q1', 2026, 'Trần Thị B', 'Đang mở'),
('Q2-2024', 'Đánh giá Quý 2 năm 2024', 'Q2', 2024, 'Trần Thị B', 'Đã đóng'),
('Q3-2024', 'Đánh giá Quý 3 năm 2024', 'Q3', 2024, 'Trần Thị B', 'Đã đóng'),
('Q4-2024', 'Đánh giá Quý 4 năm 2024', 'Q4', 2024, 'Trần Thị B', 'Đã đóng');

-- --------------------------------------------------------

--
-- Table structure for table `hopdong`
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
-- Dumping data for table `hopdong`
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
-- Table structure for table `lichlamviec`
--

CREATE TABLE `lichlamviec` (
  `MALICH` varchar(10) NOT NULL,
  `MANV` varchar(10) DEFAULT NULL,
  `MACALAM` varchar(10) DEFAULT NULL,
  `NGAYLAMVIEC` date DEFAULT NULL,
  `GHICHU` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `lichlamviec`
--

INSERT INTO `lichlamviec` (`MALICH`, `MANV`, `MACALAM`, `NGAYLAMVIEC`, `GHICHU`) VALUES
('L01', 'NV07', 'C1', '2026-02-04', 'Làm tại văn phòng'),
('L02', 'NV08', 'C1', '2026-02-04', 'Trực kỹ thuật'),
('L03', 'NV09', 'C1', '2026-02-04', 'Hỗ trợ khách hàng'),
('L4', 'NV07', 'C1', '2026-03-14', NULL),
('L05', 'NV07', 'C1', '2026-03-17', 'Ca hành chính'),
('L06', 'NV07', 'C2', '2026-03-18', 'Ca sáng'),
('L07', 'NV07', 'C3', '2026-03-19', 'Ca chiều'),
('L08', 'NV07', 'C4', '2026-03-20', 'Ca gãy sáng'),
('L09', 'NV07', 'C5', '2026-03-21', 'Ca gãy chiều'),
('L10', 'NV07', 'C6', '2026-03-23', 'Ca tăng cường'),
('L11', 'NV07', 'C1', '2026-03-24', 'Ca hành chính'),
('L12', 'NV07', 'C2', '2026-03-25', 'Ca sáng'),
('L13', 'NV07', 'C3', '2026-03-26', 'Ca chiều'),
('L14', 'NV07', 'OFF', '2026-03-27', 'Nghỉ bù'),
('L15', 'NV08', 'C1', '2026-03-17', 'Trực kỹ thuật'),
('L16', 'NV08', 'C1', '2026-03-18', 'Trực kỹ thuật'),
('L17', 'NV08', 'C2', '2026-03-19', 'Hỗ trợ dự án'),
('L18', 'NV08', 'C2', '2026-03-20', 'Hỗ trợ dự án'),
('L19', 'NV08', 'C3', '2026-03-21', 'Trực ca chiều'),
('L20', 'NV09', 'C3', '2026-03-17', 'Kinh doanh'),
('L21', 'NV09', 'C3', '2026-03-18', 'Kinh doanh'),
('L22', 'NV09', 'C6', '2026-03-19', 'Tăng cường sự kiện'),
('L23', 'NV09', 'C6', '2026-03-20', 'Tăng cường sự kiện'),
('L24', 'NV09', 'C1', '2026-03-21', 'Hành chính'),
('L25', 'NV07', 'C1', '2026-03-06', 'Làm việc hành chính'),
('L26', 'NV07', 'C1', '2026-03-07', 'Làm việc hành chính'),
('L27', 'NV07', 'C2', '2026-03-09', 'Trực ca sáng'),
('L28', 'NV07', 'C2', '2026-03-10', 'Trực ca sáng'),
('L29', 'NV07', 'C3', '2026-03-11', 'Trực ca chiều'),
('L30', 'NV07', 'C3', '2026-03-12', 'Trực ca chiều'),
('L31', 'NV07', 'C4', '2026-03-13', 'Ca gãy sáng'),
('L32', 'NV07', 'C5', '2026-03-14', 'Ca gãy chiều'),
('L33', 'NV07', 'C6', '2026-03-15', 'Ca tăng cường'),
('L34', 'NV07', 'C1', '2026-03-16', 'Làm việc hành chính');

-- --------------------------------------------------------

--
-- Table structure for table `nghiphep`
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
-- Dumping data for table `nghiphep`
--

INSERT INTO `nghiphep` (`MANGHIPHEP`, `MANV`, `LOAINGHI`, `LYDONGHI`, `NGAYNGHI`, `NGAYLAMLAI`, `NGUOIDUYET`, `NGAYDUYET`, `TRANGTHAI`, `LYDOTUCHOI`) VALUES
('NP01', 'NV07', 'Có lương', 'Ốm nhẹ', '2026-01-20', '2026-01-21', 'Nguyễn HR 1', '2026-01-19', 'Đã duyệt', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `nhanvien`
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
-- Dumping data for table `nhanvien`
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
('NV09', 'PB03', 'CV02', 'TD01', 'Đặng Quốc Huy', 'Nam', '55 Quang Trung, Quận Gò Vấp, TP.HCM', '0989012345', 'huy.dang@company.com', '2025-02-01', 12, 'Đang làm việc'),
('NV10', 'PB04', 'CV02', 'TD01', 'Lý Hải Đăng', 'Nam', 'Quận 7, TP.HCM', '0901112221', 'dang.ly@company.com', '2026-03-15', 12, 'Đang làm việc'),
('NV11', 'PB04', 'CV02', 'TD01', 'Bùi Thị Xuân', 'Nữ', 'Quận 4, TP.HCM', '0901112222', 'xuan.bui@company.com', '2026-03-15', 12, 'Đang làm việc'),
('NV12', 'PB04', 'CV02', 'TD01', 'Ngô Kiến Huy', 'Nam', 'Quận 10, TP.HCM', '0901112223', 'huy.ngo@company.com', '2026-03-15', 12, 'Đang làm việc'),
('NV13', 'PB04', 'CV02', 'TD02', 'Võ Hoàng Yến', 'Nữ', 'Quận 3, TP.HCM', '0901112224', 'yen.vo@company.com', '2026-03-15', 12, 'Đang làm việc'),
('NV14', 'PB04', 'CV02', 'TD01', 'Trương Thế Vinh', 'Nam', 'Quận 5, TP.HCM', '0901112225', 'vinh.truong@company.com', '2026-03-15', 12, 'Đang làm việc'),
('NV15', 'PB01', 'CV02', 'TD01', 'Lê Khánh', 'Nữ', 'Quận Bình Thạnh, TP.HCM', '0901112226', 'khanh.le@company.com', '2026-03-15', 12, 'Đang làm việc'),
('NV16', 'PB01', 'CV02', 'TD01', 'Nguyễn Phi Hùng', 'Nam', 'Quận 12, TP.HCM', '0901112227', 'hung.nguyen@company.com', '2026-03-15', 12, 'Đang làm việc'),
('NV17', 'PB01', 'CV02', 'TD02', 'Phan Mạnh Quỳnh', 'Nam', 'Quận Tân Bình, TP.HCM', '0901112228', 'quynh.phan@company.com', '2026-03-15', 12, 'Đang làm việc'),
('NV18', 'PB02', 'CV02', 'TD01', 'Hòa Minzy', 'Nữ', 'Quận Gò Vấp, TP.HCM', '0901112229', 'hoa.min@company.com', '2026-03-15', 12, 'Đang làm việc'),
('NV19', 'PB02', 'CV02', 'TD01', 'Đức Phúc', 'Nam', 'Quận 7, TP.HCM', '0901112230', 'phuc.duc@company.com', '2026-03-15', 12, 'Đang làm việc'),
('NV20', 'PB02', 'CV02', 'TD01', 'Erik Trần', 'Nam', 'Quận 1, TP.HCM', '0901112231', 'erik.tran@company.com', '2026-03-15', 12, 'Đang làm việc'),
('NV21', 'PB03', 'CV02', 'TD01', 'Tóc Tiên', 'Nữ', 'Quận 2, TP.HCM', '0901112232', 'tien.toc@company.com', '2026-03-15', 12, 'Đang làm việc'),
('NV22', 'PB03', 'CV02', 'TD01', 'Soobin Hoàng Sơn', 'Nam', 'Quận Phú Nhuận, TP.HCM', '0901112233', 'soobin.hoang@company.com', '2026-03-15', 12, 'Đang làm việc'),
('NV23', 'PB03', 'CV02', 'TD01', 'Bích Phương', 'Nữ', 'Quận Bình Tân, TP.HCM', '0901112234', 'phuong.bich@company.com', '2026-03-15', 12, 'Đang làm việc'),
('NV24', 'PB04', 'CV03', 'TD01', 'Hồ Ngọc Hà', 'Nữ', 'Quận 7, TP.HCM', '0901112235', 'ha.ho@company.com', '2026-03-15', 12, 'Đang làm việc');

-- --------------------------------------------------------

--
-- Table structure for table `nhom_quyen`
--

CREATE TABLE `nhom_quyen` (
  `GROUPID` varchar(10) NOT NULL,
  `GROUPNAME` varchar(50) NOT NULL,
  `MO_TA` varchar(255) DEFAULT NULL,
  `NGAY_TAO` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Nhóm quyền để quản lý nhiều role cùng lúc';

--
-- Dumping data for table `nhom_quyen`
--

INSERT INTO `nhom_quyen` (`GROUPID`, `GROUPNAME`, `MO_TA`, `NGAY_TAO`) VALUES
('GR1', 'Quản trị hệ thống', 'Quản lý toàn bộ hệ thống', '2026-03-17 08:02:29'),
('GR2', 'Quản lý nhân sự', 'Quản lý nhân sự và lương', '2026-03-17 08:02:29'),
('GR3', 'Quản lý tài chính', 'Quản lý lương và báo cáo tài chính', '2026-03-17 08:02:29'),
('GR4', 'Nhân viên', 'Nhân viên thông thường', '2026-03-17 08:02:29');

-- --------------------------------------------------------

--
-- Table structure for table `phanquyen_chitiet`
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
  `NGAY_TAO` timestamp NOT NULL DEFAULT current_timestamp(),
  `NGAY_CAP_NHAT` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `phanquyen_chitiet`
--

INSERT INTO `phanquyen_chitiet` (`ROLEID`, `MACHUCNANG`, `QUYEN_XEM`, `QUYEN_THEM`, `QUYEN_SUA`, `QUYEN_XOA`, `QUYEN_DUYET`, `QUYEN_XUAT_BC`, `NGAY_TAO`, `NGAY_CAP_NHAT`) VALUES
('R1', 'CN01', 1, 1, 1, 1, 1, 1, '2026-03-17 08:02:29', '2026-03-17 08:02:29'),
('R1', 'CN02', 1, 1, 1, 1, 1, 1, '2026-03-17 08:02:29', '2026-03-17 08:02:29'),
('R1', 'CN03', 1, 1, 1, 1, 1, 1, '2026-03-17 08:02:29', '2026-03-17 08:02:29'),
('R1', 'CN04', 1, 1, 1, 1, 1, 1, '2026-03-17 08:02:29', '2026-03-17 08:02:29'),
('R1', 'CN05', 1, 1, 1, 1, 1, 1, '2026-03-17 08:02:29', '2026-03-17 08:02:29'),
('R1', 'CN06', 1, 1, 1, 1, 1, 1, '2026-03-17 08:02:29', '2026-03-17 08:02:29'),
('R1', 'CN07', 1, 1, 1, 1, 1, 1, '2026-03-17 08:02:29', '2026-03-17 08:02:29'),
('R1', 'CN08', 1, 1, 1, 1, 0, 1, '2026-03-17 08:02:29', '2026-03-17 08:02:29'),
('R1', 'CN09', 1, 0, 0, 0, 0, 1, '2026-03-17 08:02:29', '2026-03-17 08:02:29'),
('R1', 'CN10', 1, 1, 1, 1, 0, 1, '2026-03-17 08:02:29', '2026-03-17 08:02:29'),
('R2', 'CN01', 1, 0, 1, 0, 0, 1, '2026-03-17 08:02:29', '2026-03-17 08:02:29'),
('R2', 'CN02', 1, 0, 0, 0, 0, 1, '2026-03-17 08:02:29', '2026-03-17 08:02:29'),
('R2', 'CN03', 1, 0, 0, 0, 0, 1, '2026-03-17 08:02:29', '2026-03-17 08:02:29'),
('R2', 'CN04', 1, 0, 0, 0, 1, 0, '2026-03-17 08:02:29', '2026-03-17 08:02:29'),
('R2', 'CN05', 1, 0, 0, 0, 1, 1, '2026-03-17 08:02:29', '2026-03-17 08:02:29'),
('R2', 'CN06', 1, 0, 0, 0, 0, 1, '2026-03-17 08:02:29', '2026-03-17 08:02:29'),
('R2', 'CN07', 1, 0, 0, 0, 0, 0, '2026-03-17 08:02:29', '2026-03-17 08:02:29'),
('R2', 'CN08', 0, 0, 0, 0, 0, 0, '2026-03-17 08:02:29', '2026-03-17 08:02:29'),
('R2', 'CN09', 1, 0, 0, 0, 0, 1, '2026-03-17 08:02:29', '2026-03-17 08:02:29'),
('R2', 'CN10', 1, 1, 1, 0, 0, 0, '2026-03-17 08:02:29', '2026-03-17 08:02:29'),
('R3', 'CN01', 1, 0, 0, 0, 0, 0, '2026-03-17 08:02:29', '2026-03-17 08:02:29'),
('R3', 'CN02', 1, 0, 0, 0, 0, 0, '2026-03-17 08:02:29', '2026-03-17 08:02:29'),
('R3', 'CN03', 1, 0, 0, 0, 0, 0, '2026-03-17 08:02:29', '2026-03-17 08:02:29'),
('R3', 'CN04', 1, 1, 1, 0, 0, 0, '2026-03-17 08:02:29', '2026-03-17 08:02:29'),
('R3', 'CN05', 1, 0, 0, 0, 0, 0, '2026-03-17 08:02:29', '2026-03-17 08:02:29'),
('R3', 'CN06', 1, 0, 0, 0, 0, 0, '2026-03-17 08:02:29', '2026-03-17 08:02:29'),
('R3', 'CN07', 0, 0, 0, 0, 0, 0, '2026-03-17 08:02:29', '2026-03-17 08:02:29'),
('R3', 'CN08', 0, 0, 0, 0, 0, 0, '2026-03-17 08:02:29', '2026-03-17 08:02:29'),
('R3', 'CN09', 0, 0, 0, 0, 0, 0, '2026-03-17 08:02:29', '2026-03-17 08:02:29'),
('R3', 'CN10', 1, 0, 0, 0, 0, 0, '2026-03-17 08:02:29', '2026-03-17 08:02:29');

-- --------------------------------------------------------

--
-- Table structure for table `phieudanhgia`
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
-- Dumping data for table `phieudanhgia`
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
-- Table structure for table `phongban`
--

CREATE TABLE `phongban` (
  `MAPHONGBAN` varchar(10) NOT NULL,
  `TENPHONGBAN` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `phongban`
--

INSERT INTO `phongban` (`MAPHONGBAN`, `TENPHONGBAN`) VALUES
('PB01', 'Nhân sự'),
('PB02', 'Kỹ thuật'),
('PB03', 'Kinh doanh'),
('PB04', 'Kế toán - Tài chính');

-- --------------------------------------------------------

--
-- Table structure for table `role`
--

CREATE TABLE `role` (
  `ROLEID` varchar(10) NOT NULL,
  `ROLENAME` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `role`
--

INSERT INTO `role` (`ROLEID`, `ROLENAME`) VALUES
('R1', 'Admin'),
('R2', 'Manager'),
('R3', 'Employee');

-- --------------------------------------------------------

--
-- Table structure for table `taikhoan`
--

CREATE TABLE `taikhoan` (
  `MANV` varchar(10) NOT NULL,
  `ROLEID` varchar(10) DEFAULT NULL,
  `PASSWORD` varchar(255) NOT NULL DEFAULT '123',
  `STATUS` int(11) DEFAULT 1 COMMENT '1: Hoạt động, 0: Bị khóa'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `taikhoan`
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
-- Table structure for table `tieuchidanhgia`
--

CREATE TABLE `tieuchidanhgia` (
  `MATIEUCHI` varchar(10) NOT NULL,
  `TENTIEUCHI` varchar(100) DEFAULT NULL,
  `DIEM` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tieuchidanhgia`
--

INSERT INTO `tieuchidanhgia` (`MATIEUCHI`, `TENTIEUCHI`, `DIEM`) VALUES
('TC01', 'Năng suất làm việc', 10),
('TC02', 'Thái độ phối hợp', 10),
('TC03', 'Kỹ năng chuyên môn', 10),
('TC04', 'Tuân thủ kỷ luật lao động', 10),
('TC05', 'Sáng tạo và cải tiến', 10),
('TC06', 'Kỹ năng giải quyết vấn đề', 10),
('TC07', 'Khả năng làm việc nhóm', 10),
('TC08', 'Khả năng chịu áp lực công việc', 10),
('TC09', 'Giao tiếp và trình bày', 10),
('TC10', 'Mức độ gắn bó với công ty', 10);

-- --------------------------------------------------------

--
-- Table structure for table `trinhdo`
--

CREATE TABLE `trinhdo` (
  `MATRINHDO` varchar(10) NOT NULL,
  `TRINHDO` varchar(50) NOT NULL,
  `HESOTRINHDO` decimal(5,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `trinhdo`
--

INSERT INTO `trinhdo` (`MATRINHDO`, `TRINHDO`, `HESOTRINHDO`) VALUES
('TD01', 'Đại học', 1.00),
('TD02', 'Thạc sĩ', 1.50),
('TD03', 'Tiến sĩ', 2.00);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `bangluong`
--
ALTER TABLE `bangluong`
  ADD PRIMARY KEY (`MALUONG`),
  ADD KEY `fk_bl_nv` (`MANV`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
