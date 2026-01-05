package com.hrm;

import javax.swing.*;

public class App {
    
    void initFrame () {
        JFrame frame = new JFrame("HRM Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        initFrame();
    }
}
