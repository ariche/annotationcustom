package com.lagou.edu.utils;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * @author 应癫
 */
public class DruidUtils {

    private DruidUtils(){
    }

    private static DruidDataSource druidDataSource = new DruidDataSource();


    static {
        druidDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        druidDataSource.setUrl("jdbc:mysql://192.168.213.13:3306/studydemo");
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("password");

    }

    public static DruidDataSource getInstance() {
        return druidDataSource;
    }

}
