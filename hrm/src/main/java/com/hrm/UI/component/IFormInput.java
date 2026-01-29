package com.hrm.UI.component;

public interface IFormInput<T> {
    T getFormData();
    boolean validateForm();
    void clearForm();

    default void setFormData ( T data) {}
}


