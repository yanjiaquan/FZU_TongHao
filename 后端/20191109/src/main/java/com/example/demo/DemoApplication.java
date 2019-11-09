package com.example.demo;

import net.sf.json.JSONArray;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.*;
import java.util.*;

import net.sf.json.*;

@SpringBootApplication
public class DemoApplication {
    public static List convertList(ResultSet rs) throws SQLException {
        List list = new ArrayList();
        ResultSetMetaData md = rs.getMetaData();
        int columnCount = md.getColumnCount();
        while (rs.next()) {
            Map rowData = new HashMap<String,Object>();
            for (int i = 1; i <= columnCount; i++) {
                rowData.put(md.getColumnName(i), rs.getObject(i));
            }
            list.add(rowData);
        }
        rs.close();
        return list;
    }
    public static JSONArray convertJsonArray(ResultSet rs) throws  SQLException{
        /**将ResultSet对象转为JSONArray<JSONObject>对象*/
        JSONArray jsonArray=new JSONArray();
        ResultSetMetaData md = rs.getMetaData();
        int columnCount = md.getColumnCount();
        while (rs.next()) {
            JSONObject rowData = new JSONObject();
            /**行数据使用JSONObject存储*/
            for (int i = 1; i <= columnCount; i++) {
                rowData.put(md.getColumnName(i), rs.getObject(i));
                /**放入列名与值 JSON[列名]=数据对象*/
            }
            jsonArray.add(rowData);
            /**再将一行存入jsonArray中，形成二维表*/
        }
        rs.close();
        /**关闭结果集读取流*/
        return jsonArray;
    }
    public static List getResultList(Connection connection, String sql) throws SQLException {
        /**不带参数的sql语句*/
        PreparedStatement prst = connection.prepareStatement(sql);
        /**sql语句预编译*/
        List l= convertList(prst.executeQuery());
        /**执行sql语句，收到查询结果，并将结果集ResultSet转为List*/
        prst.close();
        /**关闭 预编译字段*/
        return l;
    }
    public static JSONArray getResultJson(Connection connection, String sql) throws SQLException {
        /**不带参数的sql语句*/
        PreparedStatement prst = connection.prepareStatement(sql);
        /**sql语句预编译*/
        JSONArray l= convertJsonArray(prst.executeQuery());
        /**执行sql语句，收到查询结果，并将结果集ResultSet转为List*/
        prst.close();
        /**关闭 预编译字段*/
        return l;
    }
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
        JdbcDriver driver=new JdbcDriver();
        driver.init();
        Connection conn=driver.getconnection();
        String sql="select * from User";
        try{
            JSONArray jsonArray=getResultJson(conn,sql);
            System.out.println(jsonArray.toString());
            String string=jsonArray.toString();
            JSONArray jsonArray1=JSONArray.fromObject(string);
            System.out.println(jsonArray1.getJSONObject(1).get("UserName"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
