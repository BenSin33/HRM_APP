# Salary Calculation Implementation

## Overview
Implemented the salary calculation functionality for the SalaryTab module according to the formula:
**Thực lĩnh = (Lương cơ bản × Hệ số trình độ × (Số ngày công / 22)) + Tổng phụ cấp - Tổng khấu trừ**

## Changes Made

### 1. **AttendanceDAO.java** - Added Working Days Counting Method
**File:** `d:\MCRo VSCODE\DoAnJava\HRM_APP\hrm\src\main\java\com\hrm\DAO\Employee\AttendanceDAO.java`

**New Method:**
```java
public float getWorkingDaysForEmployeeInMonth(String manv, int month, int year)
```

**Function:**
- Counts working days for a specific employee in a given month/year
- Only counts days where `TRANGTHAI = '1'` (on-time attendance)
- Returns the count as a float value
- Used to calculate the attendance ratio in salary computation

### 2. **SalaryService.java** - Added Salary Calculation Method
**File:** `d:\MCRo VSCODE\DoAnJava\HRM_APP\hrm\src\main\java\com\hrm\Service\SalaryService.java`

**New Method:**
```java
public boolean calculateSalaryForMonthRange(int fromMonth, int fromYear, int toMonth, int toYear)
```

**Function:**
- Calculates salary for all employees for a given month range
- Implements the complete salary calculation formula:
  1. Gets working days from AttendanceDAO
  2. Retrieves employee data (basic salary, education coefficient, position allowance)
  3. Gets default allowances and deductions
  4. Calculates: `(BaseSalary × EducationCoeff × (WorkingDays / 22)) + Allowances - Deductions`
  5. Updates or creates salary records in the database

**Key Features:**
- Only recalculates if salary doesn't exist or is in draft status (trangThai = '0')
- Uses YearMonth for date handling
- Rounds final salary to 2 decimal places
- Returns true if at least one salary was successfully calculated
- Handles exceptions gracefully

**Salary Calculation Formula Details:**
```
Lương cơ bản = From LUONGCOBAN_SNAPSHOT in bangluong table
Hệ số trình độ = From HESOTRINHDO in trinhdo table
Số ngày công = Counted from chamcong table (TRANGTHAI = '1')
Số ngày công thực tế = 22 (default working days)
Phụ cấp = Total allowances (default) + Position allowance (phucapChucVu)
Khấu trừ = Total deductions (default)
```

### 3. **SalaryManagement.java** - Implemented Calculate Button Handler
**File:** `d:\MCRo VSCODE\DoAnJava\HRM_APP\hrm\src\main\java\com\hrm\UI\HR\SalaryTab\SalaryManagement.java`

**Modified Method:**
```java
private void handleCalculateSalary()
```

**Function:**
- Validates that a month/date range is selected
- Shows confirmation dialog with salary calculation formula
- Calls the salary calculation service
- Displays success/error messages to user
- Refreshes the salary table after calculation

**User Interaction:**
1. User clicks "Tính lương" button
2. System validates selected month/date range
3. Shows confirmation dialog with calculation formula
4. If confirmed, calculates salaries for all employees in the range
5. Displays success message and refreshes table

## Data Flow

```
User clicks "Tính lương" button
    ↓
handleCalculateSalary() validates month selection
    ↓
calculateSalaryForMonthRange() iterates through all employees
    ↓
For each employee, each month:
  - Get working days from AttendanceDAO
  - Get employee salary info
  - Get allowances and deductions
  - Calculate salary using formula
  - Update/create salary record in database
    ↓
Refresh salary table display
```

## Testing Checklist

- [x] Project compiles without errors
- [ ] Test calculating salary for current month (March 2026, from 1st to 21st)
- [ ] Verify working days are counted correctly from chamcong table
- [ ] Test with different employees (different salaries, education levels, positions)
- [ ] Verify salary calculation follows the exact formula
- [ ] Test updating existing salary records (draft status)
- [ ] Verify allowances and deductions are included correctly
- [ ] Test date range calculations (multiple months)
- [ ] Verify database updates are persisted

## Database Interaction

The implementation interacts with the following tables:
- **bangluong** - Salary table (updated with calculated values)
- **chamcong** - Attendance table (provides working days count)
- **nhanvien** - Employee table (provides employee list)
- **trinhdo** - Education level table (provides education coefficient)
- **chucvu** - Position table (provides position allowance)
- **allowance** & **deduction** - Provides default allowances and deductions

## Notes

1. The system uses a default of 22 working days per month as the reference for salary calculation
2. Only employees with on-time attendance (TRANGTHAI = '1') are counted as working days
3. Salary records in draft status (trangThai = '0') can be recalculated
4. The system stores a snapshot of basic salary at the time of calculation
5. Current implementation uses system-wide default allowances and deductions; can be extended to support per-employee allowances if needed

## Future Enhancements

1. Add support for employee-specific allowances and deductions
2. Add option to manually adjust salary before finalizing
3. Add salary calculation templates for different departments
4. Add audit trail for salary modifications
5. Add export functionality for calculated salaries
