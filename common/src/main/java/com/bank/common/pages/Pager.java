package com.bank.common.pages;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class Pager {


    private int totalRows; // 总行数
    private int pageSize = 50; // 每页显示行数
    private int currentPage; // 当前页号
    private int totalPages; // 总页数
    private int startRow; // 当前页在数据库中的起始行
    private String pageMethod; // 翻页操作

    private List resultList = new ArrayList();
    public List getResultList() {return resultList;}

    public void setResultList(List resultList) {this.resultList = resultList;}

    public String getPageMethod() {return pageMethod;}

    public void setPageMethod(String pageMethod) {this.pageMethod = pageMethod;}

    public Pager(){}

    public Pager(int _totalRows){
        totalRows = _totalRows;
        totalPages = totalRows/pageSize;
        int mod = totalRows % pageSize;
        if(mod>0){ totalPages++;}
        currentPage=1;
        startRow=0;
    }

    public Pager(int _totalRows, int _pageSize){
        totalRows = _totalRows;
        pageSize = _pageSize;
        totalPages = totalRows/pageSize;
        int mod = totalRows % pageSize;
        if(mod>0){ totalPages++;}
        currentPage=1;
        startRow=0;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public int getStartRow() {
        return startRow;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void first(){
        currentPage = 1;
        startRow = 0;
    }

    public void previous(){
        if(currentPage==1){ return; }
        currentPage--;
        startRow = (currentPage-1)*pageSize;
    }

    public void next(){
        if(currentPage<totalPages){
            currentPage++;
        }
        startRow = (currentPage-1)*pageSize;
    }

    public void last(){
        if(totalPages<=0){
            return;
        }else{
            currentPage=totalPages;
        }
        startRow = (currentPage-1)*pageSize;
    }

    public void refresh(int _currentPage){
        currentPage = _currentPage;
        if(currentPage>totalPages){
            last();
        }
        if(currentPage<1){
            first();
        }
    }

    public void go(){
        if(currentPage>totalPages){
            last();
        }else{
            startRow = (currentPage-1)*pageSize;
        }
    }

    @JsonIgnore
    public boolean isFirst(){
        if(currentPage!=1){
            return false;
        }else{
            return true;
        }
    }

    @JsonIgnore
    public boolean isLast(){
        if(currentPage!=totalPages){
            return false;
        }else{
            return true;
        }
    }
}
