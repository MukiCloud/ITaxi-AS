package com.mukicloud.mukitest.NetDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class NetDBConn {
    private static Connection SQLConn;
    private static Statement STM;

    public static boolean ConnectSQL(String SQLIP, String DBName, String User, String Password) {
        try {
            if (SQLConn == null || !SQLConn.isValid(15)) {
                Class.forName("com.mysql.jdbc.Driver");
                SQLConn = DriverManager.getConnection("jdbc:mysql://" + SQLIP + ":3306/" + DBName + "?serverTimezone=UTC&characterEncoding=UTF-8", User, Password);
                if (SQLConn != null) {
                    STM = SQLConn.createStatement();
                    return true;
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundException\n" + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean DisconnectSQL() {
        try {
            if (SQLConn != null && !SQLConn.isClosed()) {
                STM.close();
                SQLConn.close();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Connection getSQLConn() {
        return SQLConn;
    }

    public static Statement getSTM() {
        return STM;
    }
}
