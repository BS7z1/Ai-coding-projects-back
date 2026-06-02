package com.bank.beijing.template.core.pojo;

import com.bank.common.annotation.PrimaryKeyMark;
import com.bank.common.annotation.Remark;
import com.bank.core.pojo.SingleBean;

/**
 * 数据库表明：template_single_pk
 */

public class TemplateSinglePk extends SingleBean<java.lang.String> implements java.io.Serializable {

    @Remark("流水号")
    @PrimaryKeyMark("TSK_ID")
    private java.lang.String tskId;
    @Remark("借据号")
    private java.lang.String loanId;
    @Remark("公司名称")
    private java.lang.String companyName;
    @Remark("企业统一信用代码")
    private java.lang.String creditCode;
    @Remark("借据金额（元）")
    private java.math.BigDecimal amount;
    @Remark("利率（%）")
    private java.math.BigDecimal interestRate;
    @Remark("是否有效：0否 1是")
    private java.lang.String isValid;
    @Remark("用户工号")
    private java.lang.String empNo;
    @Remark("用户名称")
    private java.lang.String empNm;
    @Remark("起始时间")
    private java.sql.Date startTm;
    @Remark("更新时间")
    private java.sql.Timestamp updatedTm;

    // 默认构造方法
    public TemplateSinglePk(){ super(); }

    // getter and setter
    public java.lang.String getTskId() {
        return tskId;
    }

    public void setTskId(java.lang.String tskId) {
        this.tskId = tskId;
    }

    public java.lang.String getLoanId() {
        return loanId;
    }

    public void setLoanId(java.lang.String loanId) {
        this.loanId = loanId;
    }

    public java.lang.String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(java.lang.String companyName) {
        this.companyName = companyName;
    }

    public java.lang.String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(java.lang.String creditCode) {
        this.creditCode = creditCode;
    }

    public java.math.BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(java.math.BigDecimal amount) {
        this.amount = amount;
    }

    public java.math.BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(java.math.BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public java.lang.String getIsValid() {
        return isValid;
    }

    public void setIsValid(java.lang.String isValid) {
        this.isValid = isValid;
    }

    public java.lang.String getEmpNo() {
        return empNo;
    }

    public void setEmpNo(java.lang.String empNo) {
        this.empNo = empNo;
    }

    public java.lang.String getEmpNm() {
        return empNm;
    }

    public void setEmpNm(java.lang.String empNm) {
        this.empNm = empNm;
    }

    public java.sql.Date getStartTm() {
        return startTm;
    }

    public void setStartTm(java.sql.Date startTm) {
        this.startTm = startTm;
    }

    public java.sql.Timestamp getUpdatedTm() {
        return updatedTm;
    }

    public void setUpdatedTm(java.sql.Timestamp updatedTm) {
        this.updatedTm = updatedTm;
    }
}
