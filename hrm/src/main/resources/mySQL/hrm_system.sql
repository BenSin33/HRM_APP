-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Mar 03, 2026 at 09:59 AM
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
  `TRANGTHAI` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `bangluong`
--

INSERT INTO `bangluong` (`MALUONG`, `MANV`, `THANG`, `NAM`, `LUONGCOBAN_SNAPSHOT`, `SONGAYCONG`, `TONG_PHUCAP`, `TONG_KHAUTRU`, `NGAYCHOTLUONG`, `THUCLINH`, `TRANGTHAI`) VALUES
('ML01', 'NV07', 1, 2026, 8000000.00, 26, 1800000.00, 600000.00, '2026-01-10', 9200000.00, 'Đã thanh toán'),
('ML02', 'NV07', 2, 2026, 8000000.00, 24, 1680000.00, 600000.00, '2026-02-10', 9080000.00, 'Đã thanh toán');

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
('C2', 'Ca sáng', '06:00:00', '14:00:00'),
('C3', 'Ca chiều', '14:00:00', '22:00:00'),
('C4', 'Ca đêm', '22:00:00', '06:00:00'),
('C5', 'Ca gãy sáng', '08:00:00', '12:00:00'),
('C6', 'Ca gãy chiều', '17:00:00', '21:00:00'),
('C7', 'Ca tăng cường', '18:00:00', '22:00:00');

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
  `TRANGTHAI` varchar(50) DEFAULT NULL,
  `MACALAM` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `chamcong`
--

INSERT INTO `chamcong` (`MACHAMCONG`, `MANV`, `NGAYLAMVIEC`, `SOGIOLAM`, `CHECKIN`, `CHECKOUT`, `TRANGTHAI`, `MACALAM`) VALUES
('CC01', 'NV07', '2026-02-25', 8, '08:00:00', '17:00:00', 'Đúng giờ', 'C1'),
('CC02', 'NV08', '2026-01-27', 8, '07:55:00', '17:05:00', 'Đúng giờ', 'C1'),
('CC03', 'NV09', '2026-01-27', 7.5, '08:30:00', '17:00:00', 'Đi muộn', 'C1'),
('CC04', 'NV01', '2026-01-20', 8, '08:00:00', '17:00:00', 'Đúng giờ', 'C1'),
('CC05', 'NV03', '2026-02-25', 6.75, '09:15:00', '17:00:00', 'Đi muộn', 'C1'),
('CC06', 'NV04', '2026-02-26', 8, '14:00:00', '22:00:00', 'Đúng giờ', 'C3'),
('CC07', 'NV07', '2026-02-26', 7, '22:00:00', '05:00:00', 'Về sớm', 'C4'),
('CC08', 'NV07', '2026-02-27', 7.5, '14:30:00', '22:00:00', 'Đi muộn', 'C3'),
('CC10001', 'NV07', '2026-03-03', 0, '14:04:27', '14:04:29', 'Đúng giờ', NULL);

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
('CN03', 'Chấm công');

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
('CV01', 'Giám đốc', 15000000.00),
('CV02', 'Trưởng phòng', 3000000.00),
('CV03', 'Trưởng nhóm', 2000000.00),
('CV04', 'Nhân viên', 0.00);

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
(2, 'Phí công đoàn', 100000.00);

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
('HD05', 'NV05', '3 năm', '2025-01-01', '2028-01-01', 18000000.00),
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
('L04', 'NV01', 'C1', '2026-01-20', 'Làm việc hành chính'),
('L05', 'NV02', 'C1', '2026-01-20', NULL),
('L06', 'NV03', 'C1', '2026-02-25', 'Làm việc hành chính'),
('L07', 'NV04', 'C3', '2026-02-26', 'Trực ca chiều'),
('L08', 'NV05', 'C4', '2026-02-26', 'Trực ca tối'),
('L09', 'NV06', 'C3', '2026-02-27', 'Trực ca chiều'),
('L10', 'NV07', 'C1', '2026-02-27', 'Làm tại văn phòng'),
('L11', 'NV08', 'C2', '2026-02-28', NULL),
('L12', 'NV09', 'C5', '2026-02-28', NULL),
('L13', 'NV07', 'C1', '2026-03-02', 'Làm việc hành chính và hỗ trợ sửa máy tính'),
('L14', 'NV07', 'C7', '2026-03-03', 'Làm tại văn phòng'),
('L15', 'NV07', 'C1', '2026-03-04', NULL),
('L16', 'NV07', 'C1', '2026-03-05', 'Làm tại văn phòng'),
('L17', 'NV07', 'C4', '2026-03-06', 'Làm tại văn phòng'),
('L18', 'NV07', 'C5', '2026-03-07', 'Làm tại văn phòng');

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
  `NGAYDUYET` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `nghiphep`
