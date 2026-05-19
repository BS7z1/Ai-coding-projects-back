package com.bank.common.pages;

import java.util.List;

public class PagerHelper {
    protected static Pager getPager(String pageSize, String currentPage, int totalRows, String pageMethod){
        Pager pager = null;

        if(pageSize == null){
            pager = new Pager(totalRows, Integer.parseInt(pageSize));
        }else{
            pager = new Pager(totalRows);
        }

        // 页号为空首次查询，页号不为空则刷新 pager 对象，输入当页页号信息
        if(currentPage!=null){
            pager.refresh(Integer.parseInt(currentPage));
        }

        if(pageMethod!=null){
            if(pageMethod.equals("first")){
                pager.first();
            } else if (pageMethod.equals("previous")) {
                pager.previous();
            } else if (pageMethod.equals("next")) {
                pager.next();
            } else if (pageMethod.equals("last")) {
                pager.last();
            } else{
                pager.go();
            }
        }else {
            pager.go();
        }
        return pager;
    }

    protected static Pager getSessionPager(String pageSize, String currentPage, int totalRows, String pageMethod, List list){
        Pager pager = null;

        if(pageSize == null){
            pager = new Pager(totalRows, Integer.parseInt(pageSize));
        }else{
            pager = new Pager(totalRows);
        }

        pager.setResultList(list);

        // 页号为空首次查询，页号不为空则刷新 pager 对象，输入当页页号信息
        if(currentPage!=null){
            pager.refresh(Integer.parseInt(currentPage));
        }

        if(pageMethod!=null){
            if(pageMethod.equals("first")){
                pager.first();
            } else if (pageMethod.equals("previous")) {
                pager.previous();
            } else if (pageMethod.equals("next")) {
                pager.next();
            } else if (pageMethod.equals("last")) {
                pager.last();
            } else{
                pager.go();
            }
        }else {
            pager.go();
        }
        return pager;
    }

    public static Pager getPager(Pager oldPager, int rowCount){
        Pager newPager = getPager(
                String.valueOf(oldPager.getPageSize()),
                String.valueOf(oldPager.getCurrentPage()),
                rowCount,
                oldPager.getPageMethod()
        );
        return newPager;
    }

    public static Pager getSessionPager(Pager oldPager, int rowCount, List resultList){
        Pager newPager = getSessionPager(
                String.valueOf(oldPager.getPageSize()),
                String.valueOf(oldPager.getCurrentPage()),
                rowCount,
                oldPager.getPageMethod(),
                resultList
        );
        return newPager;
    }
}
