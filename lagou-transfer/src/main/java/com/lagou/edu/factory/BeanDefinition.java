package com.lagou.edu.factory;

import java.beans.PropertyDescriptor;
import java.util.Map;

public class BeanDefinition {

    private Class<?> classType;

    private Map<String, PropertyDescriptor> autowiredFieldMap;

    private String beanId;

    private boolean isTrasational;


    public Class<?> getClassType() {
        return classType;
    }

    public void setClassType(Class<?> classType) {
        this.classType = classType;
    }

    public Map<String, PropertyDescriptor> getAutowiredFieldMap() {
        return autowiredFieldMap;
    }

    public void setAutowiredFieldMap(Map<String, PropertyDescriptor> autowiredFieldMap) {
        this.autowiredFieldMap = autowiredFieldMap;
    }

    public String getBeanId() {
        return beanId;
    }

    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }

    public boolean isTrasational() {
        return isTrasational;
    }

    public void setTrasational(boolean trasational) {
        isTrasational = trasational;
    }
}
