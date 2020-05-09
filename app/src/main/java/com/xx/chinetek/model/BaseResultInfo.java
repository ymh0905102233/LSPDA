package com.xx.chinetek.model;

/**
 * @ Des: 常用的解析结果返回类
 * @ Created by yangyiqing on 2019/8/2.
 */
public class BaseResultInfo<K,V> {
    private K      headerStatus;
    private V      info;
    private String message;


    public BaseResultInfo(K headerStatus){
        this.headerStatus=headerStatus;
        message="";
    }
    public BaseResultInfo(){
        message="";
    }
    public K getHeaderStatus() {
        return headerStatus;
    }

    public void setHeaderStatus(K headerStatus) {
        this.headerStatus = headerStatus;
    }

    public V getInfo() {
        return info;
    }

    public void setInfo(V info) {
        this.info = info;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
