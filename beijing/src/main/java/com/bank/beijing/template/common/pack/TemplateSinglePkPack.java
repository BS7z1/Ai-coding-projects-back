package com.bank.beijing.template.common.pack;

import com.bank.beijing.template.core.pojo.TemplateSinglePk;

import java.util.*;

public class TemplateSinglePkPack {

    public static String packTemplateSinglePkQuery(TemplateSinglePk templateSinglePk){
        StringBuffer result = new StringBuffer();
        if (templateSinglePk == null) {
            return result.toString();
        }

        // 主键字段 - 流水号（通过 model.primaryKey 访问）
        if (isNotBlank(templateSinglePk.getPrimaryKey())) {
            result.append(" and model.primaryKey = :tskId");
        }
        // 借据号
        if (isNotBlank(templateSinglePk.getLoanId())) {
            result.append(" and model.loanId = :loanId");
        }
        // 公司名称
        if (isNotBlank(templateSinglePk.getCompanyName())) {
            result.append(" and model.companyName = :companyName");
        }
        // 企业统一信用代码
        if (isNotBlank(templateSinglePk.getCreditCode())) {
            result.append(" and model.creditCode = :creditCode");
        }
        // 借据金额（元）
        if (templateSinglePk.getAmount() != null) {
            result.append(" and model.amount = :amount");
        }
        // 利率（%）
        if (templateSinglePk.getInterestRate() != null) {
            result.append(" and model.interestRate = :interestRate");
        }
        // 是否有效：0否 1是
        if (isNotBlank(templateSinglePk.getIsValid())) {
            result.append(" and model.isValid = :isValid");
        }
        // 用户工号
        if (isNotBlank(templateSinglePk.getEmpNo())) {
            result.append(" and model.empNo = :empNo");
        }
        // 用户名称
        if (isNotBlank(templateSinglePk.getEmpNm())) {
            result.append(" and model.empNm = :empNm");
        }
        // 起始时间
        if (templateSinglePk.getStartTm() != null) {
            result.append(" and model.startTm = :startTm");
        }
        // 更新时间
        if (templateSinglePk.getUpdatedTm() != null) {
            result.append(" and model.updatedTm = :updatedTm");
        }

        // 默认按创建时间倒序
        result.append(" order by model.updatedTm desc");
        return result.toString();
    }

    public static Map<String, Object> packTemplateSinglePkQueryParams(TemplateSinglePk templateSinglePk){
        Map<String, Object> params = new HashMap<>();
        if (templateSinglePk == null) {
            return params;
        }
        if (isNotBlank(templateSinglePk.getPrimaryKey())) {
            params.put("tskId", templateSinglePk.getPrimaryKey().trim());
        }
        if (isNotBlank(templateSinglePk.getLoanId())) {
            params.put("loanId", templateSinglePk.getLoanId().trim());
        }
        if (isNotBlank(templateSinglePk.getCompanyName())) {
            params.put("companyName", templateSinglePk.getCompanyName().trim());
        }
        if (isNotBlank(templateSinglePk.getCreditCode())) {
            params.put("creditCode", templateSinglePk.getCreditCode().trim());
        }
        if (templateSinglePk.getAmount() != null) {
            params.put("amount", templateSinglePk.getAmount());
        }
        if (templateSinglePk.getInterestRate() != null) {
            params.put("interestRate", templateSinglePk.getInterestRate());
        }
        if (isNotBlank(templateSinglePk.getIsValid())) {
            params.put("isValid", templateSinglePk.getIsValid().trim());
        }
        if (isNotBlank(templateSinglePk.getEmpNo())) {
            params.put("empNo", templateSinglePk.getEmpNo().trim());
        }
        if (isNotBlank(templateSinglePk.getEmpNm())) {
            params.put("empNm", templateSinglePk.getEmpNm().trim());
        }
        if (templateSinglePk.getStartTm() != null) {
            params.put("startTm", templateSinglePk.getStartTm());
        }
        if (templateSinglePk.getUpdatedTm() != null) {
            params.put("updatedTm", templateSinglePk.getUpdatedTm());
        }

        return params;
    }

    private static boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
