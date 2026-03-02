package com.hrm.utils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class JDBCConection {

    public static Connection getConnection() {
        Properties properties = new Properties();

        try {
            InputStream is = JDBCConection.class
                    .getClassLoader()
                    .getResourceAsStream("db.properties");

            if (is == null) {
                System.out.println("❌ Không tìm thấy db.properties");
                return null;
            }

            properties.load(is);

            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.user");
            String password = properties.getProperty("db.password");

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Kết nối DB thành công");
            return conn;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}