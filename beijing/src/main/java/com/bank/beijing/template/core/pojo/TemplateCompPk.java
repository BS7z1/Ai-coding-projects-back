package com.bank.beijing.template.core.pojo;

import com.bank.common.annotation.PrimaryKeyMark;
import com.bank.common.annotation.Remark;
import com.bank.core.pojo.CompositeBean;

/**
 * 数据库表明：template_Comp_pk
 */

public class TemplateCompPk extends CompositeBean<PKTemplateCompPk> implements java.io.Serializable {

    @Remark("流水号")
    @PrimaryKeyMark("TSK_ID")
    private String tskId;
    @Remark("借据号")
    @PrimaryKeyMark("LOAN_ID")
    private String loanId;
    @Remark("公司名称")
    private String companyName;
    @Remark("企业统一信用代码")
    private String creditCode;
    @Remark("借据金额（元）")
    private java.math.BigDecimal amount;
    @Remark("利率（%）")
    private java.math.BigDecimal interestRate;
    @Remark("是否有效：0否 1是")
    private String isValid;
    @Remark("用户工号")
    private String empNo;
    @Remark("用户名称")
    private String empNm;
    @Remark("起始时间")
    private java.sql.Date startTm;
    @Remark("更新时间")
    private java.sql.Timestamp updatedTm;

    // 默认构造方法
    public TemplateCompPk(){ super(); }

    // getter and setter
    public String getTskId() {
        return tskId;
    }

    public void setTskId(String tskId) {
        this.tskId = tskId;
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
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

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    public String getEmpNo() {
        return empNo;
    }

    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }

    public String getEmpNm() {
        return empNm;
    }

    public void setEmpNm(String empNm) {
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
