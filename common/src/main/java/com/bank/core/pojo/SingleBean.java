package com.bank.core.pojo;

import com.bank.common.util.UtilWork;

import java.io.Serializable;

public abstract class SingleBean<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private T primaryKey;

    private String recordId;
    private String recordDate;
    private String lastmodiId;
    private String lastmodiDate;
    private String orderPriority;

    public SingleBean(){ super();}

    public T getPrimaryKey() {return primaryKey;}

    public void setPrimaryKey(T id){ this.primaryKey = id;}

    public String getRecordId(){ return recordId;}

    public void setRecordId(String aRecordId){ this.recordId = aRecordId;}

    public String getRecordDate(){ return recordDate;}

    public void setRecordDate(String aRecordDate){ this.recordDate = aRecordDate;}

    public String getLastmodiId(){ return lastmodiId;}

    public void setLastmodiId(String aLastmodiId){ this.lastmodiId = aLastmodiId;}

    public String getLastmodiDate(){ return lastmodiDate;}

    public void setLastmodiDate(String aLastmodiDate){ this.lastmodiDate = aLastmodiDate;}

    public String getOrderPriority(){ return orderPriority;}

    public void setOrderPriority(String aOrderPriority){ this.orderPriority = aOrderPriority;}

    public void initSave(String empid){
        setRecordId(empid);
        setRecordDate(UtilWork.getNowTime());
        setLastmodiId(empid);
        setLastmodiDate(UtilWork.getNowTime();
    }

    public void initUpdate(String empid){
        setLastmodiId(empid);
        setLastmodiDate(UtilWork.getNowTime());
    }

    @Override
    public int hashCode(){
        return primaryKey == null ? 0 : primaryKey.hashCode();
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(obj == null) return false;
        if(getClass()!=obj.getClass()) return false;
        if(primaryKey == null){
            return false;
        }
        SingleBean<T> bean = (SingleBean<T>) obj;
        return primaryKey.equals(bean.getPrimaryKey());
    }
}
