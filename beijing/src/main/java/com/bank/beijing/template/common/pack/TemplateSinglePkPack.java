package com.bank.beijing.template.common.pack;

import com.bank.beijing.template.core.pojo.TemplateSinglePk;

import java.util.*;

public class TemplateSinglePkPack {

    public static String packTemplateSinglePkQuery(TemplateSinglePk templateSinglePk){
        StringBuffer result = new StringBuffer();

        // 示例：
        // 普通字段，占位符拼接：result.append(" and model.fieldname1 = :fieldname1");
        // 主键字段，占位符拼接：result.append(" and model.primaryKey.fieldname2 = :fieldname2");
        // result.append(" order by model.recordDate desc");

        return result.toString();
    }

    public static Map<String, Object> packTemplateSinglePkQueryParams(TemplateSinglePk templateSinglePk){
        Map<String, Object> params = new HashMap<>();
        // 普通字段，拼接占位符如右：params.put("fieldname1", fieldname1Value);
        return params;
    }
}
