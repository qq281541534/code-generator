package com.busap.generator.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * 数据库连接
 * @author LiuYu
 */
public class DBConnectionUtils {
	private static Connection conn = null;  
	   
    public static Connection getJDBCConnection() {  
        if (null == conn) {  
            try {  
                Class.forName("com.mysql.jdbc.Driver"); //   
                String url = "jdbc:mysql://192.168.108.143:1234/mywifi?useUnicode=true&characterEncoding=utf-8";//  
                String user = "iov2";  
                String password = "iov2";  
                conn = DriverManager.getConnection(url, user, password);  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
        return conn;  
    }  
  
    public static void close(){  
        if(null!=conn){  
            try {  
                conn.close();  
            } catch (SQLException e) {  
                e.printStackTrace();  
            }  
        }  
    }
}


