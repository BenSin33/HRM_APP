package com.hrm.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class JDBCConection{
    public static Connection getConnection () {

        Properties properties = new Properties();

        try (FileInputStream fis = new FileInputStream("db.properties")) {
            properties.load(fis);

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