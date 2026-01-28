package com.hrm.utils;

import javax.swing.*;
import java.awt.*;

public class IconResize {
    public static ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        if (icon.getImage() == null) return icon; // Tránh lỗi nếu không tìm thấy ảnh
        Image img = icon.getImage();
        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }
}