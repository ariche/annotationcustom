package com.lagou.edu.servlet;

import com.lagou.edu.factory.BeanFactory;
import com.lagou.edu.service.TransferService;

public class Test {

    public static void main(String[] args) {
        TransferService transferService = (TransferService) BeanFactory.getBean("transferServiceImpl");
        try {
            transferService.transfer("6029621011000","6029621011001",100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
