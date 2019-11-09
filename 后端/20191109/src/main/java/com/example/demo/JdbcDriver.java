package com.example.demo;
import java.sql.*;
/**
 *  与数据库建立连接并返回连接对象
 *  @author Dreamfeather
 */
public class JdbcDriver {
    private static final String DRIVER_NAME = "com.mysql.cj.jdbc.Driver";
    /**驱动名*/
    private static final String URL = "jdbc:mysql://47.102.115.203:3306/FZU";
    /**服务器地址，端口，数据库名*/
    private static final String USER_NAME = "root2";
    /**用户名*/
    private static final String PASSWORD = "qwerty";
    /**密码*/
    private Connection connection = null;
    /**连接*/
    public void init(){
        try {
            /**加载mysql的驱动类*/
            Class.forName(DRIVER_NAME);
            /**获取数据库连接*/
            connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
            /**使用 用户名 密码登录*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getconnection() {
        return connection;
        /**返回连接*/
    }

    public void close() {
        try {
            if (connection != null){
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}