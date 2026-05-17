package com.bank.beijing.beijingmgr.common.pack;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 北京业务数据包
 */
@Data
public class BeijingPack implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 操作类型
     */
    private String action;

    /**
     * 数据列表
     */
    private List<Object> dataList;

    /**
     * 查询条件
     */
    private Object queryParams;
}
