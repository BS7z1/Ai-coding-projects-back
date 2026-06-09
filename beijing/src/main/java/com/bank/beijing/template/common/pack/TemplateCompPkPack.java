package com.bank.beijing.template.common.pack;

import com.bank.beijing.template.core.pojo.TemplateCompPk;

import java.util.HashMap;
import java.util.Map;

public class TemplateCompPkPack {

    public static String packTemplateCompPkQuery(TemplateCompPk templateCompPk){
        StringBuffer result = new StringBuffer();
        if (templateCompPk == null) {
            return result.toString();
        }

        // 主键字段 - 流水号（通过 model.primaryKey 访问）
        if (isNotBlank(templateCompPk.getTskId())) {
            result.append(" and model.primaryKey.tskId = :tskId");
        }
        // 借据号
        if (isNotBlank(templateCompPk.getLoanId())) {
            result.append(" and model.primaryKey.loanId = :loanId");
        }
        // 公司名称
        if (isNotBlank(templateCompPk.getCompanyName())) {
            result.append(" and model.companyName = :companyName");
        }
        // 企业统一信用代码
        if (isNotBlank(templateCompPk.getCreditCode())) {
            result.append(" and model.creditCode = :creditCode");
        }
        // 借据金额（元）
        if (templateCompPk.getAmount() != null) {
            result.append(" and model.amount = :amount");
        }
        // 利率（%）
        if (templateCompPk.getInterestRate() != null) {
            result.append(" and model.interestRate = :interestRate");
        }
        // 是否有效：0否 1是
        if (isNotBlank(templateCompPk.getIsValid())) {
            result.append(" and model.isValid = :isValid");
        }
        // 用户工号
        if (isNotBlank(templateCompPk.getEmpNo())) {
            result.append(" and model.empNo = :empNo");
        }
        // 用户名称
        if (isNotBlank(templateCompPk.getEmpNm())) {
            result.append(" and model.empNm = :empNm");
        }
        // 起始时间
        if (templateCompPk.getStartTm() != null) {
            result.append(" and model.startTm = :startTm");
        }
        // 更新时间
        if (templateCompPk.getUpdatedTm() != null) {
            result.append(" and model.updatedTm = :updatedTm");
        }

        // 默认按创建时间倒序
        result.append(" order by model.updatedTm desc");
        return result.toString();
    }

    public static Map<String, Object> packTemplateCompPkQueryParams(TemplateCompPk templateCompPk){
        Map<String, Object> params = new HashMap<>();
        if (templateCompPk == null) {
            return params;
        }
        if (isNotBlank(templateCompPk.getTskId())) {
            params.put("tskId", templateCompPk.getTskId().trim());
        }
        if (isNotBlank(templateCompPk.getLoanId())) {
            params.put("loanId", templateCompPk.getLoanId().trim());
        }
        if (isNotBlank(templateCompPk.getCompanyName())) {
            params.put("companyName", templateCompPk.getCompanyName().trim());
        }
        if (isNotBlank(templateCompPk.getCreditCode())) {
            params.put("creditCode", templateCompPk.getCreditCode().trim());
        }
        if (templateCompPk.getAmount() != null) {
            params.put("amount", templateCompPk.getAmount());
        }
        if (templateCompPk.getInterestRate() != null) {
            params.put("interestRate", templateCompPk.getInterestRate());
        }
        if (isNotBlank(templateCompPk.getIsValid())) {
            params.put("isValid", templateCompPk.getIsValid().trim());
        }
        if (isNotBlank(templateCompPk.getEmpNo())) {
            params.put("empNo", templateCompPk.getEmpNo().trim());
        }
        if (isNotBlank(templateCompPk.getEmpNm())) {
            params.put("empNm", templateCompPk.getEmpNm().trim());
        }
        if (templateCompPk.getStartTm() != null) {
            params.put("startTm", templateCompPk.getStartTm());
        }
        if (templateCompPk.getUpdatedTm() != null) {
            params.put("updatedTm", templateCompPk.getUpdatedTm());
        }

        return params;
    }

    private static boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