--

INSERT INTO `nghiphep` (`MANGHIPHEP`, `MANV`, `LOAINGHI`, `LYDONGHI`, `NGAYNGHI`, `NGAYLAMLAI`, `NGUOIDUYET`, `NGAYDUYET`) VALUES
('NP01', 'NV07', 'Có lương', 'Ốm nhẹ', '2026-02-20', '2026-02-21', 'Hoàng Bảo Ngọc', '2026-01-19'),
('NP02', 'NV02', 'Không lương', 'Việc gia đình', '2026-01-20', '2026-01-21', 'Nguyễn Hoàng Nam', '2026-01-18'),
('NP03', 'NV09', 'Có lương', 'Đi khám bệnh', '2026-02-28', '2026-03-01', 'Phạm Minh Quang', '2026-02-27');

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
('NV01', 'PB01', 'CV01', 'TD03', 'Nguyễn Hoàng Nam', 'Nam', '123 Lê Lợi, Quận 1, TP.HCM', '0901234567', 'nam.nguyen@company.com', '2018-01-15', 12, 'Đang làm việc'),
('NV02', 'PB01', 'CV02', 'TD03', 'Trần Thị Thu Thảo', 'Nữ', '456 Nguyễn Huệ, Quận 1, TP.HCM', '0912345678', 'thao.tran@company.com', '2024-02-01', 12, 'Đang làm việc'),
('NV03', 'PB01', 'CV02', 'TD02', 'Lê Văn Tùng', 'Nam', '789 CMT8, Quận 3, TP.HCM', '0923456789', 'tung.le@company.com', '2024-06-15', 12, 'Đang làm việc'),
('NV04', 'PB02', 'CV02', 'TD02', 'Phạm Minh Quang', 'Nam', '12 Hòa Bình, Quận Tân Phú, TP.HCM', '0934567890', 'quang.pham@company.com', '2022-03-20', 12, 'Đang làm việc'),
('NV05', 'PB02', 'CV03', 'TD02', 'Hoàng Bảo Ngọc', 'Nữ', '88 Cộng Hòa, Quận Tân Bình, TP.HCM', '0945678901', 'ngoc.hoang@company.com', '2023-05-10', 12, 'Đang làm việc'),
('NV06', 'PB03', 'CV03', 'TD02', 'Vũ Anh Tuấn', 'Nam', '202 Võ Văn Kiệt, Quận 5, TP.HCM', '0956789012', 'tuan.vu@company.com', '2023-08-15', 12, 'Đang làm việc'),
('NV07', 'PB02', 'CV04', 'TD01', 'Ngô Thanh Sơn', 'Nam', '15 Trần Hưng Đạo, Quận 1, TP.HCM', '0967890123', 'son.ngo@company.com', '2025-01-10', 12, 'Đang làm việc'),
('NV08', 'PB03', 'CV04', 'TD02', 'Đỗ Mỹ Linh', 'Nữ', '33 Phan Xích Long, Quận Phú Nhuận, TP.HCM', '0878901234', 'linh.do@company.com', '2025-01-20', 12, 'Đang làm việc'),
('NV09', 'PB03', 'CV04', 'TD02', 'Đặng Quốc Huy', 'Nam', '55 Quang Trung, Quận Gò Vấp, TP.HCM', '0989012345', 'huy.dang@company.com', '2025-02-01', 12, 'Đang làm việc');

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
  `QUYEN_XOA` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `phanquyen_chitiet`
--

INSERT INTO `phanquyen_chitiet` (`ROLEID`, `MACHUCNANG`, `QUYEN_XEM`, `QUYEN_THEM`, `QUYEN_SUA`, `QUYEN_XOA`) VALUES
('R1', 'CN01', 1, 1, 1, 1),
('R1', 'CN02', 1, 1, 1, 1),
('R2', 'CN01', 1, 0, 1, 0),
('R3', 'CN03', 1, 1, 0, 0);

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
  `NGAYDANHGIA` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `phieudanhgia`
--

INSERT INTO `phieudanhgia` (`MAPHIEU`, `MANV`, `MADOT`, `MATIEUCHI`, `TONGDIEM`, `NHANXET`, `QUYETDINH`, `NGAYDANHGIA`) VALUES
('DG01', 'NV07', 'Q1-2026', 'TC01', 90, 'Nhiệt tình, hoàn thành tốt', 'Giữ nguyên', '2026-01-31'),
('DG02', 'NV04', 'Q1-2026', 'TC03', 100, 'Lãnh đạo xuất sắc', 'Khen thưởng', '2026-01-31');

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
('PB04', 'Kế toán'),
('PB05', 'Marketing');

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
  `USERID` varchar(50) NOT NULL,
  `MANV` varchar(10) DEFAULT NULL,
  `ROLEID` varchar(10) DEFAULT NULL,
  `PASSWORD` varchar(255) NOT NULL,
  `STATUS` int(11) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `taikhoan`
