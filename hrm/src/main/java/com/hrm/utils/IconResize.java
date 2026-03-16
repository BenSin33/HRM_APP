package com.hrm.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class IconResize {

    public static ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        if (icon == null || icon.getImage() == null) {
            return icon;
        }

        Image img = icon.getImage();

        BufferedImage resizedImage = new BufferedImage(
                width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = resizedImage.createGraphics();

        // Giữ nét, chống răng cưa
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(img, 0, 0, width, height, null);
        g2d.dispose();

        return new ImageIcon(resizedImage);
    }
}
