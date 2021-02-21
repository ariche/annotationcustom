package com.lagou.demo.controller;

import com.lagou.demo.service.IDemoService;
import com.lagou.edu.mvcframework.annotations.LagouAutowired;
import com.lagou.edu.mvcframework.annotations.LagouController;
import com.lagou.edu.mvcframework.annotations.LagouRequestMapping;
import com.lagou.edu.mvcframework.annotations.Security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@LagouController
@Security(value = {"张三","李四","王五"})
@LagouRequestMapping("/demo")
public class DemoController {


    @LagouAutowired
    private IDemoService demoService;


    /**
     * URL: /demo/query?name=lisi
     * @param request
     * @param response
     * @param name
     * @return
     */
    @Security(value = {"张三"})
    @LagouRequestMapping("/query")
    public String query(HttpServletRequest request, HttpServletResponse response,String name) {
        return demoService.get(name);
    }


    @Security(value = {"李四"})
    @LagouRequestMapping("/query2")
    public String query2(HttpServletRequest request, HttpServletResponse response,String name) {
        return demoService.get(name+"**");
    }


    @LagouRequestMapping("/query3")
    public String query3(HttpServletRequest request, HttpServletResponse response,String name) {
        return demoService.get(name+"%%");
    }
}