--

INSERT INTO `taikhoan` (`USERID`, `MANV`, `ROLEID`, `PASSWORD`, `STATUS`) VALUES
('admin1', 'NV01', 'R1', '123', 1),
('admin2', 'NV02', 'R1', '123', 1),
('admin3', 'NV03', 'R1', '123', 1),
('emp1', 'NV07', 'R3', '123', 1),
('emp2', 'NV08', 'R3', '123', 1),
('emp3', 'NV09', 'R3', '123', 1),
('mgr1', 'NV04', 'R2', '123', 1),
('mgr2', 'NV05', 'R2', '123', 1),
('mgr3', 'NV06', 'R2', '123', 1);

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
('TC04', 'Kỹ năng làm việc nhóm', 10),
('TC05', 'Sự sáng tạo trong công việc', 10),
('TC06', 'Kỷ luật và giờ giấc', 10),
('TC07', 'Khả năng giải quyết vấn đề', 10),
('TC08', 'Mức độ hoàn thành KPI', 10),
('TC09', 'Kỹ năng giao tiếp', 10),
('TC10', 'Tinh thần cầu tiến', 10);

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
('TD01', 'Cao đẳng', 0.90),
('TD02', 'Đại học', 1.00),
('TD03', 'Thạc sĩ', 1.50),
('TD04', 'Tiến sĩ', 2.00);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `bangluong`
--
ALTER TABLE `bangluong`
  ADD PRIMARY KEY (`MALUONG`),
  ADD KEY `fk_bl_nv` (`MANV`);

--
-- Indexes for table `calam`
--
ALTER TABLE `calam`
  ADD PRIMARY KEY (`MACALAM`);

--
-- Indexes for table `chamcong`
--
ALTER TABLE `chamcong`
  ADD PRIMARY KEY (`MACHAMCONG`),
  ADD KEY `fk_cc_nv` (`MANV`);

--
-- Indexes for table `chitiet_luong_biendong`
--
ALTER TABLE `chitiet_luong_biendong`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `fk_ct_bl` (`MALUONG`);

--
-- Indexes for table `chucnang`
--
ALTER TABLE `chucnang`
  ADD PRIMARY KEY (`MACHUCNANG`);

--
-- Indexes for table `chucvu`
--
ALTER TABLE `chucvu`
  ADD PRIMARY KEY (`MACHUCVU`);

--
-- Indexes for table `danhmuc_khautru`
--
ALTER TABLE `danhmuc_khautru`
  ADD PRIMARY KEY (`MAKHAUTRU`);

--
-- Indexes for table `danhmuc_phucap`
--
ALTER TABLE `danhmuc_phucap`
  ADD PRIMARY KEY (`MAPHUCAP`);

--
-- Indexes for table `hopdong`
--
ALTER TABLE `hopdong`
  ADD PRIMARY KEY (`MAHOPDONG`),
  ADD KEY `fk_hd_nv` (`MANV`);

--
-- Indexes for table `lichlamviec`
--
ALTER TABLE `lichlamviec`
  ADD PRIMARY KEY (`MALICH`),
  ADD KEY `fk_llv_nv` (`MANV`),
  ADD KEY `fk_llv_cl` (`MACALAM`);

--
-- Indexes for table `nghiphep`
--
ALTER TABLE `nghiphep`
  ADD PRIMARY KEY (`MANGHIPHEP`),
  ADD KEY `fk_np_nv` (`MANV`);

--
-- Indexes for table `nhanvien`
--
ALTER TABLE `nhanvien`
  ADD PRIMARY KEY (`MANV`),
  ADD KEY `fk_nv_pb` (`MAPHONGBAN`),
  ADD KEY `fk_nv_cv` (`MACHUCVU`),
  ADD KEY `fk_nv_td` (`MATRINHDO`);

--
-- Indexes for table `phanquyen_chitiet`
--
ALTER TABLE `phanquyen_chitiet`
  ADD PRIMARY KEY (`ROLEID`,`MACHUCNANG`),
  ADD KEY `fk_pq_cn` (`MACHUCNANG`);

--
-- Indexes for table `phieudanhgia`
--
ALTER TABLE `phieudanhgia`
  ADD PRIMARY KEY (`MAPHIEU`),
  ADD KEY `fk_pdg_nv` (`MANV`),
  ADD KEY `fk_pdg_tc` (`MATIEUCHI`);

