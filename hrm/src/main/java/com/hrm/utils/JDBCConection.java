package com.hrm.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class JDBCConection{
    public static Connection getConnection () {

        Properties properties = new Properties();

        try (InputStream input = JDBCConection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                System.err.println("Lỗi: db.properties không tìm thấy trong classpath!");
                return null;
            }
            properties.load(input);

            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.user");
            String password = properties.getProperty("db.password");

            Class.forName("com.mysql.cj.jdbc.Driver"); //đăng kí Driver Mysql (XAMPP dùng MariaDB/MySQL)

            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            System.out.println("lỗi kết nối database" + e.getMessage());
            return null;

        }
    }
}