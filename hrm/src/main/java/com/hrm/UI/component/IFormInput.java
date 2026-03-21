package com.hrm.UI.component;

// Giao diện chung cho các form nhập liệu (thêm/sửa)
public interface IFormInput<T> {
    T getFormData();  // Lấy dữ liệu từ form dưới dạng đối tượng T
    boolean validateForm();  // Kiểm tra tính hợp lệ của dữ liệu trong form
    void clearForm();   // Xóa trắng các trường trong form

    default void setFormData ( T data) {}  // Thiết lập dữ liệu cho form (dùng trong chế độ Sửa)
}