--
-- Indexes for table `phongban`
--
ALTER TABLE `phongban`
  ADD PRIMARY KEY (`MAPHONGBAN`);

--
-- Indexes for table `role`
--
ALTER TABLE `role`
  ADD PRIMARY KEY (`ROLEID`);

--
-- Indexes for table `taikhoan`
--
ALTER TABLE `taikhoan`
  ADD PRIMARY KEY (`USERID`),
  ADD KEY `fk_tk_nv` (`MANV`),
  ADD KEY `fk_tk_role` (`ROLEID`);

--
-- Indexes for table `tieuchidanhgia`
--
ALTER TABLE `tieuchidanhgia`
  ADD PRIMARY KEY (`MATIEUCHI`);

--
-- Indexes for table `trinhdo`
--
ALTER TABLE `trinhdo`
  ADD PRIMARY KEY (`MATRINHDO`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `chitiet_luong_biendong`
--
ALTER TABLE `chitiet_luong_biendong`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `danhmuc_khautru`
--
ALTER TABLE `danhmuc_khautru`
  MODIFY `MAKHAUTRU` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `danhmuc_phucap`
--
ALTER TABLE `danhmuc_phucap`
  MODIFY `MAPHUCAP` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `bangluong`
--
ALTER TABLE `bangluong`
  ADD CONSTRAINT `fk_bl_nv` FOREIGN KEY (`MANV`) REFERENCES `nhanvien` (`MANV`);

--
-- Constraints for table `chamcong`
--
ALTER TABLE `chamcong`
  ADD CONSTRAINT `fk_cc_nv` FOREIGN KEY (`MANV`) REFERENCES `nhanvien` (`MANV`);

--
-- Constraints for table `chitiet_luong_biendong`
--
ALTER TABLE `chitiet_luong_biendong`
  ADD CONSTRAINT `fk_ct_bl` FOREIGN KEY (`MALUONG`) REFERENCES `bangluong` (`MALUONG`);

--
-- Constraints for table `hopdong`
--
ALTER TABLE `hopdong`
  ADD CONSTRAINT `fk_hd_nv` FOREIGN KEY (`MANV`) REFERENCES `nhanvien` (`MANV`);

--
-- Constraints for table `lichlamviec`
--
ALTER TABLE `lichlamviec`
  ADD CONSTRAINT `fk_llv_cl` FOREIGN KEY (`MACALAM`) REFERENCES `calam` (`MACALAM`),
  ADD CONSTRAINT `fk_llv_nv` FOREIGN KEY (`MANV`) REFERENCES `nhanvien` (`MANV`);

--
-- Constraints for table `nghiphep`
--
ALTER TABLE `nghiphep`
  ADD CONSTRAINT `fk_np_nv` FOREIGN KEY (`MANV`) REFERENCES `nhanvien` (`MANV`);

--
-- Constraints for table `nhanvien`
--
ALTER TABLE `nhanvien`
  ADD CONSTRAINT `fk_nv_cv` FOREIGN KEY (`MACHUCVU`) REFERENCES `chucvu` (`MACHUCVU`),
  ADD CONSTRAINT `fk_nv_pb` FOREIGN KEY (`MAPHONGBAN`) REFERENCES `phongban` (`MAPHONGBAN`),
  ADD CONSTRAINT `fk_nv_td` FOREIGN KEY (`MATRINHDO`) REFERENCES `trinhdo` (`MATRINHDO`);

--
-- Constraints for table `phanquyen_chitiet`
--
ALTER TABLE `phanquyen_chitiet`
  ADD CONSTRAINT `fk_pq_cn` FOREIGN KEY (`MACHUCNANG`) REFERENCES `chucnang` (`MACHUCNANG`),
  ADD CONSTRAINT `fk_pq_role` FOREIGN KEY (`ROLEID`) REFERENCES `role` (`ROLEID`);

--
-- Constraints for table `phieudanhgia`
--
ALTER TABLE `phieudanhgia`
  ADD CONSTRAINT `fk_pdg_nv` FOREIGN KEY (`MANV`) REFERENCES `nhanvien` (`MANV`),
  ADD CONSTRAINT `fk_pdg_tc` FOREIGN KEY (`MATIEUCHI`) REFERENCES `tieuchidanhgia` (`MATIEUCHI`);

--
-- Constraints for table `taikhoan`
--
ALTER TABLE `taikhoan`
  ADD CONSTRAINT `fk_tk_nv` FOREIGN KEY (`MANV`) REFERENCES `nhanvien` (`MANV`),
  ADD CONSTRAINT `fk_tk_role` FOREIGN KEY (`ROLEID`) REFERENCES `role` (`ROLEID`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
