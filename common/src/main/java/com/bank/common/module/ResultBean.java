package com.bank.common.module;

import com.bank.common.pages.Pager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResultBean implements Serializable {

    private static final long serialVersionUID = 20260518191600L;
    // true查询成功， false查询失败
    private boolean success;

    // 封装给客户看的信息
    private String message;

    private List resultList;

    private Pager pager;

    public Pager getPager() {
        return pager;
    }

    public void setPager(Pager pager) {
        this.pager = pager;
    }

    public List getResultList() {
        return resultList;
    }

    public void setResultList(List resultList) {
        this.resultList = resultList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ResultBean(){ super();}

    // 有 resultList/success/pager 方法
    public ResultBean(boolean success, List resultList, Pager pager){
        super();
        this.pager = pager;
        this.resultList = resultList;
        this.success = success;
    }

    // 有 resultList/message/success/pager 方法
    public ResultBean(boolean success, String message, List resultList, Pager pager){
        super();
        this.pager = pager;
        this.resultList = resultList;
        this.success = success;
        this.message = message;
    }

    // 有 resultList/success 方法
    public ResultBean(boolean success, List resultList){
        super();
        this.resultList = resultList;
        this.success = success;
    }

    // 有 success/message 方法
    public ResultBean(boolean success, String message){
        super();
        this.success = success;
        this.message = message;
    }

    // 有 resultList/success/pager 方法, 但是 ArrayList
    public ResultBean(boolean success, String message, ArrayList resultList){
        super();
        this.pager = pager;
        this.resultList = resultList;
        this.success = success;
    }

    // 有 resultList/message/success/pager 方法，但是 ArrayList
    public ResultBean(boolean success, String message, ArrayList resultList, Pager pager){
        super();
        this.pager = pager;
        this.resultList = resultList;
        this.success = success;
        this.message = message;
    }
}
