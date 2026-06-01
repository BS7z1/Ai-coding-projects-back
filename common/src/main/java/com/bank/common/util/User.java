package com.bank.common.util;

import java.io.Serializable;

/**
 * 登录用户信息
 * 与 AssoUtil.getUser()、AssoUtil.getEmpNo()、AssoUtil.getLoginNo() 对应
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 员工号，对应 AssoUtil.getEmpNo() */
    private String empNo;

    /** 登录名（用户名），对应 AssoUtil.getLoginNo() */
    private String loginNo;

    /** 真实姓名 */
    private String realName;

    /** 所属机构（包含 bankCode / orgNo），对应 AssoUtil.getOrgNo() 中的 user.getOrg() */
    private Org org;

    public User() {}

    public User(String empNo, String loginNo, String realName, Org org) {
        this.empNo = empNo;
        this.loginNo = loginNo;
        this.realName = realName;
        this.org = org;
    }

    public String getEmpNo() {
        return empNo;
    }

    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }

    public String getLoginNo() {
        return loginNo;
    }

    public void setLoginNo(String loginNo) {
        this.loginNo = loginNo;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    @Override
    public String toString() {
        return "User{empNo='" + empNo + "', loginNo='" + loginNo + "', realName='" + realName + "'}";
    }
}
