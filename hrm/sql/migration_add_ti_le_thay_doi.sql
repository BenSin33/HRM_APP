-- Tỉ lệ thay đổi lương (%): dương=tăng, âm=trừ, 0=giữ nguyên
ALTER TABLE `phieudanhgia`
ADD COLUMN `TI_LE_THAY_DOI` DECIMAL(5,2) DEFAULT 0.00
COMMENT 'Tỉ lệ thay đổi lương (đơn vị %): dương=tăng, âm=trừ, 0=giữ nguyên';
