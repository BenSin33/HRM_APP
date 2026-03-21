# Salary Schema Update Summary - SONGAYCONG_CHUAN Implementation

## Overview
Successfully implemented the SONGAYCONG_CHUAN (standard working days snapshot) column to improve data integrity and historical accuracy in salary calculations, following the architectural pattern of existing LUONGCOBAN_SNAPSHOT.

## Changes Made

### 1. Database Schema (hrm_system.sql)
**File:** [hrm/src/main/resources/mySQL/hrm_system.sql](hrm/src/main/resources/mySQL/hrm_system.sql)

**Change:** Added SONGAYCONG_CHUAN column to bangluong table
```sql
SONGAYCONG_CHUAN float DEFAULT 26 COMMENT 'Số ngày công chuẩn (công ty quy định)'
```
- **Default Value:** 26 days (standard working days per month in Vietnam)
- **Purpose:** Store snapshot of standard working days for that specific month
- **Data Type:** float (allows flexibility for different month policies)

### 2. Data Transfer Object (SalaryDTO.java)
**File:** [hrm/src/main/java/com/hrm/DTO/Employee/SalaryDTO.java](hrm/src/main/java/com/hrm/DTO/Employee/SalaryDTO.java)

**Change:** Added soNgayCongChuan field
```java
public float soNgayCongChuan; // Số ngày công chuẩn (công ty quy định)
```
- Positioned after soNgayCong field for logical grouping
- Maps to SONGAYCONG_CHUAN database column

### 3. Data Access Object - SELECT Methods (SalaryDAO.java)
**File:** [hrm/src/main/java/com/hrm/DAO/Employee/SalaryDAO.java](hrm/src/main/java/com/hrm/DAO/Employee/SalaryDAO.java)

**Updates:** Three SELECT methods updated to include SONGAYCONG_CHUAN

#### a) getAllSalaries()
- Added SONGAYCONG_CHUAN to SELECT clause
- Added mapping: `dto.soNgayCongChuan = rs.getFloat("SONGAYCONG_CHUAN");`

#### b) getSalariesByMonthYear()
- Added SONGAYCONG_CHUAN to SELECT clause
- Added mapping: `dto.soNgayCongChuan = rs.getFloat("SONGAYCONG_CHUAN");`

#### c) getSalaryByMaNV()
- Added SONGAYCONG_CHUAN to SELECT clause
- Added mapping: `dto.soNgayCongChuan = rs.getFloat("SONGAYCONG_CHUAN");`

### 4. Data Access Object - Write Methods (SalaryDAO.java)

#### a) insertSalary()
**Changes:**
- Added SONGAYCONG_CHUAN to INSERT column list
- Added parameter binding: `ps.setFloat(7, salary.soNgayCongChuan > 0 ? salary.soNgayCongChuan : 26);`
- Parameter index adjusted to accommodate new column
- Default value 26 applied if soNgayCongChuan is 0 or invalid

#### b) updateSalary()
**Changes:**
- Added SONGAYCONG_CHUAN to UPDATE SET clause
- Added parameter binding: `ps.setFloat(3, salary.soNgayCongChuan > 0 ? salary.soNgayCongChuan : 26);`
- Parameter index adjusted to accommodate new column
- Default value 26 applied if soNgayCongChuan is 0 or invalid

### 5. Business Logic Service (SalaryService.java)
**File:** [hrm/src/main/java/com/hrm/Service/SalaryService.java](hrm/src/main/java/com/hrm/Service/SalaryService.java)

**Changes:** Updated calculateSalaryForMonthRange() method

#### a) Added soNgayCongChuan constant
```java
float soNgayCongChuan = 26; // Số ngày công chuẩn (công ty quy định)
```

#### b) Set soNgayCongChuan in SalaryDTO
```java
salaryDTO.soNgayCongChuan = soNgayCongChuan;
```

#### c) Updated formula calculation
**Old:** Used hardcoded value 26
**New:** Uses variable soNgayCongChuan
```java
BigDecimal ngayCongRatio = new BigDecimal(soNgayCong)
    .divide(new BigDecimal(soNgayCongChuan), 4, RoundingMode.HALF_UP);
```

#### d) Updated formula documentation
```
Công thức: thực lĩnh = (lương cơ bản x hệ số trình độ x (số ngày công / số ngày công chuẩn)) + Tổng phụ cấp - tổng khấu trừ
```

## Salary Calculation Formula

### New Formula (with SONGAYCONG_CHUAN)
```
Thực lĩnh = (Lương × Hệ số × (Số ngày công / Số ngày công chuẩn)) + Phụ cấp - Khấu trừ
```

### Component Mapping
- **Lương:** LUONGCOBAN_SNAPSHOT (from hopdong table)
- **Hệ số:** HESOTRINHDO (education coefficient)
- **Số ngày công:** SONGAYCONG (actual working days from chamcong table)
- **Số ngày công chuẩn:** SONGAYCONG_CHUAN (standard working days - now stored in bangluong)
- **Phụ cấp:** PHUCAPCHUCVU (position allowance)
- **Khấu trừ:** Allowances and deductions from respective tables

## Key Benefits

1. **Historical Accuracy:** Each salary record stores the standard working days used for that month's calculation
2. **Flexibility:** Can adjust standard working days per month if company policy changes
3. **Data Integrity:** Follows existing snapshot pattern (like LUONGCOBAN_SNAPSHOT)
4. **Traceability:** Can verify past calculations by reviewing stored values
5. **Audit Trail:** Maintains complete record of what days were used for each calculation

## Testing Recommendations

1. Verify INSERT operations create records with SONGAYCONG_CHUAN = 26
2. Verify SELECT methods retrieve SONGAYCONG_CHUAN correctly
3. Verify UPDATE operations modify SONGAYCONG_CHUAN correctly
4. Test salary calculation with verify the formula uses soNgayCongChuan
5. Check historical records to ensure new column doesn't break existing queries

## Compilation Status
✅ **BUILD SUCCESS** - All 238 source files compiled without errors (March 21, 2026, 15:18)

## Migration Notes for Existing Data
If migrating from older version without SONGAYCONG_CHUAN:
1. Column has DEFAULT 26 - existing records will get this value
2. No data loss - only new column added
3. Queries should explicitly select SONGAYCONG_CHUAN as they now do
4. Existing UPDATE statements need version with new column

## Files Modified
1. ✅ hrm_system.sql - Schema updated
2. ✅ SalaryDTO.java - Field added
3. ✅ SalaryDAO.java - SELECT/INSERT/UPDATE methods updated
4. ✅ SalaryService.java - Formula and calculation updated
