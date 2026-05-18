package com.bank.common.util;

import com.bank.common.module.ResultBean;
import com.bank.common.pages.Pager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class WebUtilWork {
    private static final Logger logger = LoggerFactory.getLogger(WebUtilWork.class);

    /**
     *  ResultBean 封装类
     * @param dbResult
     */
    public static ResultBean WebResultPack(Object dbResult){
        ResultBean resultBean = null;
        if(dbResult instanceof List && dbResult != null){
            if(((List)dbResult).size()>0){
                resultBean = new ResultBean(true, (List) dbResult);
            }else {
                resultBean = new ResultBean(true, "没有相关信息（No records）", new ArrayList());
            }
        }else{
            resultBean  = new ResultBean(true, "操作执行成功（Success）.");
        }
        return resultBean;
    }

    /**
     *  ResultBean 封装类
     * @param dbResult
     * @param pager
     */
    @SuppressWarnings("unchecked")
    public static ResultBean WebResultPack(Object dbResult, Pager pager){
        ResultBean resultBean = null;
        if(dbResult instanceof List && dbResult != null){
            if(((List)dbResult).size()>0){
                resultBean = new ResultBean(true, (List) dbResult, pager);
            }else {
                resultBean = new ResultBean(true, "没有相关信息（No records）", new ArrayList(), pager);
            }
        }else{
            resultBean  = new ResultBean(true, "操作执行成功（Success）.");
        }
        return resultBean;
    }

    /**
     *  ResultBean 封装类
     * @param obj
     * @param success
     * @param message
     */
    public static ResultBean WebResultPack(boolean success, String message, Object obj){
        ResultBean resultBean = null;
        if(obj != null){
            ArrayList list = new ArrayList();
            list.add(obj);
            resultBean = new ResultBean(success, message, list);
        }else{
            resultBean  = new ResultBean(success, message);
        }
        return resultBean;
    }

}
