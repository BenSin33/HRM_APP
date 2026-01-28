-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1
-- Thời gian đã tạo: Th1 28, 2026 lúc 08:16 AM
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
  `LUONGCOBAN` decimal(18,2) DEFAULT NULL,
  `PHUCAP` decimal(18,2) DEFAULT NULL,
  `KHAUTRU` decimal(18,2) DEFAULT NULL,
  `NGAYTAO` date DEFAULT NULL,
  `THUCLINH` decimal(18,2) DEFAULT NULL,
  `TRANGTHAI` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `bangluong`
--

INSERT INTO `bangluong` (`MALUONG`, `MANV`, `THANG`, `NAM`, `LUONGCOBAN`, `PHUCAP`, `KHAUTRU`, `NGAYTAO`, `THUCLINH`, `TRANGTHAI`) VALUES
('ML01', 'NV01', 12, 2025, 25000000.00, 3000000.00, 0.00, '2025-12-31', 28000000.00, 'Đã thanh toán');

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
('C2', 'Ca sáng', '06:00:00', '14:00:00');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `chamcon`
--

CREATE TABLE `chamcong` (
  `MACHAMCON` varchar(10) NOT NULL,
  `MANV` varchar(10) DEFAULT NULL,
  `NGAYLAMVIEC` date DEFAULT NULL,
  `SOGIOLAM` float DEFAULT NULL,
  `CHECKIN` time DEFAULT NULL,
  `CHECKOUT` time DEFAULT NULL,
  `TRANGTHAI` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `chamcon`
--

INSERT INTO `chamcong` (`MACHAMCON`, `MANV`, `NGAYLAMVIEC`, `SOGIOLAM`, `CHECKIN`, `CHECKOUT`, `TRANGTHAI`) VALUES
('CC01', 'NV01', '2026-01-27', 8, '08:00:00', '17:00:00', 'Đúng giờ');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `chucnang`
--

CREATE TABLE `chucnang` (
  `MACHUCNANG` varchar(10) NOT NULL,
  `ROLEID` varchar(10) DEFAULT NULL,
  `TENCHUCNANG` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

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
('CV03', 'Nhân viên', 0.00),
('CV04', 'Thực tập', 0.00);

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
('HD01', 'NV01', 'Vô thời hạn', '2023-01-01', '2099-12-31', 25000000.00),
('HD03', 'NV03', '1 năm', '2024-01-20', '2025-01-20', 15000000.00);

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
('L01', 'NV03', 'C1', '2026-01-28', 'Phát triển module'),
('L02', 'NV04', 'C1', '2026-01-28', 'Họp kinh doanh');

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
  `NGAYDUYET` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `nghiphep`
--

INSERT INTO `nghiphep` (`MANGHIPHEP`, `MANV`, `LOAINGHI`, `LYDONGHI`, `NGAYNGHI`, `NGAYLAMLAI`, `NGUOIDUYET`, `NGAYDUYET`) VALUES
('NP01', 'NV06', 'Có lương', 'Ốm nhẹ', '2026-01-28', '2026-01-29', 'Nguyễn Văn HR', '2026-01-27');

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
('NV01', 'PB01', 'CV01', 'TD03', 'Nguyễn Văn HR', 'Nam', 'Hà Nội', '091', 'hr@gmail.com', '2023-01-01', 12, 'Đang làm việc'),
('NV02', 'PB02', 'CV01', 'TD03', 'Trần Minh Tech', 'Nam', 'TP.HCM', '092', 'tech@gmail.com', '2023-02-15', 12, 'Đang làm việc'),
('NV03', 'PB02', 'CV03', 'TD01', 'Lê Thị Code', 'Nữ', 'Đà Nẵng', '093', 'dev@gmail.com', '2024-01-20', 12, 'Đang làm việc'),
('NV04', 'PB03', 'CV01', 'TD01', 'Phạm Sale', 'Nam', 'Hải Phòng', '094', 'sale@gmail.com', '2023-05-10', 12, 'Đang làm việc'),
('NV05', 'PB04', 'CV03', 'TD01', 'Hoàng Kế Toán', 'Nữ', 'Cần Thơ', '095', 'acc@gmail.com', '2024-01-10', 12, 'Đang làm việc'),
('NV06', 'PB02', 'CV03', 'TD01', 'Bùi Java', 'Nam', 'Hà Nội', '096', 'java@gmail.com', '2024-06-01', 12, 'Đang làm việc'),
('NV07', 'PB03', 'CV03', 'TD01', 'Ngô Kinh Doanh', 'Nữ', 'Bình Dương', '097', 'kd@gmail.com', '2023-11-01', 12, 'Đang làm việc'),
('NV08', 'PB02', 'CV04', 'TD01', 'Vũ Intern', 'Nam', 'Nam Định', '098', 'it@gmail.com', '2025-12-01', 5, 'Thử việc'),
('NV09', 'PB01', 'CV03', 'TD01', 'Mai Bảo Ngọc', 'Nữ', 'Huế', '099', 'ngoc@gmail.com', '2024-03-01', 12, 'Đang làm việc'),
('NV10', 'PB02', 'CV03', 'TD01', 'Đặng Thế Anh', 'Nam', 'Hà Nội', '0910', 'anh@gmail.com', '2023-10-10', 12, 'Đang làm việc');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `phieudanhgia`
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
-- Đang đổ dữ liệu cho bảng `phieudanhgia`
--

INSERT INTO `phieudanhgia` (`MAPHIEU`, `MANV`, `MADOT`, `MATIEUCHI`, `TONGDIEM`, `NHANXET`, `QUYETDINH`, `NGAYDANHGIA`) VALUES
('DG01', 'NV03', 'Q4-2025', 'TC01', 9, 'Năng suất xuất sắc', 'Tăng lương', '2025-12-25');

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
('PB03', 'Kinh doanh'),
('PB04', 'Kế toán');

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
  `USERID` varchar(10) NOT NULL,
  `MANV` varchar(10) DEFAULT NULL,
  `ROLEID` varchar(10) DEFAULT NULL,
  `PASSWORD` varchar(255) NOT NULL,
  `STATUS` int(11) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `taikhoan`
--

INSERT INTO `taikhoan` (`USERID`, `MANV`, `ROLEID`, `PASSWORD`, `STATUS`) VALUES
('admin', 'NV01', 'R1', '123', 1),
('manager', 'NV02', 'R2', '123', 1),
('user', 'NV03', 'R3', '123', 1);

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
('TC01', 'Năng suất', 10),
('TC02', 'Thái độ', 10);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `trinhdo`
--

CREATE TABLE `trinhdo` (
  `MATRINHDO` varchar(10) NOT NULL,
  `TRINHDO` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `trinhdo`
--

INSERT INTO `trinhdo` (`MATRINHDO`, `TRINHDO`) VALUES
('TD01', 'Đại học'),
('TD03', 'Thạc sĩ');

--
-- Chỉ mục cho các bảng đã đổ
--

--
-- Chỉ mục cho bảng `bangluong`
--
ALTER TABLE `bangluong`
  ADD PRIMARY KEY (`MALUONG`),
  ADD KEY `MANV` (`MANV`);

--
-- Chỉ mục cho bảng `calam`
--
ALTER TABLE `calam`
  ADD PRIMARY KEY (`MACALAM`);

--
-- Chỉ mục cho bảng `chamcon`
--
ALTER TABLE `chamcon`
  ADD PRIMARY KEY (`MACHAMCON`),
  ADD KEY `MANV` (`MANV`);

--
-- Chỉ mục cho bảng `chucnang`
--
ALTER TABLE `chucnang`
  ADD PRIMARY KEY (`MACHUCNANG`),
  ADD KEY `ROLEID` (`ROLEID`);

--
-- Chỉ mục cho bảng `chucvu`
--
ALTER TABLE `chucvu`
  ADD PRIMARY KEY (`MACHUCVU`);

--
-- Chỉ mục cho bảng `hopdong`
--
ALTER TABLE `hopdong`
  ADD PRIMARY KEY (`MAHOPDONG`),
  ADD KEY `MANV` (`MANV`);

--
-- Chỉ mục cho bảng `lichlamviec`
--
ALTER TABLE `lichlamviec`
  ADD PRIMARY KEY (`MALICH`),
  ADD KEY `MANV` (`MANV`),
  ADD KEY `MACALAM` (`MACALAM`);

--
-- Chỉ mục cho bảng `nghiphep`
--
ALTER TABLE `nghiphep`
  ADD PRIMARY KEY (`MANGHIPHEP`),
  ADD KEY `MANV` (`MANV`);

--
-- Chỉ mục cho bảng `nhanvien`
--
ALTER TABLE `nhanvien`
  ADD PRIMARY KEY (`MANV`),
  ADD KEY `MAPHONGBAN` (`MAPHONGBAN`),
  ADD KEY `MACHUCVU` (`MACHUCVU`),
  ADD KEY `MATRINHDO` (`MATRINHDO`);

--
-- Chỉ mục cho bảng `phieudanhgia`
--
ALTER TABLE `phieudanhgia`
  ADD PRIMARY KEY (`MAPHIEU`),
  ADD KEY `MANV` (`MANV`),
  ADD KEY `MATIEUCHI` (`MATIEUCHI`);

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
  ADD PRIMARY KEY (`USERID`),
  ADD KEY `MANV` (`MANV`),
  ADD KEY `ROLEID` (`ROLEID`);

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
-- Các ràng buộc cho các bảng đã đổ
--

--
-- Các ràng buộc cho bảng `bangluong`
--
ALTER TABLE `bangluong`
  ADD CONSTRAINT `bangluong_ibfk_1` FOREIGN KEY (`MANV`) REFERENCES `nhanvien` (`MANV`);

--
-- Các ràng buộc cho bảng `chamcon`
--
ALTER TABLE `chamcon`
  ADD CONSTRAINT `chamcon_ibfk_1` FOREIGN KEY (`MANV`) REFERENCES `nhanvien` (`MANV`);

--
-- Các ràng buộc cho bảng `chucnang`
--
ALTER TABLE `chucnang`
  ADD CONSTRAINT `chucnang_ibfk_1` FOREIGN KEY (`ROLEID`) REFERENCES `role` (`ROLEID`);

--
-- Các ràng buộc cho bảng `hopdong`
--
ALTER TABLE `hopdong`
  ADD CONSTRAINT `hopdong_ibfk_1` FOREIGN KEY (`MANV`) REFERENCES `nhanvien` (`MANV`);

--
-- Các ràng buộc cho bảng `lichlamviec`
--
ALTER TABLE `lichlamviec`
  ADD CONSTRAINT `lichlamviec_ibfk_1` FOREIGN KEY (`MANV`) REFERENCES `nhanvien` (`MANV`),
  ADD CONSTRAINT `lichlamviec_ibfk_2` FOREIGN KEY (`MACALAM`) REFERENCES `calam` (`MACALAM`);

--
-- Các ràng buộc cho bảng `nghiphep`
--
ALTER TABLE `nghiphep`
  ADD CONSTRAINT `nghiphep_ibfk_1` FOREIGN KEY (`MANV`) REFERENCES `nhanvien` (`MANV`);

--
-- Các ràng buộc cho bảng `nhanvien`
--
ALTER TABLE `nhanvien`
  ADD CONSTRAINT `nhanvien_ibfk_1` FOREIGN KEY (`MAPHONGBAN`) REFERENCES `phongban` (`MAPHONGBAN`),
  ADD CONSTRAINT `nhanvien_ibfk_2` FOREIGN KEY (`MACHUCVU`) REFERENCES `chucvu` (`MACHUCVU`),
  ADD CONSTRAINT `nhanvien_ibfk_3` FOREIGN KEY (`MATRINHDO`) REFERENCES `trinhdo` (`MATRINHDO`);

--
-- Các ràng buộc cho bảng `phieudanhgia`
--
ALTER TABLE `phieudanhgia`
  ADD CONSTRAINT `phieudanhgia_ibfk_1` FOREIGN KEY (`MANV`) REFERENCES `nhanvien` (`MANV`),
  ADD CONSTRAINT `phieudanhgia_ibfk_2` FOREIGN KEY (`MATIEUCHI`) REFERENCES `tieuchidanhgia` (`MATIEUCHI`);

--
-- Các ràng buộc cho bảng `taikhoan`
--
ALTER TABLE `taikhoan`
  ADD CONSTRAINT `taikhoan_ibfk_1` FOREIGN KEY (`MANV`) REFERENCES `nhanvien` (`MANV`),
  ADD CONSTRAINT `taikhoan_ibfk_2` FOREIGN KEY (`ROLEID`) REFERENCES `role` (`ROLEID`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
