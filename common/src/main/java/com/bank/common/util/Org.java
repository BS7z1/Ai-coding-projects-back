package com.bank.common.util;

import java.io.Serializable;

/**
 * 机构信息
 * 与 AssoUtil.getDicd()、AssoUtil.getOrgNo() 对应
 */
public class Org implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 银行机构代码（dicd），对应 AssoUtil.getDicd() */
    private String bankCode;

    /** 机构号，对应 AssoUtil.getOrgNo() */
    private String orgNo;

    /** 机构名称 */
    private String orgName;

    public Org() {}

    public Org(String bankCode, String orgNo, String orgName) {
        this.bankCode = bankCode;
        this.orgNo = orgNo;
        this.orgName = orgName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getOrgNo() {
        return orgNo;
    }

    public void setOrgNo(String orgNo) {
        this.orgNo = orgNo;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    @Override
    public String toString() {
        return "Org{bankCode='" + bankCode + "', orgNo='" + orgNo + "', orgName='" + orgName + "'}";
    }
}
